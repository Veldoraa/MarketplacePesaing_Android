package com.bornewtech.marketplacepesaing.ui.transaksi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.AdapterTransaksi
import com.bornewtech.marketplacepesaing.data.firestoreDb.Transaction
import com.bornewtech.marketplacepesaing.databinding.ActivityTransaksiBinding

class Transaksi : AppCompatActivity() {
    private lateinit var binding: ActivityTransaksiBinding
    private lateinit var adapterTransaksi: AdapterTransaksi
    private lateinit var transaksiList: List<Transaction>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnImgAddAlamat.setOnClickListener {
            startActivity(Intent(this, Alamat::class.java))
        }

        // Retrieve the cartItems list from the intent
        transaksiList = intent.getSerializableExtra("cartItems") as? List<Transaction> ?: emptyList()

        // Initialize RecyclerView and Adapter
        adapterTransaksi = AdapterTransaksi(transaksiList)
        val recyclerView: RecyclerView = findViewById(R.id.rvProductsTransaksi)
        recyclerView.adapter = adapterTransaksi
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}