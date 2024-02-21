package com.example.userblinkitclone.viewModels

import androidx.lifecycle.ViewModel
import com.example.userblinkitclone.Models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserViewModel : ViewModel() {

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

}