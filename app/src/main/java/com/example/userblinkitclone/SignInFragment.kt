package com.example.userblinkitclone

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.databinding.FragmentSignInBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignInFragment : Fragment() {
    private lateinit var binding:FragmentSignInBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSignInBinding.inflate(layoutInflater)

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
            val bundle=Bundle()
            bundle.putString("number", number.toString())
            findNavController().navigate(R.id.action_signInFragment_to_OTPFragment)
        }
    }

    private fun getNumber() {
        binding.etUserNumber.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                if (number?.length==10){
                    binding.continuebtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
                }else{
                    binding.continuebtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.grayish_blue))
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}