package com.example.userblinkitclone.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.userblinkitclone.Adapters.CartProductsAdapter
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.ActivityUsersMainBinding
import com.example.userblinkitclone.databinding.BdCartProductsBinding
import com.example.userblinkitclone.roomdb.cartProducts
import com.example.userblinkitclone.viewModels.UserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class UsersMainActivity : AppCompatActivity() ,CartListener{
    private lateinit var binding:ActivityUsersMainBinding
    val  viewModel: UserViewModel by viewModels()
    private lateinit var cartProducts : List<cartProducts>
    private lateinit var adapterCartProducts:CartProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUsersMainBinding.inflate(layoutInflater)
        getAllCartProducts()
        getTotalItemCountInCart()
        onCartClicked()
        onNextBtnClicked()
        setContentView(binding.root)
    }

    private fun onNextBtnClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this,OrderActiviity::class.java))
        }
    }

    private fun getAllCartProducts() {
            viewModel.getAll().observe(this){
                    cartProducts=it
            }
    }

    private fun onCartClicked() {
        binding.llitemCart.setOnClickListener {
            val bsCartProductsBinding = BdCartProductsBinding.inflate(LayoutInflater.from(this))
            val bs=BottomSheetDialog(this)
            bs.setContentView(bsCartProductsBinding.root)

            adapterCartProducts= CartProductsAdapter()
            bsCartProductsBinding.NumberOfProductCount.text=binding.NumberOfProductCount.text
            bsCartProductsBinding.RVcartProducts.adapter=adapterCartProducts
            adapterCartProducts.differ.submitList(cartProducts)
            bsCartProductsBinding.btnNext.setOnClickListener {
                startActivity(Intent(this,OrderActiviity::class.java))
            }
            bs.show()
        }
    }

    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalcartItemCount().observe(this){
            if (it>0){
                binding.llcart.visibility=View.VISIBLE
                binding.NumberOfProductCount.text=it.toString()
            }else{
                binding.llcart.visibility=View.GONE
            }
        }
    }

    override fun showCartLayout(itemCount : Int) {
        val previousCount= binding.NumberOfProductCount.text.toString().toInt()
        val updatedCount=previousCount+itemCount

        if (updatedCount>0){
            binding.llcart.visibility=View.VISIBLE
            binding.NumberOfProductCount.text=updatedCount.toString()
        }else{
            binding.llcart.visibility=View.GONE
            binding.NumberOfProductCount.text="0"
        }
    }

    override fun saveSharedPref(itemCount: Int) {
        viewModel.fetchTotalcartItemCount().observe(this){
            viewModel.savingCartItemCount(it+itemCount)
        }
    }
}