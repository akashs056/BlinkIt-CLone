package com.example.userblinkitclone.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.userblinkitclone.Models.Category
import com.example.userblinkitclone.databinding.SampleProductCategoryBinding

class CategoryAdapter(val categoryList : ArrayList<Category>) :
    RecyclerView.Adapter<CategoryAdapter.viewHolder>() {


    class viewHolder(val binding : SampleProductCategoryBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(SampleProductCategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
       return categoryList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val categoryList=categoryList[position]
        holder.binding.apply {
            CategoryImage.setImageResource(categoryList.image)
            CategoryTitle.text=categoryList.title
        }
    }
}