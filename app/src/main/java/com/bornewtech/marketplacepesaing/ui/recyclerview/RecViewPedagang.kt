package com.bornewtech.marketplacepesaing.ui.recyclerview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.AdapterPedagang
import com.bornewtech.marketplacepesaing.data.firestoreDb.Pedagang
import com.bornewtech.marketplacepesaing.data.firestoreDb.Products
import com.bornewtech.marketplacepesaing.databinding.ActivityRecViewPedagangBinding
import com.bornewtech.marketplacepesaing.ui.barang.recyclerview.RecViewBarang
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class RecViewPedagang : AppCompatActivity() {
    private lateinit var binding: ActivityRecViewPedagangBinding
    private var firestore =  FirebaseFirestore.getInstance()
    private val produkList = mutableListOf<Products>() // List untuk menyimpan data produk

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecViewPedagangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)

        val recyclerView: RecyclerView = findViewById(R.id.recViewPedagang)
        val layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager

        // Move adapter initialization here
        val adapter = AdapterPedagang(emptyList()) { pedagang ->
            // Handle item click here
            Log.d("RecViewPedagang", "Item clicked: $pedagang")

            // Start DetailBarang activity with selected item
            val intent = Intent(this, RecViewBarang::class.java)
            intent.putExtra("selectedPedagang", pedagang)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        firestore.collection("Profils")
            .get()
            .addOnSuccessListener { result ->
                val pedagangList = mutableListOf<Pedagang>()

                for (document in result) {
                    val pedagang = document.toObject(Pedagang::class.java)
                    pedagangList.add(pedagang)
                }

                // Pass pedagangList to your RecyclerView adapter
                adapter.updateData(pedagangList)

                // Notify data set changed
                adapter.notifyDataSetChanged()

                // Log pedagangList for debugging
                Log.d("RecViewPedagang", "Pedagang List: $pedagangList")

                // Load produk data berdasarkan ID pedagang
                loadProdukData(pedagangList)
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e("RecViewPedagang", "Error fetching data", exception)
            }
    }

    private fun loadProdukData(pedagangList: List<Pedagang>) {
        // Ambil ID pedagang dari pedagangList dan gunakan untuk query produk
        val pedagangIds = pedagangList.map { it.userId ?: "" }

        firestore.collection("Produk")
            .whereIn("pedagangId", pedagangIds)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot ->
                // Loop through each document in the query results
                for (document in querySnapshot.documents) {
                    val produk = document.toObject(Products::class.java)
                    produk?.let { produkList.add(it) }
                }

                // Handle the case when data loading is complete
                // You may want to notify the adapter or perform other actions here
                Log.d("RecViewPedagang", "Produk List: $produkList")
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e("RecViewPedagang", "Error fetching produk data", exception)
            }
    }
}
