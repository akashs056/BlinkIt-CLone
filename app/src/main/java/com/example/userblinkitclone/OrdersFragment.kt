package com.example.userblinkitclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Adapters.OrdersAdapter
import com.example.userblinkitclone.Models.OrderedItems
import com.example.userblinkitclone.databinding.FragmentOrdersBinding
import com.example.userblinkitclone.viewModels.UserViewModel
import kotlinx.coroutines.launch
import java.lang.StringBuilder


class OrdersFragment : Fragment() {
    private val viewModel:UserViewModel by viewModels()
    private lateinit var binding:FragmentOrdersBinding
    private lateinit var adapter:OrdersAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOrdersBinding.inflate(inflater,container,false)
        onBackClicked()
        getAllOrders()
        return binding.root
    }

    private fun getAllOrders() {
        lifecycleScope.launch {
            viewModel.getAllOrders().collect{orderList->
                if (orderList.isNotEmpty()){
                    val orderedList=ArrayList<OrderedItems>()
                    for(orders in orderList){
                        val title=StringBuilder()
                        var totalPrice=0;
                        for(products in orders.orderList!!){
                            val price=products.productPrice?.substring(1)?.toInt()
                            val itemCount=products.productCount!!
                            totalPrice+=(price?.times(itemCount)!!)

                            title.append("${products.productCategory}, ")
                        }
                        val orderedItem=OrderedItems(
                            orders,orders.orderDate,orders.orderStatus,title.toString(),totalPrice
                        )
                        orderedList.add(orderedItem)
                    }
                    adapter=OrdersAdapter(requireContext())
                    binding.RVyourOrders.adapter=adapter
                    adapter.differ.submitList(orderedList)
                }
            }
        }
    }

    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_ordersFragment_to_profuileFragment)
        }
    }

}