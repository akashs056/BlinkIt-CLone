package com.example.userblinkitclone.Auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.userblinkitclone.Activities.UsersMainActivity
import com.example.userblinkitclone.Models.Users
import com.example.userblinkitclone.R
import com.example.userblinkitclone.Utils.Utils
import com.example.userblinkitclone.databinding.FragmentOTPBinding
import com.example.userblinkitclone.viewModels.AuthVIewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private val viewModel =AuthVIewModel()
    private lateinit var binding: FragmentOTPBinding
    private lateinit var number:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentOTPBinding.inflate(layoutInflater)
        getUserNumber()
        sendOtp(number)
        customizeEnteringOTP()
        onBackButtonClicked()
        onLoginButtonClicked()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.login.setOnClickListener {
            Utils.showDialog(requireContext(),"Verifying the OTP")
            val editTexts = arrayOf(binding.otp1,binding.otp2,binding.otp3,binding.otp4,binding.otp5,binding.otp6)
            val otp=editTexts.joinToString(""){
                it.text.toString()
            }
            if (otp.length<editTexts.size){
                Utils.Toast(requireContext(),"Incorrect Otp")
                Utils.hideDialog()
            }else{
                editTexts.forEach { it.text?.clear()
                it.clearFocus()
                }
                verifyOTP(otp)
            }
        }
    }

    private fun verifyOTP(otp: String) {
        val users = Users(Utils.getCurrentUid(),number,null)
        viewModel.apply {
            signInWithPhoneAuthCredential(otp,number,users)
            lifecycleScope.launch{
                success.collect{
                    if (it){
                        Utils.hideDialog()
                        Utils.Toast(requireContext(),"Signed In Successfully")
                        startActivity(Intent(requireActivity(),UsersMainActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }
    }


    private fun sendOtp(number: String) {
        Utils.showDialog(requireContext(),"Sending OTP")
        viewModel.apply {
            sendOtp(number,requireActivity())
            lifecycleScope.launch {
                otpSent.collect{
                    if (it){
                        Utils.hideDialog()
                        Utils.Toast(requireContext(),"OTP sent Successfully")
                    }
                }
            }
        }
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