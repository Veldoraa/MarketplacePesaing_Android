package com.bornewtech.marketplacepesaing.ui.transaksi

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bornewtech.marketplacepesaing.data.adapter.AdapterTransaksi
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bornewtech.marketplacepesaing.data.firestoreDb.Transaction
import com.bornewtech.marketplacepesaing.databinding.ActivityTransaksiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class Transaksi : AppCompatActivity() {

    private lateinit var binding: ActivityTransaksiBinding
    private lateinit var cartItems: List<CartItem>
    private lateinit var authUser: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        authUser = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Terima data dari aktivitas sebelumnya
        cartItems = intent.getParcelableArrayListExtra("cartItems") ?: emptyList()

        // Set up RecyclerView dengan adapter yang sesuai
        val adapter = AdapterTransaksi(cartItems)
        binding.rvProductsTransaksi.layoutManager = LinearLayoutManager(this)
        binding.rvProductsTransaksi.adapter = adapter

        // Tampilkan total harga
        val totalHarga = calculateTotalPrice(cartItems)
        binding.tvTotalPrice.text = "Total Harga: Rp ${totalHarga},00"

        // Contoh: Proses transaksi (Opsional)
        processTransaction(cartItems)

        binding.btnImgAddAlamat.setOnClickListener {
            startActivity(Intent(this, Alamat::class.java))
        }

        binding.buttonPembayaran.setOnClickListener {
            // Redirect ke Pembayaran Activity dengan membawa IDTransaksi
            redirectToPembayaran(totalHarga)
        }
    }

    private fun calculateTotalPrice(cartItems: List<CartItem>): Int {
        return cartItems.sumByDouble { it.productPrice * it.productQuantity }.toInt()
    }

    private fun processTransaction(cartItems: List<CartItem>) {
        getLocationFromFirebase { latitude, longitude ->
            saveTransactionToFirestore(cartItems, latitude, longitude)
        }
    }

    private fun saveTransactionToFirestore(cartItems: List<CartItem>, latitude: Double, longitude: Double) {
        val currentUser = authUser.currentUser
        val pembeliId = currentUser?.uid

        if (pembeliId != null) {
            val totalHarga = calculateTotalPrice(cartItems)

            val transaction = Transaction(
                idTransaksi = UUID.randomUUID().toString(),
                cartItems = cartItems,
                jumlahHarga = totalHarga,
                latitude = latitude,
                longitude = longitude,
                timestamp = System.currentTimeMillis()
            )

            val firestoreReference = firestore.collection("Transaksi").document(pembeliId)
            firestoreReference.set(transaction)
                .addOnSuccessListener {
                    // Handle jika penyimpanan berhasil
                    // ...
                }
                .addOnFailureListener { e ->
                    // Handle jika penyimpanan gagal
                    // ...
                }
        }
    }

    private fun redirectToPembayaran(totalHarga: Int) {
        val intent = Intent(this, Pembayaran::class.java)
        intent.putExtra("totalHarga", totalHarga)
        startActivity(intent)
    }

    private fun getLocationFromFirebase(callback: (latitude: Double, longitude: Double) -> Unit) {
        // Get the user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Get a reference to the database
            val databaseReference = FirebaseDatabase.getInstance().reference

            // Get location data from Firebase Realtime Database
            databaseReference.child("userLocations").child(userId)
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    // Check if data is obtained successfully
                    if (dataSnapshot.exists()) {
                        // Get latitude and longitude values from the data
                        val latitude = dataSnapshot.child("latitude").value as Double
                        val longitude = dataSnapshot.child("longitude").value as Double

                        // Call the callback with location data
                        callback.invoke(latitude, longitude)
                    }
                }
                .addOnFailureListener {
                    // Failed to get location data
                    // You might want to handle this case or provide a default location
                    callback.invoke(0.0, 0.0)
                }
        } else {
            // User ID is null, handle accordingly
            // You might want to handle this case or provide a default location
            callback.invoke(0.0, 0.0)
        }
    }
}
