package com.example.userblinkitclone.viewModels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.userblinkitclone.Models.Product
import com.example.userblinkitclone.roomdb.CartProductDatabase
import com.example.userblinkitclone.roomdb.cartProducts
import com.example.userblinkitclone.roomdb.cartProductsDao
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {


    //initialization
    val sharedPreferences:SharedPreferences=application.getSharedPreferences("My_Pref",MODE_PRIVATE)
    val cartProductsDao:cartProductsDao=CartProductDatabase.getDatabaseInstance(application).cartProductDao()

    //Room DB
    suspend fun insertCartProduct(cartProducts: cartProducts){
        cartProductsDao.insertCartProduct(cartProducts)
    }

    fun getAll(): LiveData<List<cartProducts>>{
        return cartProductsDao.getAllCartProducts()
    }
    suspend fun updateCartProduct(cartProducts: cartProducts){
        cartProductsDao.updateCartProduct(cartProducts)
    }
    suspend fun deleteCartProduct(productId:String){
        cartProductsDao.deleteCartProduct(productId)
    }
    //Firebase Call
    fun getCategoryProducts(title: String?): Flow<List<Product>> = callbackFlow{
        val database=  FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory").child(title!!)

        val eventListener =object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val product=ArrayList<Product>()
                    for (products in snapshot.children){
                        val p=products.getValue(Product::class.java)
                        product.add(p!!)
                    }
                    val sortedProducts = product.sortedByDescending { it.timestamp }
                    trySend(sortedProducts)
                }else{
                    trySend(emptyList())
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        database.addValueEventListener(eventListener)
        awaitClose{database.removeEventListener(eventListener)}

    }
    fun fetchAllProducts(title: String?): Flow<List<Product>> = callbackFlow{
        val db= FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")
        val eventListener=object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products =ArrayList<Product>()
                for (product in snapshot.children){
                    val prod=product.getValue(Product::class.java)
                    if(title=="All" || prod?.productCategory==title){
                        products.add(prod!!)
                    }
                }
                val sortedProducts = products.sortedByDescending { it.timestamp }
                trySend(sortedProducts)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        db.addValueEventListener(eventListener)
        awaitClose{db.removeEventListener(eventListener)}
    }
    fun addProductToFirebase(product: Product,itemCount:Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts").child(product.productRandomId).child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory").child(product.productCategory!!).child(product.productRandomId).child("itemCount").setValue(itemCount)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType").child(product.productType!!).child(product.productRandomId).child("itemCount").setValue(itemCount)

    }
    //shared Preference
    fun savingCartItemCount(itemCount :Int){
        sharedPreferences.edit().putInt("itemCount",itemCount).apply()
    }
    fun fetchTotalcartItemCount() : MutableLiveData<Int>{
        val totalItemCount=MutableLiveData<Int>()
        totalItemCount.value=sharedPreferences.getInt("itemCount",0)
        return totalItemCount
    }
}