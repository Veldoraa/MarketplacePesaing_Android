package com.bornewtech.marketplacepesaing.ui.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bornewtech.marketplacepesaing.data.firestoreDb.ProductItem
import com.bornewtech.marketplacepesaing.databinding.ActivityDetailBarangBinding
import com.bornewtech.marketplacepesaing.ui.cart.produk.Keranjang
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
            // Get the selected product item
            val selectedItem = intent.getSerializableExtra("selectedItem") as? ProductItem?

            // Check if the selected item is not null
            if (selectedItem != null) {
                // Create a CartItem object from the selected product
                val cartItem = selectedItem.produkHarga?.let { prices ->
                    selectedItem.produkNama?.let { name ->
                        CartItem(
                            productId = selectedItem.userId, // Adjust the property based on your ProductItem class
                            productName = name,
                            productQuantity =  1, // Set the initial quantity as needed
                            productPrice = prices.toDouble() // Convert to double based on your ProductItem class
                        )
                    }
                }

                // Save the CartItem to the cart
                if (cartItem != null) {
                    saveCartItemToCart(cartItem)
                }

                // Open the Keranjang activity
                startActivity(Intent(this, Keranjang::class.java))
                finish()
            } else {
                // Handle the case when the selected item is null
                Toast.makeText(this, "Failed to get selected item", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun saveCartItemToCart(cartItem: CartItem) {
        // Implementasikan penyimpanan item keranjang, misalnya dengan menggunakan SharedPreferences
        // Anda dapat menggunakan Firebase Database/Firestore jika memungkinkan
        // Contoh dengan SharedPreferences:
        val sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE)
        val cartItems = sharedPreferences.getStringSet("cartItems", mutableSetOf())?.toMutableSet()

        // Convert objek CartItem menjadi String dan tambahkan ke Set
        cartItems?.add(cartItem.toString())

        // Simpan kembali ke SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putStringSet("cartItems", cartItems)
        editor.apply()
    }
}