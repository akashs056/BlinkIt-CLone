package com.example.userblinkitclone.Models

data class OrderedItems(
    val orderId: Orders? =null,
    val itemDate:String?=null,
    val itemStatus:Int?=null,
    val itemTitle:String?=null,
    val itemPrice:Int?=null
)
