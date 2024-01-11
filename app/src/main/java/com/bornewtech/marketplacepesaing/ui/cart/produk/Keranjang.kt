package com.bornewtech.marketplacepesaing.ui.cart.produk

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.AdapterKeranjang
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bornewtech.marketplacepesaing.databinding.ActivityKeranjangBinding

class Keranjang : AppCompatActivity() {
    private lateinit var binding: ActivityKeranjangBinding
    private lateinit var cartItems: MutableList<CartItem>
    private lateinit var adapter: AdapterKeranjang
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi cartItems sebagai MutableList
        cartItems = mutableListOf()

        recyclerView = findViewById(R.id.rvCart)
        adapter = AdapterKeranjang(cartItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        retrieveCartItems()
    }

    private fun retrieveCartItems() {
        // Implementasikan logika untuk mengambil daftar item keranjang
        // dari SharedPreferences atau Firebase Database/Firestore
        // dan simpan dalam list cartItems.

        // Contoh dengan SharedPreferences:
        val sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE)
        val cartItemsSet = sharedPreferences.getStringSet("cartItems", mutableSetOf()) ?: emptySet()

        // Convert set of strings back to list of CartItem objects
        cartItems = cartItemsSet.map { CartItem.fromString(it) }.toMutableList()

        // Refresh RecyclerView
        adapter.updateData(cartItems)
        adapter.notifyDataSetChanged()
    }
}
