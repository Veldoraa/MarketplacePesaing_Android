package com.bornewtech.marketplacepesaing.ui.transaksi

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bornewtech.marketplacepesaing.databinding.ActivityPembayaranBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class Pembayaran : AppCompatActivity() {
    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var authUser: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        authUser = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Retrieve data from intent
        val totalHarga = intent.getIntExtra("totalHarga", 0)

        // Fetch user data from Firestore and save to Firebase Realtime Database
        fetchUserDataAndSaveToDatabase(totalHarga)

        // Set up onClickListener for the "Bayar" button
        binding.buttonSelesai.setOnClickListener {
            // Perform payment logic here, then save payment status to Firebase Realtime Database
            savePaymentStatusToDatabase(userId, "sudahBayar")
        }
    }

    private fun fetchUserDataAndSaveToDatabase(totalHarga: Int) {
        // Get the user ID
        userId = authUser.currentUser?.uid

        if (userId != null) {
            // Get user data from Firestore
            firestore.collection("Pembeli").document(userId!!)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Retrieve user data
                        val namaPembeli = documentSnapshot.getString("namaLengkap")
                        binding.tvNamaPembeli.text = "$namaPembeli"

                        // Get transaction data from Firestore
                        firestore.collection("Transaksi").document(userId!!)
                            .get()
                            .addOnSuccessListener { documentSnapshotTransaksi ->
                                if (documentSnapshotTransaksi.exists()) {
                                    // Retrieve transaction data
                                    val idTransaksi = documentSnapshotTransaksi.getString("idTransaksi")

                                    // Trim the ID to 12 characters from the end and make it UPPERCASE
                                    val trimmedIdTransaksi = idTransaksi?.takeLast(12)?.toUpperCase()
                                    binding.tvIdTransaksi.text = "Transaksi: #$trimmedIdTransaksi"

                                    // Set totalHarga to TextView
                                    binding.tvTotalPrice.text = "Total Harga: Rp $totalHarga,00"

                                    // Save data to Firebase Realtime Database
                                    saveDataToDatabase(userId!!, namaPembeli, trimmedIdTransaksi, totalHarga)
                                } else {
                                    Log.d("Pembayaran", "Transaction data not found.")
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("Pembayaran", "Error fetching transaction data: $e")
                            }
                    } else {
                        Log.d("Pembayaran", "User data not found.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Pembayaran", "Error fetching user data: $e")
                }

        } else {
            Log.d("Pembayaran", "User ID is null.")
        }
    }

    private fun saveDataToDatabase(userId: String, namaPembeli: String?, trimmedIdTransaksi: String?, totalHarga: Int) {
        // Save data to Firebase Realtime Database
        val databaseReference = FirebaseDatabase.getInstance().getReference("Transaksi/$userId")
        databaseReference.child("namaPembeli").setValue(namaPembeli)
        databaseReference.child("idTransaksi").setValue(trimmedIdTransaksi)
        databaseReference.child("totalHarga").setValue(totalHarga)
    }

    private fun savePaymentStatusToDatabase(userId: String?, status: String) {
        if (userId != null) {
            // Save payment status to Firebase Realtime Database
            val databaseReference = FirebaseDatabase.getInstance().getReference("Transaksi/$userId")
            databaseReference.child("status").setValue(status)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data pembayaran berhasil disimpan", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Log.e("Pembayaran", "Error saving payment status: $e")
                    Toast.makeText(this, "Gagal menyimpan data pembayaran", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.d("Pembayaran", "User ID is null.")
        }
    }
}
