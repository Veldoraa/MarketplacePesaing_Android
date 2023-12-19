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
import com.bornewtech.marketplacepesaing.databinding.ActivityRecViewPedagangBinding
import com.bornewtech.marketplacepesaing.ui.barang.DetailBarang
import com.bornewtech.marketplacepesaing.ui.barang.recyclerview.RecViewBarang
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class RecViewPedagang : AppCompatActivity() {
    private lateinit var binding: ActivityRecViewPedagangBinding
    private var firestore =  FirebaseFirestore.getInstance()
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
            Log.d("RecViewBarang", "Item clicked: $pedagang")

            // Start DetailBarang activity with selected item
            val intent = Intent(this, RecViewBarang::class.java)
            intent.putExtra("selectedItem", pedagang)
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
            }
            .addOnFailureListener { exception ->
                // Handle errors
                Log.e("RecViewPedagang", "Error fetching data", exception)
            }
    }
}