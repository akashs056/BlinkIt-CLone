package com.example.userblinkitclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.databinding.FragmentProfuileBinding

class ProfuileFragment : Fragment() {
    private lateinit var binding:FragmentProfuileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfuileBinding.inflate(layoutInflater)
        onBackButtonClicked()
        onOrderClicked()
        return binding.root
    }

    private fun onOrderClicked() {
        binding.yourOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profuileFragment_to_ordersFragment)
        }
    }

    private fun onBackButtonClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_profuileFragment_to_homeFragment)
        }
    }

}