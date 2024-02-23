package com.example.userblinkitclone.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface cartProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCartProduct(cartProducts: cartProducts)

    @Update
    fun updateCartProduct(cartProducts: cartProducts)

    @Query("SELECT * FROM CartProducts")
    fun getAllCartProducts():LiveData<List<cartProducts>>

    @Query("DELETE FROM CartProducts WHERE productRandomId=:productId")
    fun deleteCartProduct(productId:String)
}