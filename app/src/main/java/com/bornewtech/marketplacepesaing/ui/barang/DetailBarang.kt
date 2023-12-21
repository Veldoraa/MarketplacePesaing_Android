package com.bornewtech.marketplacepesaing.ui.barang

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bornewtech.marketplacepesaing.data.firestoreDb.ProductItem
import com.bornewtech.marketplacepesaing.databinding.ActivityDetailBarangBinding
import com.bornewtech.marketplacepesaing.ui.cart.produk.Keranjang
import com.bornewtech.marketplacepesaing.ui.profile.ResetPassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DetailBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBarangBinding
    private var dbBarang = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val selectedItem = intent.getSerializableExtra("selectedItem") as? ProductItem
        if (selectedItem != null) {
            // Use selectedItem data to populate the detail view
            binding.namaBarang.text = selectedItem.produkNama.toString()
            binding.category.text = selectedItem.produkKategori.toString()
            binding.satuan.text = selectedItem.produkSatuan.toString()
            binding.stokBarang.text = selectedItem.produkStok.toString()
            binding.hargaBarang.text = selectedItem.produkHarga.toString()
        }


        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("Products").document(userId)
        //get Data ke detail barang
        referensi.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val array = document.get("productList") as ArrayList<*>
                    if (array.isNotEmpty()){
                        array[0]
                    }
                }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal Menarik Data", Toast.LENGTH_SHORT).show()
            }

        binding.btnToKeranjang.setOnClickListener {
            startActivity(Intent(this, Keranjang::class.java))
            finish()
        }
    }
}