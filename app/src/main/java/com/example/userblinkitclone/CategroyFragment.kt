package com.example.userblinkitclone

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Adapters.ProductAdapter
import com.example.userblinkitclone.Models.Product
import com.example.userblinkitclone.Utils.Utils
import com.example.userblinkitclone.databinding.FragmentCategroyBinding
import com.example.userblinkitclone.databinding.SampleProductsBinding
import com.example.userblinkitclone.roomdb.cartProducts
import com.example.userblinkitclone.viewModels.UserViewModel
import kotlinx.coroutines.launch
import java.lang.ClassCastException


class CategroyFragment : Fragment() {
    private lateinit var binding:FragmentCategroyBinding
    var categoryName: String? =null
    private lateinit var adapterProduct :ProductAdapter
    private val viewModel : UserViewModel by viewModels()
    private var cartListener:CartListener?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCategroyBinding.inflate(layoutInflater)
        setStatusBarColor()
        setTitle()
        onBackClicked()
        onSearchClicked()
        getCategoryProducts(categoryName)
        return binding.root
    }

    private fun onSearchClicked() {
        binding.toolbar.setOnMenuItemClickListener{menuItem->
            when(menuItem.itemId){
                R.id.searchh->{
                    findNavController().navigate(R.id.action_categroyFragment_to_searchFragment)
                    true
                }

                else -> {false}
            }
        }
    }
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(),R.color.yellow)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categroyFragment_to_homeFragment)
        }
    }

    private fun getCategoryProducts(categoryName: String?) {
        binding.shimmerViewContainer.visibility=View.VISIBLE
        lifecycleScope.launch {
            viewModel.getCategoryProducts(categoryName).collect{
                if (it.isEmpty()){
                    binding.textView2.visibility=View.VISIBLE
                    binding.productsRv.visibility=View.GONE
                }else{
                    binding.textView2.visibility=View.GONE
                    binding.productsRv.visibility=View.VISIBLE
                }
                adapterProduct=ProductAdapter(::OnAddBtnClicked,::onIncrementBtnCLicked,::onDecrementBtnCLicked)
                binding.productsRv.adapter=adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList= ArrayList(it)
                binding.shimmerViewContainer.visibility=View.GONE
            }
        }
    }

    private fun setTitle() {
        val bundle=arguments
        categoryName=bundle?.getString("categoryName")
        binding.toolbar.title=categoryName
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener){
            cartListener=context
        }else{
            throw  ClassCastException("Please implement cartListener")
        }
    }

    fun OnAddBtnClicked(product: Product,productsBinding: SampleProductsBinding){
        productsBinding.add.visibility=View.GONE
        productsBinding.productCount.visibility=View.VISIBLE

        //step 1
        var itemCount=productsBinding.count.text.toString().toInt()
        itemCount++
        productsBinding.count.text=itemCount.toString()

        cartListener?.showCartLayout(1)

        //step 2
        product.itemCount=itemCount
        lifecycleScope.launch {
            cartListener?.saveSharedPref(1)
            saveProductInRoom(product)
            viewModel.addProductToFirebase(product,itemCount)
        }
    }

    private fun onIncrementBtnCLicked(product: Product, productsBinding: SampleProductsBinding){
        var itemCountInc=productsBinding.count.text.toString().toInt()
        if (product.productStock!!>itemCountInc) {
            itemCountInc++
            productsBinding.count.text=itemCountInc.toString()

            cartListener?.showCartLayout(1)
            //stem 2
            product.itemCount=itemCountInc
            lifecycleScope.launch {
                cartListener?.saveSharedPref(1)
                saveProductInRoom(product)
                viewModel.addProductToFirebase(product,itemCountInc)
            }
        }else{
            Utils.Toast(requireContext(),"Cannot added more of this")
        }


    }

    private fun onDecrementBtnCLicked(product: Product, productsBinding: SampleProductsBinding){
        var itemCountDec=productsBinding.count.text.toString().toInt()
        itemCountDec--

        //step 2
        product.itemCount=itemCountDec
        lifecycleScope.launch {
            cartListener?.saveSharedPref(-1)
            saveProductInRoom(product)
            viewModel.addProductToFirebase(product,itemCountDec)
        }

        if (itemCountDec>0){
            productsBinding.count.text=itemCountDec.toString()
        }else {
            lifecycleScope.launch{
                viewModel.deleteCartProduct(product.productRandomId)
            }
            productsBinding.add.visibility=View.VISIBLE
            productsBinding.productCount.visibility=View.GONE
            productsBinding.count.text="0"
        }

        cartListener?.showCartLayout(-1)
    }
    private  fun saveProductInRoom(product: Product) {
        val cartProducts=cartProducts(
            productRandomId = product.productRandomId,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString()+product.productTUnit.toString(),
            productPrice = "₹"+"${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            image = product.productImageUris?.get(0),
            productCategory = product.productCategory,
            adminUid = product.adminUid
        )
        lifecycleScope.launch { viewModel.insertCartProduct(cartProducts)}
    }

}