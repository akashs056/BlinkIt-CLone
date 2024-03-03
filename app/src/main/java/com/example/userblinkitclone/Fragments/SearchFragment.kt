package com.example.userblinkitclone.Fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Adapters.ProductAdapter
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.Models.Product
import com.example.userblinkitclone.R
import com.example.userblinkitclone.Utils.Utils
import com.example.userblinkitclone.databinding.FragmentSearchBinding
import com.example.userblinkitclone.databinding.SampleProductsBinding
import com.example.userblinkitclone.roomdb.cartProducts
import com.example.userblinkitclone.viewModels.UserViewModel
import kotlinx.coroutines.launch
import java.lang.ClassCastException


class SearchFragment : Fragment() {
    val  viewModel:UserViewModel by viewModels()
    private lateinit var binding:FragmentSearchBinding
    private lateinit var adapterProduct:ProductAdapter
    private var cartListener: CartListener?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentSearchBinding.inflate(layoutInflater)
        binding.searchRv.requestFocus()
        searchFunction()
        getAllProducts()
        onBackClicked()
        return binding.root
    }

    private fun onBackClicked() {
        binding.goBackHome.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
        }
    }

    private fun searchFunction() {
        binding.searchRv.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query=s.toString().trim()
                adapterProduct.filter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun getAllProducts() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts("All").collect {
                if (it.isEmpty()) {
                    binding.productsRv.visibility = View.GONE
                } else {
                    binding.productsRv.visibility = View.VISIBLE
                }
                adapterProduct = ProductAdapter(
                    ::OnAddBtnClicked,
                    ::onIncrementBtnCLicked,
                    ::onDecrementBtnCLicked
                )
                binding.productsRv.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = ArrayList(it)
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CartListener){
            cartListener=context
        }else{
            throw  ClassCastException("Please implement cartListener")
        }
    }

    fun OnAddBtnClicked(product: Product, productsBinding: SampleProductsBinding){
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
        val cartProducts= cartProducts(
            productRandomId = product.productRandomId,
            productTitle = product.productTitle,
            productQuantity = product.productQuantity.toString()+product.productTUnit.toString(),
            productPrice = "₹"+"${product.productPrice}",
            productCount = product.itemCount,
            productStock = product.productStock,
            image = product.productImageUris?.get(0),
            productCategory = product.productCategory,
            adminUid = product.adminUid,
            productType = product.productType
        )
        lifecycleScope.launch { viewModel.insertCartProduct(cartProducts)}
    }



}