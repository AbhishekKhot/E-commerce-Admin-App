package com.example.codeboomvendorapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.codeboomvendorapp.adapters.OrderAdapter
import com.example.codeboomvendorapp.databinding.FragmentOrderDetailsBinding
import com.example.codeboomvendorapp.model.Order
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderDetailsFragment : Fragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter
    private val fireStore = Firebase.firestore
    private val list = ArrayList<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentOrderDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orderAdapter = OrderAdapter(requireActivity())
        setUpRecyclerView()
        getOrderData()
    }

    private fun getOrderData() {
        fireStore.collection("AllOrders").get()
            .addOnSuccessListener {
                list.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(Order::class.java)
                    list.add(data!!)
                }
                orderAdapter.differ.submitList(list)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setUpRecyclerView() {
        binding.recyclerViewOrder.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = orderAdapter
        }
    }
}