package com.example.userblinkitclone.Activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.userblinkitclone.Adapters.CartProductsAdapter
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.Constants
import com.example.userblinkitclone.Models.Orders
import com.example.userblinkitclone.R
import com.example.userblinkitclone.Utils.Utils
import com.example.userblinkitclone.databinding.ActivityOrderActiviityBinding
import com.example.userblinkitclone.databinding.AddressLayoutBinding
import com.example.userblinkitclone.viewModels.UserViewModel
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class OrderActiviity : AppCompatActivity() {
    private lateinit var binding:ActivityOrderActiviityBinding
    val  viewModel: UserViewModel by viewModels()
    private lateinit var adapterCartProducts: CartProductsAdapter
    private lateinit var b2BPGRequest : B2BPGRequest
    private var cartListener: CartListener?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOrderActiviityBinding.inflate(layoutInflater)
        setStatusBarColor()
        getAllProducts()
        onBackClicked()
        initializePhonePe()
        onPlaceOrderClicked()
        setContentView(binding.root)
    }

    private fun initializePhonePe() {
        val data=JSONObject()
        PhonePe.init(this, PhonePeEnvironment.UAT, Constants.MERCHAT_ID, "")
        data.put("merchantId",Constants.MERCHAT_ID)
        data.put("merchantTransactionId",Constants.merchantTransactionId)
        data.put("amount",500)
        data.put("mobileNumber",99889888988)
        data.put("callbackUrl","https://webhook.site/callback-url")

        val paymentInstrument=JSONObject()
        paymentInstrument.put("type","UPI_INTENT")
        paymentInstrument.put("targetApp", "com.phonepe.simulator")

        data.put("paymentInstrument",paymentInstrument)

        val deviceContext=JSONObject()
        deviceContext.put("deviceContext","")
        data.put("deviceContext",deviceContext)

        val payloadBase64=android.util.Base64.encodeToString(
            data.toString().toByteArray(Charset.defaultCharset()),android.util.Base64.NO_WRAP
        )

        val checkSum=sha256(payloadBase64+Constants.apiEndPoint+Constants.SALT_KEY)+"###1"

        b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checkSum)
            .setUrl(Constants.apiEndPoint)
            .build()
    }


    private fun sha256(input : String):String{
        val bytes=input.toByteArray(Charsets.UTF_8)
        val md=MessageDigest.getInstance("SHA-256")
        val digest=md.digest(bytes)
        return digest.fold(""){ str, it -> str + "%02x".format(it)}
    }

    private fun onPlaceOrderClicked() {
        binding.btnNext.setOnClickListener {
            viewModel.fetchAddress().observe(this){
                if (it){
                    //payment
                    getPaymentView()
                }else{
                    val addressLayoutBinding=AddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alterDialog=AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alterDialog.show()
                    addressLayoutBinding.Add.setOnClickListener { saveAddress(alterDialog,addressLayoutBinding) }
                }
            }
        }
    }

    val phonePeView=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== RESULT_OK){
            checkStatus()
        }
    }

    private fun checkStatus() {
        val xverify = sha256("/pg/v1/status/${Constants.MERCHAT_ID}/${Constants.merchantTransactionId}" + Constants.SALT_KEY )+ "###1"
        val headers= mapOf(
            "Content-type" to "application/json",
            "X-VERIFY" to xverify,
            "X-MERCHANT-ID" to Constants.MERCHAT_ID
        )
        lifecycleScope.launch {
            viewModel.checkPaymentStatus(headers)
            viewModel.paymentStatus.collect{
                if (it){
                    Utils.Toast(this@OrderActiviity,"Payment Done Successfully")
                    //save order
                    saveProduct()
                    //delete product
                    lifecycleScope.launch { viewModel.deleteAllCartProducts() }
                    viewModel.savingCartItemCount(0)
                    //hide cart layout
                    cartListener?.hideCartLayout()
                    startActivity(Intent(this@OrderActiviity,UsersMainActivity::class.java))
                    finish()
                }else{
                    Utils.Toast(this@OrderActiviity,"Payment Failed")
                }
            }
        }
    }


    private fun saveProduct() {
        viewModel.getAll().observe(this){cartProductList->
            if (cartProductList.isNotEmpty()) {
                viewModel.getUserAddress { address ->
                    val orders = Orders(
                        orderId = Utils.getRandomUid(),
                        orderList = cartProductList,
                        userAddress = address,
                        orderStatus = 0,
                        orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUid()
                    )
                    viewModel.saveOrderedProducts(orders)
                    for (products in cartProductList) {
                        val count = products.productCount
                        val stock = products.productStock?.minus(count!!)
                        viewModel.saveStockAfterOrdering(stock!!, products)
                    }
                }
            }
        }
    }

    private fun getPaymentView() {
        try {
            PhonePe.getImplicitIntent(this,b2BPGRequest,"com.phonepe.simulator")
                .let {
                    phonePeView.launch(it)
                }

        }catch (e:PhonePeInitException){
            Utils.Toast(this,e.message.toString())
        }
    }

    private fun saveAddress(alterDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        com.example.userblinkitclone.Utils.Utils.showDialog(this,"Processing")
        val userPinCode=addressLayoutBinding.pincode.text.toString()
        val userPhone=addressLayoutBinding.phoneNo.text.toString()
        val userState=addressLayoutBinding.state.text.toString()
        val userDistrict=addressLayoutBinding.district.text.toString()
        val userAddress=addressLayoutBinding.address.text.toString()

        val address="$userPinCode, $userDistrict($userState), $userAddress, $userPhone"


        lifecycleScope.launch {
            viewModel.saveAddress(address)
            viewModel.saveAddressStatus()
        }
        com.example.userblinkitclone.Utils.Utils.Toast(this,"Address Saved")
        Utils.hideDialog()
        alterDialog.dismiss()

    }

    private fun onBackClicked() {
        binding.toolbar2.setNavigationOnClickListener {
            startActivity(Intent(this,UsersMainActivity::class.java))
        }
    }

    private fun getAllProducts() {
        viewModel.getAll().observe(this){
           adapterCartProducts=CartProductsAdapter()
            binding.RVCartProduct.adapter=adapterCartProducts
            adapterCartProducts.differ.submitList(it)

            var GrandTotal=0;
            var subTotal=0;
            for(products in it){
                val price: String =products.productPrice!!.substring(1)
                subTotal+=(price.toString().toInt()* products.productCount!!)
            }
            GrandTotal=subTotal
            if (subTotal<200){
                binding.deliveryCharges.text="₹15"
                GrandTotal+=15
            }
            binding.grandTotal.text=GrandTotal.toString()
            binding.subtotal.text=subTotal.toString()
        }
    }
    private fun setStatusBarColor() {
        window?.apply {
            val statusBarColors= ContextCompat.getColor(applicationContext,R.color.yellow)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}