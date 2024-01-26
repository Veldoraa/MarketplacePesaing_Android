package com.bornewtech.marketplacepesaing.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.adapter.AdapterTransaksi
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bornewtech.marketplacepesaing.data.firestoreDb.Transaction
import com.bornewtech.marketplacepesaing.databinding.ActivityTransaksiBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class Transaksi : AppCompatActivity() {

    private lateinit var binding: ActivityTransaksiBinding
    private lateinit var cartItems: List<CartItem>
    private lateinit var authUser: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Implementasikan logika tambahan sesuai kebutuhan

        // Contoh: Proses transaksi (Opsional)
        processTransaction(cartItems)
    }

    // Metode untuk menghitung total harga
    private fun calculateTotalPrice(cartItems: List<CartItem>): Int {
        return cartItems.sumByDouble { it.productPrice * it.productQuantity }.toInt()
    }

    // Metode untuk melakukan proses transaksi (Opsional)
    private fun processTransaction(cartItems: List<CartItem>) {
        // Implementasikan logika tambahan sesuai kebutuhan
        // Contoh: Kirim notifikasi, kurangi stok produk, dll.
        // ...

        // Setelah proses transaksi selesai, Anda dapat menyimpan data transaksi ke Firestore.
        saveTransactionToFirestore(cartItems)

        // Anda juga dapat menambahkan logika untuk menampilkan pesan konfirmasi atau mengarahkan pengguna ke halaman lain.
        // Contoh: Menampilkan pesan konfirmasi
        Toast.makeText(this, "Transaksi berhasil! Terima kasih!", Toast.LENGTH_SHORT).show()

        // Kembali ke halaman utama atau halaman lain yang sesuai
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Metode untuk menyimpan data transaksi ke Firestore
    private fun saveTransactionToFirestore(cartItems: List<CartItem>) {
        val currentUser = authUser.currentUser
        val pembeliId = currentUser?.uid

        if (pembeliId != null) {
            // Membuat objek Transaction
            val transaction = Transaction(
                idTransaksi = UUID.randomUUID().toString(),
//                pembeliId = pembeliId,
                cartItems = cartItems,
                timestamp = System.currentTimeMillis()
            )

            // Menyimpan objek Transaction ke Firestore
            transaction.idTransaksi?.let {
                firestore.collection("Transaksi").document(it)
                    .set(transaction)
                    .addOnSuccessListener {
                        // Handle jika penyimpanan berhasil
                        // ...
                    }
                    .addOnFailureListener {
                        // Handle jika penyimpanan gagal
                        // ...
                    }
            }
        }
    }
}