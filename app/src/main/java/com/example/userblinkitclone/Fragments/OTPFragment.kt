package com.example.userblinkitclone.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.FragmentOTPBinding

class OTPFragment : Fragment() {
    private lateinit var binding: FragmentOTPBinding
    private lateinit var number:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOTPBinding.inflate(layoutInflater)
        getUserNumber()
        customizeEnteringOTP()
        onBackButtonClicked()
        return binding.root
    }

    private fun onBackButtonClicked() {
      binding.toolbar.setNavigationOnClickListener {
          findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
      }
    }

    private fun customizeEnteringOTP() {
        val editTexts = arrayOf(binding.otp1,binding.otp2,binding.otp3,binding.otp4,binding.otp5,binding.otp6)
        for (i in editTexts.indices){
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length==1){
                        if (i<editTexts.size-1) {
                            editTexts[i + 1].requestFocus()
                        }
                    }else{
                        if (i>0) {
                            editTexts[i - 1].requestFocus()
                        }
                    }
                }

            })
        }
    }

    private fun getUserNumber() {
        val bundle=arguments
        number=bundle?.getString("number").toString()
        binding.tvnumber.text=number
    }

}