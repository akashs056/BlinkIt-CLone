package com.example.userblinkitclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Adapters.CartProductsAdapter
import com.example.userblinkitclone.databinding.FragmentOrderDetailBinding
import com.example.userblinkitclone.viewModels.UserViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.log


class OrderDetailFragment : Fragment() {
    private lateinit var binding:FragmentOrderDetailBinding
    private lateinit var adapter:CartProductsAdapter
    private val viewModel : UserViewModel by viewModels()
    private var status=0;
    private var orderId=""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOrderDetailBinding.inflate(inflater,container,false)
        getValues()
        settingStatus()
        onBackClicked()
        lifecycleScope.launch {
            getOrderedProducts()
        }
         return binding.root
    }

    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_ordersFragment)
        }
    }

    private suspend fun getOrderedProducts() {
        viewModel.getOrderedProducts(orderId).collect{cartList->
            adapter=CartProductsAdapter()
            binding.RVorderedItem.adapter=adapter
            adapter.differ.submitList(cartList)
        }
    }

    private fun settingStatus() {
        val statusToView = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1,binding.iv2,binding.view1),
            2 to listOf(binding.iv1,binding.iv2,binding.iv3,binding.view1,binding.view2),
            3 to listOf(binding.iv1,binding.iv2,binding.iv3,binding.iv4,binding.view1,binding.view2,binding.view3)
        )
        val viewToInt=statusToView.getOrDefault(status, emptyList())
        for (views in viewToInt){
            views.backgroundTintList=ContextCompat.getColorStateList(requireContext(),R.color.blue)
        }
    }

    private fun getValues() {
        val bundles=arguments
        status=bundles!!.getInt("status")
        orderId= bundles.getString("orderId").toString()
    }

}