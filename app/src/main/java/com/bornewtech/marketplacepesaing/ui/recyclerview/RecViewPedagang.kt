package com.bornewtech.marketplacepesaing.ui.recyclerview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.AdapterPedagang
import com.bornewtech.marketplacepesaing.data.firestoreDb.Pedagang
import com.bornewtech.marketplacepesaing.databinding.ActivityRecViewPedagangBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.bornewtech.marketplacepesaing.ui.barang.recyclerview.RecViewBarang
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.bumptech.glide.Glide

class RecViewPedagang : AppCompatActivity() {
    private lateinit var binding: ActivityRecViewPedagangBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val pedagangList = mutableListOf<Pedagang>()
    private lateinit var adapter: AdapterPedagang

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecViewPedagangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.toolbarlistPedagang.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        FirebaseApp.initializeApp(this)

        val recyclerView: RecyclerView = findViewById(R.id.recViewPedagang)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = AdapterPedagang(emptyList()) { pedagang ->
            // Handle item click here
            Log.d("RecViewPedagang", "Item clicked: $pedagang")

            // Start RecViewBarang activity with selected item
            val intent = Intent(this, RecViewBarang::class.java)
            intent.putExtra("idSelected", pedagang.userId)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        firestore.collection("Pedagang")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val pedagang = document.toObject(Pedagang::class.java)
                    pedagangList.add(pedagang)
                }

                adapter.updateData(pedagangList)
                adapter.notifyDataSetChanged()

                Log.d("RecViewPedagang", "Pedagang List: $pedagangList")
            }
            .addOnFailureListener { exception ->
                Log.e("RecViewPedagang", "Error fetching data", exception)
            }
    }
}
