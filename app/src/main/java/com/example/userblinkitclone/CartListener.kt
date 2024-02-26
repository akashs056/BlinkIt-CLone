package com.example.userblinkitclone

interface CartListener {
    fun showCartLayout(itemCount : Int)
    fun saveSharedPref(itemCount : Int)

    fun hideCartLayout()

}