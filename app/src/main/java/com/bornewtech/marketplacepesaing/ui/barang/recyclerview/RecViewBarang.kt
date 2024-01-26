package com.bornewtech.marketplacepesaing.ui.barang.recyclerview

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.AdapterProduct
import com.bornewtech.marketplacepesaing.data.firestoreDb.Products
import com.bornewtech.marketplacepesaing.databinding.ActivityRecViewBarangBinding
import com.bornewtech.marketplacepesaing.ui.barang.DetailBarang
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecViewBarang : AppCompatActivity() {
    private lateinit var binding: ActivityRecViewBarangBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterProduk: AdapterProduct
    private val dbBarang = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecViewBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        recyclerView = findViewById(R.id.recViewBarang)
        recyclerView.setHasFixedSize(true)

        adapterProduk = AdapterProduct(emptyList()) { selectedItem ->
            // Handle item click here
            Log.d("RecViewBarang", "Item clicked: $selectedItem")

            // Start DetailBarang activity with selected item
            val intent = Intent(this, DetailBarang::class.java)
            intent.putExtra("selectedItem", selectedItem)
            startActivity(intent)
        }

        setupRecyclerView()
        eventChangeListener()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterProduk
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun eventChangeListener() {
        val selectedId = intent.getStringExtra("idSelected")
        if (selectedId != null) {
            dbBarang.collection("Products").document(selectedId)
                .addSnapshotListener { documentSnapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val productList = documentSnapshot.toObject(Products::class.java)
                        if (productList != null) {
                            val productListData = productList.productList
                            productListData?.let {
                                // Convert Firestore data to include image URL
                                val productListWithImageUrl = it.map { productItem ->
                                    productItem.copy(imageUrl = productItem.imageUrl)
                                }
                                adapterProduk.updateData(productListWithImageUrl)
                                adapterProduk.notifyDataSetChanged()
                            }
                        } else {
                            Log.d(TAG, "Failed to convert document to Products")
                        }
                    } else {
                        Log.d(TAG, "Document does not exist")
                    }
                }
        } else {
            Log.d(TAG, "User not authenticated")
        }
    }
}