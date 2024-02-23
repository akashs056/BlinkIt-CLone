package com.example.userblinkitclone.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.example.userblinkitclone.FilteringProducts
import com.example.userblinkitclone.Models.Product
import com.example.userblinkitclone.databinding.SampleProductsBinding

class ProductAdapter(
    val OnAddBtnClicked: (Product, SampleProductsBinding) -> Unit,
    val onIncrementBtnCLicked: (Product, SampleProductsBinding) -> Unit,
    val onDecrementBtnCLicked: (Product, SampleProductsBinding) -> Unit
) : RecyclerView.Adapter<ProductAdapter.viewHolder>() ,
    Filterable {
    class viewHolder(val binding: SampleProductsBinding) : RecyclerView.ViewHolder(binding.root){}

    val difutil =object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productRandomId==newItem.productRandomId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem==newItem
        }
    }
    val differ= AsyncListDiffer(this,difutil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(SampleProductsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val product=differ.currentList[position]
        holder.binding.apply {

            val imageList=ArrayList<SlideModel>()
            val productImage=product.productImageUris
            for (i in 0 until productImage!!.size){
                imageList.add(SlideModel(productImage[i].toString()))
            }
            imageSlider.setImageList(imageList)
            tvProductTitle.text=product.productTitle
            val quantity=product.productQuantity.toString()+product.productTUnit
            tvProductQuantity.text=quantity
            tvProductPrice.text="₹"+product.productPrice

            if (product.itemCount!!>0){
                add.visibility= View.GONE
                productCount.visibility= View.VISIBLE
                count.text= product.itemCount.toString()
            }

            add.setOnClickListener {
                OnAddBtnClicked(product,this)
            }
            increment.setOnClickListener {
                onIncrementBtnCLicked(product,this)
            }
            decrement.setOnClickListener {
                onDecrementBtnCLicked(product,this)
            }
        }
    }

    private  val filter : FilteringProducts?=null
    var originalList = ArrayList<Product>()
    override fun getFilter(): Filter {
        if (filter==null) return FilteringProducts(this,originalList)
        return filter
    }
}