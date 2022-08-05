package com.example.codeboomvendorapp.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.codeboomvendorapp.model.Category
import com.example.codeboomvendorapp.R
import com.example.codeboomvendorapp.adapters.CategoryAdapter
import com.example.codeboomvendorapp.databinding.FragmentCategoriesBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private lateinit var dialog: Dialog
    private val fireStore = Firebase.firestore
    private val categoryAdapter=CategoryAdapter()

    private var launchGalleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageUri = it.data!!.data
                binding.ivCategory.setImageURI(imageUri)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        _binding = FragmentCategoriesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.progress_dialog)
        dialog.setCancelable(false)

        setUpRecyclerView()
        binding.tvChooseImage.setOnClickListener {
            val intent = Intent("android.intent.action.GET_CONTENT")
            intent.type = "image/*"
            launchGalleryActivity.launch(intent)
        }

        binding.btnAddCategory.setOnClickListener {
            checkFieldValues(binding.etCategoryName.text.toString())
        }

    }

    private fun setUpRecyclerView() {
        val list = ArrayList<Category>()
        fireStore.collection("Categories").get()
            .addOnSuccessListener {
                list.clear()
                for(doc in it.documents){
                    val data = doc.toObject(Category::class.java)
                    list.add(data!!)
                }
                categoryAdapter.differ.submitList(list)
                binding.recyclerViewCategory.adapter=categoryAdapter
            }
    }

    private fun checkFieldValues(categoryName: String) {
        if (categoryName.isEmpty()) {
            binding.etCategoryName.requestFocus()
            binding.etCategoryName.error="Empty"
        } else if (imageUri == null) {
            Toast.makeText(requireActivity(), "Please select image", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage(categoryName)
        }
    }

    private fun uploadImage(categoryName: String) {
        dialog.show()
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val storageReference = FirebaseStorage.getInstance().reference.child("Category/$fileName")
        storageReference.putFile(imageUri!!).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener { image ->
                uploadDataToFireStore(categoryName, image.toString())
            }
        }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireActivity(),
                    it.message.toString() + "Failed",
                    Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadDataToFireStore(categoryName: String, url: String) {

        val categoryMap =
            hashMapOf<String, Any>("category_name" to categoryName, "category_image" to url)

        fireStore.collection("Categories").add(categoryMap)
            .addOnSuccessListener {
                dialog.dismiss()
                binding.ivCategory.setImageDrawable(resources.getDrawable(R.drawable.ic_image_search))
                binding.etCategoryName.text = null
                setUpRecyclerView()
                Toast.makeText(requireActivity(),
                    "new Category added successfully",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(requireActivity(),
                    it.message.toString() + "Failed",
                    Toast.LENGTH_SHORT).show()
            }
    }
}