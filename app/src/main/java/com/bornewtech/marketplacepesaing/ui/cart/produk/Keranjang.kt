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
    private lateinit var cartAdapter: AdapterKeranjang
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi cartItems sebagai MutableList
        cartItems = mutableListOf()

        recyclerView = findViewById(R.id.rvCart)
        cartAdapter = AdapterKeranjang(
            cartItems,
            onItemClick = {},
            onIncrementClick = { handleIncrementClick(it) },
            onDecrementClick = { handleDecrementClick(it) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

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
        cartAdapter.updateData(cartItems)
        cartAdapter.notifyDataSetChanged()
    }

    private fun handleIncrementClick(cartItem: CartItem) {
        val updatedCartItem = cartItem.copy()  // Copy item untuk menghindari modifikasi langsung pada item yang ada di cartItems
        updatedCartItem.incrementQuantity()
        updateCart(updatedCartItem)
    }

    private fun handleDecrementClick(cartItem: CartItem) {
        val updatedCartItem = cartItem.copy()  // Copy item untuk menghindari modifikasi langsung pada item yang ada di cartItems
        updatedCartItem.decrementQuantity()
        updateCart(updatedCartItem)
    }



    private fun updateCart(cartItem: CartItem) {
        // Update quantity pada setiap item langsung di dalam cartItems
        val updatedCartItems = cartItems.map {
            val item = it.copy()  // Copy item untuk menghindari modifikasi langsung pada item yang ada di cartItems
            if (item.productId == cartItem.productId) {
                // Update quantity jika item ditemukan
                item.productQuantity = cartItem.productQuantity
            }
            item
        }.toMutableList()

        // Simpan kembali ke SharedPreferences
        val sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("cartItems", updatedCartItems.map { it.toString() }.toMutableSet())
        editor.apply()

        // Refresh RecyclerView
        cartAdapter.updateData(updatedCartItems)
        cartAdapter.notifyDataSetChanged()
    }

}
