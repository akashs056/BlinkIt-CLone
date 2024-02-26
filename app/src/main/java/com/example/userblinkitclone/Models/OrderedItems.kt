package com.example.userblinkitclone.Models

import com.example.userblinkitclone.roomdb.cartProducts

data class OrderedItems(
    val orderId:String?=null,
    val itemDate:String?=null,
    val itemStatus:Int?=null,
    val itemTitle:String?=null,
    val itemPrice:Int?=null
)
