package com.bornewtech.marketplacepesaing.ui.cart.produk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.AdapterKeranjang
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bornewtech.marketplacepesaing.databinding.ActivityKeranjangBinding
import com.bornewtech.marketplacepesaing.ui.barang.recyclerview.RecViewBarang
import com.bornewtech.marketplacepesaing.ui.transaksi.Transaksi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Keranjang : AppCompatActivity() {
    private lateinit var binding: ActivityKeranjangBinding
    private lateinit var cartItems: MutableList<CartItem>
    private lateinit var cartAdapter: AdapterKeranjang
    private lateinit var recyclerView: RecyclerView
    private lateinit var firestoreKeranjang: FirebaseFirestore
    private lateinit var authUser: FirebaseAuth

    private var totalCartPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKeranjangBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        // Initialize Firebase Firestore
        firestoreKeranjang = FirebaseFirestore.getInstance()

        authUser = FirebaseAuth.getInstance()

        binding.buttonPembayaran.setOnClickListener {
            val intent = Intent(this, Transaksi::class.java)
            intent.putParcelableArrayListExtra("cartItems", ArrayList(cartItems))
            startActivity(intent)
        }

        // Initialize cartItems as MutableList
        cartItems = mutableListOf()

        recyclerView = findViewById(R.id.rvCart)
        cartAdapter = AdapterKeranjang(
            cartItems,
            onItemClick = {},
            onIncrementClick = { cartItem, position -> handleIncrementClick(cartItem) },
            onDecrementClick = { cartItem, position -> handleDecrementClick(cartItem) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter

        retrieveCartItems()
    }

    private fun retrieveCartItems() {
        // Implement the logic to retrieve the list of cart items
        // from SharedPreferences or Firebase Database/Firestore
        // and store it in the cartItems list.

        // Example with SharedPreferences:
        val sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE)
        val cartItemsSet = sharedPreferences.getStringSet("cartItems", mutableSetOf()) ?: emptySet()

        // Convert set of strings back to list of CartItem objects
        cartItems = cartItemsSet.map { CartItem.fromString(it) }.toMutableList()

        // Refresh RecyclerView
        cartAdapter.updateData(cartItems)
        cartAdapter.notifyDataSetChanged()

        // Iterate through each cart item and fetch Pedagang ID based on Produk ID
        cartItems.forEach { cartItem ->
            fetchPedagangIdForCartItem(cartItem)
        }

        // Calculate total price and update display
        calculateTotalCartPrice()
        updateTotalPriceDisplay()

        // Save cart to Firestore after fetching Pedagang ID for each item
        saveCartToFirestore(cartItems)
    }

    private fun fetchPedagangIdForCartItem(cartItem: CartItem) {
        val currentUser = authUser.currentUser
        val uid = currentUser?.uid

        uid?.let {
            cartItem.productId?.let { productId ->
                val productsCollection = firestoreKeranjang.collection("Products")
                val productDocument = productsCollection.document(productId)

                productDocument.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val pedagangId = documentSnapshot.getString("pedagangId")
                            if (pedagangId != null) {
                                // Set Pedagang ID on cartItem
                                cartItem.pedagangId = pedagangId

                                // Update cartItems list and save to SharedPreferences
                                updateCart(cartItem, false)
                            } else {
                                Log.e("Keranjang", "Pedagang ID is null for Produk ID: $productId")
                            }
                        } else {
                            Log.e("Keranjang", "Document does not exist for Produk ID: $productId")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Keranjang", "Error fetching document for Produk ID: $productId", exception)
                    }
            }
        } ?: run {
            Log.e("Keranjang", "User UID is null")
        }
    }

    private fun handleIncrementClick(cartItem: CartItem) {
        val currentUser = authUser.currentUser

        if (currentUser != null) {
            val pembeliId = currentUser.uid

            // Find the index of the item in the cartItems list
            val index = cartItems.indexOfFirst { it.productId == cartItem.productId }

            if (index != -1) {
                // Update the quantity of the existing item directly
                cartItems[index].incrementQuantity()

                // Set the pembeliId for the existing item
                cartItems[index].pembeliId = pembeliId

                // Update the adapter with the new data
                cartAdapter.updateData(cartItems)
                cartAdapter.notifyDataSetChanged()

                // Save cart to Firestore after fetching Pedagang ID for each item
                saveCartToFirestore(cartItems)

                // Calculate total price and update display
                calculateTotalCartPrice()
                updateTotalPriceDisplay()
            }
        } else {
            Log.e("Keranjang", "User is not signed in during increment click")
        }
    }

    private fun handleDecrementClick(cartItem: CartItem) {
        val currentUser = authUser.currentUser

        if (currentUser != null) {
            val pembeliId = currentUser.uid

            // Find the index of the item in the cartItems list
            val index = cartItems.indexOfFirst { it.productId == cartItem.productId }

            if (index != -1) {
                // Update the quantity of the existing item directly
                cartItems[index].decrementQuantity()

                // Set the pembeliId for the existing item
                cartItems[index].pembeliId = pembeliId

                // Check if quantity is less than or equal to 0
                if (cartItems[index].productQuantity <= 0) {
                    // Remove the item from Firestore and SharedPreferences
                    removeItemFromFirestoreAndSharedPreferences(cartItem)
                } else {
                    // Update the adapter with the new data
                    cartAdapter.updateData(cartItems)
                    cartAdapter.notifyDataSetChanged()

                    // Save cart to Firestore after fetching Pedagang ID for each item
                    saveCartToFirestore(cartItems)

                    // Calculate total price and update display
                    calculateTotalCartPrice()
                    updateTotalPriceDisplay()
                }
            }
        } else {
            Log.e("Keranjang", "User is not signed in during decrement click")
        }
    }

    private fun removeItemFromFirestoreAndSharedPreferences(cartItem: CartItem) {
        // Remove the item from Firestore
        removeItemFromFirestore(cartItem)

        // Remove the item from SharedPreferences
        removeItemFromSharedPreferences(cartItem)
    }

    private fun removeItemFromFirestore(cartItem: CartItem) {
        val currentUser = authUser.currentUser
        val uid = currentUser?.uid

        if (uid != null && cartItem.productId != null) {
            val cartsCollection = firestoreKeranjang.collection("Keranjang").document(uid)

            // Filter out the item to be removed from Firestore
            val updatedCartItems = cartItems.filter { it.productId != cartItem.productId }

            // Update Firestore with the new cartItems (excluding the removed item)
            cartsCollection.set(mapOf("cartItems" to updatedCartItems.map { it.toMap() }))
                .addOnSuccessListener {
                    Log.d("Keranjang", "Item removed from Firestore successfully")

                    // Update local cartItems and UI after successful Firestore update
                    cartItems = updatedCartItems.toMutableList()
                    cartAdapter.updateData(cartItems)
                    calculateTotalCartPrice()
                    updateTotalPriceDisplay()
                }
                .addOnFailureListener {
                    Log.e("Keranjang", "Failed to remove item from Firestore", it)
                }
        }
    }

    private fun removeItemFromSharedPreferences(cartItem: CartItem) {
        val updatedCartItems = cartItems.filter { it.productId != cartItem.productId }

        // Update SharedPreferences with the new cartItems (excluding the removed item)
        saveCartToSharedPreferences(updatedCartItems)
    }

    private fun updateCart(cartItem: CartItem, removeIfZero: Boolean) {
        // Update quantity for each item directly within cartItems
        val updatedCartItems = cartItems.map {
            val item = it.copy()
            if (item.productId == cartItem.productId) {
                // Update quantity, pedagangId, and pembeliId if item is found
                item.productQuantity = cartItem.productQuantity
                item.pedagangId = cartItem.pedagangId
                item.pembeliId = cartItem.pembeliId
            }
            item
        }.toMutableList()

        // Remove the item only if removeIfZero is true and quantity is zero
        if (removeIfZero && cartItem.productQuantity < 1) {
            updatedCartItems.remove(cartItem)
        }

        // Save back to SharedPreferences
        saveCartToSharedPreferences(updatedCartItems)

        // Update the adapter with the new data
        cartAdapter.updateData(updatedCartItems)
        cartAdapter.notifyDataSetChanged()

        // Save cart to Firestore after fetching Pedagang ID for each item
        saveCartToFirestore(updatedCartItems)
    }

    private fun saveCartToSharedPreferences(cartItems: List<CartItem>) {
        // Save back to SharedPreferences
        val sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("cartItems", cartItems.map { it.toString() }.toMutableSet())
        editor.apply()
    }

    private fun saveCartToFirestore(cartItems: List<CartItem>) {
        // Get the UID of the currently logged-in user
        val currentUser = authUser.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            // Get the Firestore collection reference "carts" based on the user UID
            val cartsCollection = firestoreKeranjang.collection("Keranjang").document(uid)

            // Save the list of cart items to Firestore
            cartsCollection.set(mapOf("cartItems" to cartItems.map { it.toMap() }))
                .addOnSuccessListener {
                    Log.d("Keranjang", "Cart successfully saved to Firestore")
                }
                .addOnFailureListener {
                    Log.e("Keranjang", "Failed to save cart to Firestore", it)
                }
        }
    }

    private fun calculateTotalCartPrice() {
        // Calculate total price based on the updatedCartItems
        totalCartPrice = cartItems.sumByDouble { it.productPrice * it.productQuantity }.toInt()
    }

    private fun updateTotalPriceDisplay() {
        // Update the display of totalCartPrice wherever you want to show it
        // For example, if you have a TextView to display the total price:
        binding.tvTotalPrice.text = "Total: Rp ${totalCartPrice},00"
    }
}
