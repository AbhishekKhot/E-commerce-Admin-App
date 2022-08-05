package com.example.codeboomvendorapp.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.codeboomvendorapp.databinding.ProductImageItemBinding
import com.example.codeboomvendorapp.model.Category

class ProductImageAdapter(val list:ArrayList<Uri>):RecyclerView.Adapter<ProductImageAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding:ProductImageItemBinding):RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding=ProductImageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.binding.ivProducts.setImageURI(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}