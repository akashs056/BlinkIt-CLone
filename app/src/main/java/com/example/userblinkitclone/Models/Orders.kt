package com.example.userblinkitclone.Models

import com.example.userblinkitclone.roomdb.cartProducts

data class Orders(
    val orderId:String?=null,
    val orderList:List<cartProducts>?=null,
    val userAddress:String?=null,
    val orderStatus:Int=0,
    val orderDate:String?=null,
    val orderingUserId:String?=null
)
