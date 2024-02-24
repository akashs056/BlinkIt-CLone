package com.example.userblinkitclone.viewModels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.userblinkitclone.Models.Users
import com.example.userblinkitclone.Utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthVIewModel : ViewModel() {
    private val _verificationId= MutableStateFlow<String?>(null)
    private val _otpSent=MutableStateFlow(false)
    val otpSent =_otpSent
    private  val _success=MutableStateFlow(false)
    val  success=_success
    private val _isCurrentUser=MutableStateFlow(false)
    val isCurrentUser=_isCurrentUser

    init {
        Utils.getAuthInstance().currentUser?.let {
            _isCurrentUser.value=true
        }
    }
    fun sendOtp(number : String, activity: Activity){
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationId.value=verificationId
                _otpSent.value=true
            }
        }
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$number") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun signInWithPhoneAuthCredential(otp: String, number: String) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)
        Utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val users = Users(uid = task.result.user!!.uid,number=number,address ="null")
                    _success.value=true
                    FirebaseDatabase.getInstance().getReference("ALl Users")
                        .child("Users")
                        .child(users.uid!!)
                        .setValue(users)
                } else {

                }
            }
    }
}