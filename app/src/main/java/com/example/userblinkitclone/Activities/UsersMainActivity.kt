package com.example.userblinkitclone.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.userblinkitclone.CartListener
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.ActivityUsersMainBinding
import com.example.userblinkitclone.viewModels.UserViewModel

class UsersMainActivity : AppCompatActivity() ,CartListener{
    private lateinit var binding:ActivityUsersMainBinding
    val  viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUsersMainBinding.inflate(layoutInflater)
        getTotalItemCountInCart()
        setContentView(binding.root)
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