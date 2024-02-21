package com.example.userblinkitclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Adapters.ProductAdapter
import com.example.userblinkitclone.databinding.FragmentCategroyBinding
import com.example.userblinkitclone.viewModels.UserViewModel
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class CategroyFragment : Fragment() {
    private lateinit var binding:FragmentCategroyBinding
    var categoryName: String? =null
    private lateinit var adapterProduct :ProductAdapter
    private val viewModel : UserViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCategroyBinding.inflate(layoutInflater)
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
                adapterProduct=ProductAdapter()
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

}