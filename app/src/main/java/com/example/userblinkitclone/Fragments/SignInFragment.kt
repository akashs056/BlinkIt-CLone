package com.example.userblinkitclone.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSignInBinding.inflate(layoutInflater)

        onContinueClick()
        getNumber()
        return binding.root
    }

    private fun onContinueClick() {
        binding.continuebtn.setOnClickListener {
            val number=binding.etUserNumber.text
            if (number!!.isEmpty() || number.length<10 || number.length>10){
                binding.etUserNumber.error="Invalid Number"
                return@setOnClickListener
            }
            val bundle= Bundle()
            bundle.putString("number", number.toString())
            findNavController().navigate(R.id.action_signInFragment_to_OTPFragment)
        }
    }

    private fun getNumber() {
        binding.etUserNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                if (number?.length==10){
                    binding.continuebtn.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        )
                    )
                }else{
                    binding.continuebtn.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grayish_blue
                        )
                    )
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}