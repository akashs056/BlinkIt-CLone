package com.example.userblinkitclone.Utils

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.userblinkitclone.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Utils {

    private var dialog:AlertDialog?=null
    fun showDialog(context: Context,message: String){
        val progress= ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.text.text=message
        dialog=AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }
    fun hideDialog(){
        dialog?.dismiss()
    }
    fun Toast(context: Context,message:String){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
    }
    private var firebaseAuthInstance : FirebaseAuth?=null
    fun getAuthInstance(): FirebaseAuth{
        if (firebaseAuthInstance==null){
            firebaseAuthInstance= FirebaseAuth.getInstance()
        }
        return firebaseAuthInstance!!
    }

    fun getCurrentUid() : String{
        return firebaseAuthInstance!!.currentUser!!.uid
    }
    fun getRandomUid():String{
        return (1..28).map { (('a'..'z') + ('0'..'9')).random() }.joinToString("")
    }
    fun getCurrentDate():String{
        val currentDate=LocalDate.now()
        val formatter=DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }
}