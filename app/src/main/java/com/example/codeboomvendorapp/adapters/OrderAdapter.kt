package com.example.codeboomvendorapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.codeboomvendorapp.R
import com.example.codeboomvendorapp.databinding.OrderItemBinding
import com.example.codeboomvendorapp.model.Order
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderAdapter(val context:Context):RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    inner class OrderViewHolder(val binding:OrderItemBinding):RecyclerView.ViewHolder(binding.root)

    val differCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.Order_Id==newItem.Order_Id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem==newItem
        }
    }

    val differ=AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
       val binding=OrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order=differ.currentList[position]
        Glide.with(holder.itemView).load(order.Product_Image).placeholder(R.drawable.ic_image_search).into(holder.binding.ivProduct)
        holder.binding.apply {
            this.tvName.text=order.Product_Name
            this.tvPrice.text=order.Product_Price
            this.tvDate.text=order.Order_time
        }

        if(order.Order_status=="Order Cancelled"){
            holder.binding.ivDelete.visibility=View.VISIBLE
        }

        holder.binding.ivDelete.setOnClickListener {
            val alert = AlertDialog.Builder(context)
            alert.setTitle("DELETE")
                .setMessage("DO YOU WANT TO DELETE THIS ORDER FROM DATABASE ?")
                .setNegativeButton("NO", null)
                .setPositiveButton("YES") { dialog, which ->
                    val fireStore= Firebase.firestore
                    fireStore.collection("AllOrders").whereEqualTo("Product_Id",order.Product_Id).get()
                        .addOnCompleteListener {
                            for (snapshot in it.result) {
                                fireStore.collection("AllOrders")
                                    .document(snapshot.id).delete()
                            }
                        }
                    notifyDataSetChanged()
                }
            alert.show()
        }

        holder.binding.btnUpdate.setOnClickListener {view->
            val fireStore= Firebase.firestore
            fireStore.collection("AllOrders").whereEqualTo("Product_Id",order.Product_Id).get()
                .addOnCompleteListener {
                    for (snapshot in it.result) {
                        fireStore.collection("AllOrders")
                            .document(snapshot.id).update("Order_status",holder.binding.spinnerStatus.selectedItem.toString().trim())

                    }
                }
                .addOnFailureListener {
                   Snackbar.make(view,"Something Went Wrong,Please Try Again",Toast.LENGTH_SHORT).show()
                }

            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

}