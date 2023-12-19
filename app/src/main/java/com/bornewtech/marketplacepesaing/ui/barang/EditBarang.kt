package com.bornewtech.marketplacepesaing.ui.barang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.marketplacepesaing.data.firestoreDb.ProductItem
import com.bornewtech.marketplacepesaing.data.firestoreDb.Products
import com.bornewtech.marketplacepesaing.databinding.ActivityEditBarangBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditBarang : AppCompatActivity() {
    private lateinit var binding: ActivityEditBarangBinding
    private var dbBarang = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setData()

        binding.btnUpdateBarang.setOnClickListener {
            val uNama = binding.updNamaProduk.text.toString().trim()
            val uKategori = binding.updKategori.text.toString().trim()
            val uSatuan = binding.updSatuan.text.toString().trim()
            val uStok = binding.updStokBarang.text.toString().trim()
            val uHarga = binding.updHargaBarang.text.toString().trim()

            val updateBarang = hashMapOf(
                "produkNama" to uNama,
                "produkKategori" to uKategori,
                "produkSatuan" to uSatuan,
                "produkStok" to uStok,
                "produkHarga" to uHarga
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val produkUpdate = dbBarang.collection("Products").document(userId)
//            dbBarang.collection("Products").document(userId).update(updateBarang)
            produkUpdate.update("productList", FieldValue.arrayUnion(updateBarang))
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil memperbarui data", Toast.LENGTH_SHORT).show()
                    binding.updNamaProduk.text.toString()
                    binding.updKategori.text.toString()
                    binding.updSatuan.text.toString()
                    binding.updStokBarang.text.toString()
                    binding.updHargaBarang.text.toString()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                }
//            Toast.makeText(this, "Sukses mengupdate barang", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val referensi = dbBarang.collection("Products").document(userId)
        referensi.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val products = document.toObject(Products::class.java)
                    val productList = products?.productList

                    if (!productList.isNullOrEmpty()) {
                        val selectedItem = intent.getSerializableExtra("selectedItem") as? ProductItem

                        if (selectedItem != null) {
                            // Use selectedItem data to populate the UI
                            binding.updNamaProduk.setText(selectedItem.produkNama)
                            binding.updKategori.setText(selectedItem.produkKategori)
                            binding.updSatuan.setText(selectedItem.produkSatuan)
                            binding.updStokBarang.setText(selectedItem.produkStok)
                            binding.updHargaBarang.setText(selectedItem.produkHarga)
                        } else {
                            // Handle the case when no item is selected (optional)
                            Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mengupdate barang", Toast.LENGTH_SHORT).show()
            }
    }
}