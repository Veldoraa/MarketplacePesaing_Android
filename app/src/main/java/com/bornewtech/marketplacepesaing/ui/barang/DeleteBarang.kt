package com.bornewtech.marketplacepesaing.ui.barang

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityDeleteBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DeleteBarang : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteBarangBinding
    private var dbBarang = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setData()
        binding.btnDeleteBarang.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbBarang.collection("Products").document(userId)
                .delete()
                .addOnSuccessListener {
                    val intent = Intent(this, DetailBarang::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
        }
    }
    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("Products").document(userId)
        referensi.get().addOnSuccessListener {
            if (it != null) {
                val nama = it.data?.get("Nama Produk")?.toString()
                val kategori = it.data?.get("Kategori Produk")?.toString()
                val satuan = it.data?.get("Satuan Produk")?.toString()
                val stok = it.data?.get("Stok Produk")?.toString()
                val harga = it.data?.get("Harga Produk")?.toString()

                binding.dltNamaProduk.setText(nama)
                binding.dltKategori.setText(kategori)
                binding.dltSatuan.setText(satuan)
                binding.dltStokBarang.setText(stok)
                binding.dltHargabrng.setText(harga)
            }
        }
            .addOnFailureListener{
                Toast.makeText(this, "Gagal menghapus barang", Toast.LENGTH_SHORT).show()
            }
    }
}