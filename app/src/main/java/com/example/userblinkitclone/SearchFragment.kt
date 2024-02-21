package com.example.userblinkitclone

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Adapters.ProductAdapter
import com.example.userblinkitclone.databinding.FragmentSearchBinding
import com.example.userblinkitclone.viewModels.UserViewModel
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
    val  viewModel:UserViewModel by viewModels()
    private lateinit var binding:FragmentSearchBinding
    private lateinit var adapterProduct:ProductAdapter
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
            findNavController().navigate(com.example.userblinkitclone.R.id.action_searchFragment_to_homeFragment)
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
                adapterProduct = ProductAdapter()
                binding.productsRv.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = ArrayList(it)
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }

    }

}