package com.example.userblinkitclone.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.userblinkitclone.Models.OrderedItems
import com.example.userblinkitclone.R
import com.example.userblinkitclone.databinding.SampleYourOrdersBinding

class OrdersAdapter(val context: Context):RecyclerView.Adapter<OrdersAdapter.viewHolder>() {
    class viewHolder(val binding:SampleYourOrdersBinding):RecyclerView.ViewHolder(binding.root){}


    val diffUtil=object :DiffUtil.ItemCallback<OrderedItems>(){
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem.orderId==newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem==newItem
        }
    }
    val differ=AsyncListDiffer(this,diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(SampleYourOrdersBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentOrder=differ.currentList[position]
        holder.binding.apply {
            title.text=currentOrder.itemTitle
            price.text=currentOrder.itemPrice.toString()
            date.text=currentOrder.itemDate
            when(currentOrder.itemStatus){
            0->{
                status.text="Ordered"
                status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.yellow)
            }1->{
                status.text="Redceived"
                status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.blue)
            }2->{
                status.text="Dispatched"
                status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.green)
            }3->{
                status.text="Delivered"
                status.backgroundTintList=ContextCompat.getColorStateList(context, R.color.orange)
            }
            }
        }
    }
}