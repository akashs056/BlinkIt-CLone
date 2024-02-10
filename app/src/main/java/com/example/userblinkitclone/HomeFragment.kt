package com.example.userblinkitclone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.userblinkitclone.Adapters.CategoryAdapter
import com.example.userblinkitclone.Models.Category
import com.example.userblinkitclone.databinding.FragmentHomeBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(layoutInflater)
        setAllCategories()
        return binding.root
    }

    private fun setAllCategories() {
        val categoryList =ArrayList<Category>()
        for (i in 0 until Constants.allProductsCategory.size){
            categoryList.add(Category(Constants.allProductsCategory[i],Constants.allProductsCategoryIcon[i]))
        }
        binding.rvCategories.adapter=CategoryAdapter(categoryList)
    }

}