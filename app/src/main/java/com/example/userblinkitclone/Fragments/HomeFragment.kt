package com.example.userblinkitclone.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Adapters.CategoryAdapter
import com.example.userblinkitclone.Constants
import com.example.userblinkitclone.Models.Category
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.FragmentHomeBinding
import com.example.userblinkitclone.viewModels.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private val viewModel : UserViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding=FragmentHomeBinding.inflate(layoutInflater)
        setAllCategories()
        setStatusBarColor()
        onSearchClicked()
        onProfileClicked()
        return binding.root
    }

    private fun onProfileClicked() {
        binding.Profile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profuileFragment)
        }
    }


    private fun onSearchClicked() {
        binding.searchEt.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
        val categoryList =ArrayList<Category>()
        for (i in 0 until Constants.allProductsCategory.size){
            categoryList.add(Category(
                Constants.allProductsCategory[i],
                Constants.allProductsCategoryIcon[i]))
        }
        binding.rvCategories.adapter=CategoryAdapter(categoryList,::onCategoryClicked)
    }
    private fun onCategoryClicked(category: Category){
        val bundle =Bundle()
        bundle.putString("categoryName",category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categroyFragment,bundle)
    }
    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors= ContextCompat.getColor(requireContext(), R.color.orange)
            statusBarColor=statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility=View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

}