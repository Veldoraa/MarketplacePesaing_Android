//package com.bornewtech.marketplacepesaing.ui.transaksi
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.bornewtech.marketplacepesaing.databinding.ActivityPembayaranBinding
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.firestore.FirebaseFirestore
//
//class Pembayaran : AppCompatActivity() {
//    private lateinit var binding: ActivityPembayaranBinding
//    private lateinit var authUser: FirebaseAuth
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var firebaseDatabase: FirebaseDatabase
//    private var userId: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPembayaranBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        binding.imageCloseBilling.setOnClickListener {
//            startActivity(Intent(this, Transaksi::class.java))
//        }
//
//        authUser = FirebaseAuth.getInstance()
//        firestore = FirebaseFirestore.getInstance() // Inisialisasi Firestore di sini
//        firebaseDatabase = FirebaseDatabase.getInstance()
//
//        // Retrieve data from intent
//        val totalHarga = intent.getIntExtra("totalHarga", 0)
//
//        // Fetch user data from Firestore and save to Firebase Realtime Database
//        fetchUserDataAndSaveToDatabase(totalHarga)
//
//        // Set up onClickListener for the "Bayar" button
//        binding.buttonSelesai.setOnClickListener {
//            // Perform payment logic here, then save payment status and location to Firebase Realtime Database
//            savePaymentStatusAndLocationToDatabase(userId, "Sudah Bayar")
//
//            // Save data from Firebase Realtime Database to Firestore
//            saveDataFromRealtimeDatabaseToFirestore(userId)
//        }
//    }
//
//    private fun fetchUserDataAndSaveToDatabase(totalHarga: Int) {
//        // Get the user ID
//        userId = authUser.currentUser?.uid
//
//        if (userId != null) {
//            // Get user data from Firestore
//            firestore.collection("Pembeli").document(userId!!)
//                .get()
//                .addOnSuccessListener { documentSnapshot ->
//                    if (documentSnapshot.exists()) {
//                        // Retrieve user data
//                        val namaPembeli = documentSnapshot.getString("namaLengkap")
//                        val userId = documentSnapshot.getString("userId")
//                        binding.tvNamaPembeli.text = "$namaPembeli"
//
//                        // Get transaction data from Firestore
//                        firestore.collection("Transaksi").document(userId!!)
//                            .get()
//                            .addOnSuccessListener { documentSnapshotTransaksi ->
//                                if (documentSnapshotTransaksi.exists()) {
//                                    // Retrieve transaction data
//                                    val idTransaksi = documentSnapshotTransaksi.getString("idTransaksi")
//
//                                    // Tetapkan idTransaksi yang lengkap ke TextView
//                                    binding.tvIdTransaksi.text = "Transaksi: #$idTransaksi"
//
//                                    // Trim the ID to 12 characters from the end and make it UPPERCASE
//                                    val trimmedIdTransaksi = idTransaksi?.takeLast(12)?.toUpperCase()
//
//                                    // Simpan idTransaksi yang lengkap ke Firebase Realtime Database dan Firestore
//                                    saveDataToDatabase(userId!!, namaPembeli, idTransaksi, totalHarga)
//
//                                    // Tampilkan versi yang dipotong di antarmuka pengguna
//                                    binding.tvIdTransaksi.text = "Transaksi: #$trimmedIdTransaksi"
//                                    // Set totalHarga to TextView
//                                    binding.tvTotalPrice.text = "Total Harga: Rp $totalHarga,00"
//                                } else {
//                                    Log.d("Pembayaran", "Transaction data not found.")
//                                }
//                            }
//                            .addOnFailureListener { e ->
//                                Log.e("Pembayaran", "Error fetching transaction data: $e")
//                            }
//                    } else {
//                        Log.d("Pembayaran", "User data not found.")
//                    }
//                }
//                .addOnFailureListener { e ->
//                    Log.e("Pembayaran", "Error fetching user data: $e")
//                }
//
//        } else {
//            Log.d("Pembayaran", "User ID is null.")
//        }
//    }
//
//    private fun saveDataToDatabase(userId: String, namaPembeli: String?, idTransaksi: String?, totalHarga: Int) {
//        // Save data to Firebase Realtime Database
//        val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
//        databaseReference.apply {
//            child("namaPembeli").setValue(namaPembeli)
//            child("idTransaksi").setValue(idTransaksi)
//            child("totalHarga").setValue(totalHarga)
//            child("pembeliId").setValue(userId)
//        }
//    }
//
//    private fun savePaymentStatusAndLocationToDatabase(userId: String?, status: String) {
//        if (userId != null) {
//            // Save payment status to Firebase Realtime Database
//            val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
//            databaseReference.child("status").setValue(status)
//
//            // Fetch latitude and longitude from userLocations
//            fetchUserLocationAndSaveToDatabase(userId)
//        } else {
//            Log.d("Pembayaran", "User ID is null.")
//        }
//    }
//
//    private fun fetchUserLocationAndSaveToDatabase(userId: String) {
//        firebaseDatabase.reference.child("userLocations").child("pembeli").child(userId)
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        // Retrieve latitude and longitude
//                        val latitude = snapshot.child("latitude").value as? Double
//                        val longitude = snapshot.child("longitude").value as? Double
//
//                        // Save latitude and longitude to Firebase Realtime Database
//                        saveLocationToDatabase(userId, latitude, longitude)
//                    } else {
//                        Log.d("Pembayaran", "User location data not found.")
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("Pembayaran", "Error fetching user location data: $error")
//                }
//            })
//    }
//
//    private fun saveLocationToDatabase(userId: String, latitude: Double?, longitude: Double?) {
//        if (latitude != null && longitude != null) {
//            // Save latitude and longitude to Firebase Realtime Database
//            val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
//            databaseReference.apply {
//                child("latitude").setValue(latitude)
//                child("longitude").setValue(longitude)
//            }
//
//            Toast.makeText(this, "Data pembayaran berhasil disimpan", Toast.LENGTH_SHORT).show()
//        } else {
//            Log.d("Pembayaran", "Latitude or longitude is null.")
//            Toast.makeText(this, "Gagal menyimpan data pembayaran", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun saveDataFromRealtimeDatabaseToFirestore(userId: String?) {
//        if (userId != null) {
//            val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
//            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val namaPembeli = snapshot.child("namaPembeli").value as? String
//                        val idTransaksi = snapshot.child("idTransaksi").value as? String
//                        val pembeliId = snapshot.child("pembeliId").value as? String
//                        val totalHarga = (snapshot.child("totalHarga").value as? Long)?.toInt()
//                        val status = snapshot.child("status").value as? String
//                        val latitude = snapshot.child("latitude").value as? Double
//                        val longitude = snapshot.child("longitude").value as? Double
//
//                        // Save data to Firestore
//                        val riwayatTransaksi = hashMapOf(
//                            "namaPembeli" to namaPembeli,
//                            "idTransaksi" to idTransaksi,
//                            "pembeliId" to pembeliId,
//                            "totalHarga" to totalHarga,
//                            "status" to status,
//                            "latitude" to latitude,
//                            "longitude" to longitude
//                        )
//
//                        if (namaPembeli != null && idTransaksi != null && totalHarga != null && status != null && latitude != null && longitude != null) {
//                            firestore.collection("riwayatTransaksi").document(userId)
//                                .set(riwayatTransaksi)
//                                .addOnSuccessListener {
//                                    Log.d("Pembayaran", "Data berhasil disimpan di Firestore.")
//                                }
//                                .addOnFailureListener { e ->
//                                    Log.e("Pembayaran", "Error saat menyimpan data di Firestore: $e")
//                                }
//                        } else {
//                            Log.d("Pembayaran", "Ada data yang null saat menyimpan ke Firestore.")
//                        }
//                    } else {
//                        Log.d("Pembayaran", "Data dari Realtime Database tidak ditemukan.")
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("Pembayaran", "Error fetching data from Realtime Database: $error")
//                }
//            })
//        } else {
//            Log.d("Pembayaran", "User ID is null.")
//        }
//    }
//}


package com.bornewtech.marketplacepesaing.ui.transaksi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bornewtech.marketplacepesaing.databinding.ActivityPembayaranBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class Pembayaran : AppCompatActivity() {
    private lateinit var binding: ActivityPembayaranBinding
    private lateinit var authUser: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.imageCloseBilling.setOnClickListener {
            startActivity(Intent(this, Transaksi::class.java))
        }

        authUser = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // Inisialisasi Firestore di sini
        firebaseDatabase = FirebaseDatabase.getInstance()

        // Retrieve data from intent
        val totalHarga = intent.getIntExtra("totalHarga", 0)

        // Fetch user data from Firestore and save to Firebase Realtime Database
        fetchUserDataAndSaveToDatabase(totalHarga)

        // Set up onClickListener for the "Bayar" button
        binding.buttonSelesai.setOnClickListener {
            // Perform payment logic here, then save payment status and location to Firebase Realtime Database
            savePaymentStatusAndLocationToDatabase(userId, "Sudah Bayar")

            // Save data from Firebase Realtime Database to Firestore
            saveDataFromRealtimeDatabaseToFirestore(userId)
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
                        val userId = documentSnapshot.getString("userId")
                        val alamatLengkap = documentSnapshot.getString("alamatLengkap") // Tambahan untuk alamat lengkap

                        // Save alamat lengkap to Firestore
                        if (alamatLengkap != null) {
                            saveAddressToFirestore(userId!!, alamatLengkap)
                        }

                        // Save alamat lengkap to Firebase Realtime Database
                        if (alamatLengkap != null) {
                            saveAddressToRealtimeDatabase(userId!!, alamatLengkap)
                        }

                        binding.tvNamaPembeli.text = "$namaPembeli"

                        // Get transaction data from Firestore
                        firestore.collection("Transaksi").document(userId!!)
                            .get()
                            .addOnSuccessListener { documentSnapshotTransaksi ->
                                if (documentSnapshotTransaksi.exists()) {
                                    // Retrieve transaction data
                                    val idTransaksi = documentSnapshotTransaksi.getString("idTransaksi")

                                    // Tetapkan idTransaksi yang lengkap ke TextView
                                    binding.tvIdTransaksi.text = "Transaksi: #$idTransaksi"

                                    // Trim the ID to 12 characters from the end and make it UPPERCASE
                                    val trimmedIdTransaksi = idTransaksi?.takeLast(12)?.toUpperCase()

                                    // Simpan idTransaksi yang lengkap ke Firebase Realtime Database dan Firestore
                                    saveDataToDatabase(userId!!, namaPembeli, idTransaksi, totalHarga)

                                    // Tampilkan versi yang dipotong di antarmuka pengguna
                                    binding.tvIdTransaksi.text = "Transaksi: #$trimmedIdTransaksi"
                                    // Set totalHarga to TextView
                                    binding.tvTotalPrice.text = "Total Harga: Rp $totalHarga,00"
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

    private fun saveDataToDatabase(userId: String, namaPembeli: String?, idTransaksi: String?, totalHarga: Int) {
        // Save data to Firebase Realtime Database
        val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
        databaseReference.apply {
            child("namaPembeli").setValue(namaPembeli)
            child("idTransaksi").setValue(idTransaksi)
            child("totalHarga").setValue(totalHarga)
            child("pembeliId").setValue(userId)
        }
    }

    private fun saveAddressToFirestore(userId: String, alamatLengkap: String) {
        // Save alamatLengkap to Firestore under Pembeli document with user's UID
        firestore.collection("Pembeli").document(userId)
            .update("alamatLengkap", alamatLengkap)
            .addOnSuccessListener {
                Log.d("Pembayaran", "Alamat lengkap berhasil disimpan di Firestore: $alamatLengkap")
            }
            .addOnFailureListener { e ->
                Log.e("Pembayaran", "Gagal menyimpan alamat lengkap di Firestore: $e")
            }

        // Save alamatLengkap to Firestore under riwayatTransaksi document with user's UID
        firestore.collection("riwayatTransaksi").document(userId)
            .update("alamatLengkap", alamatLengkap)
            .addOnSuccessListener {
                Log.d("Pembayaran", "Alamat lengkap berhasil disimpan di Firestore: $alamatLengkap")
            }
            .addOnFailureListener { e ->
                Log.e("Pembayaran", "Gagal menyimpan alamat lengkap di Firestore: $e")
            }
    }

    private fun saveAddressToRealtimeDatabase(userId: String, alamatLengkap: String) {
        // Save alamatLengkap to Firebase Realtime Database
        val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
        databaseReference.child("alamatLengkap").setValue(alamatLengkap)
            .addOnSuccessListener {
                Log.d("Pembayaran", "Alamat lengkap berhasil disimpan di Firebase Realtime Database: $alamatLengkap")
            }
            .addOnFailureListener { e ->
                Log.e("Pembayaran", "Gagal menyimpan alamat lengkap di Firebase Realtime Database: $e")
            }
    }

    private fun savePaymentStatusAndLocationToDatabase(userId: String?, status: String) {
        if (userId != null) {
            // Save payment status to Firebase Realtime Database
            val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
            databaseReference.child("status").setValue(status)

            // Fetch latitude and longitude from userLocations
            fetchUserLocationAndSaveToDatabase(userId)
        } else {
            Log.d("Pembayaran", "User ID is null.")
        }
    }

    private fun fetchUserLocationAndSaveToDatabase(userId: String) {
        firebaseDatabase.reference.child("userLocations").child("pembeli").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Retrieve latitude and longitude
                        val latitude = snapshot.child("latitude").value as? Double
                        val longitude = snapshot.child("longitude").value as? Double

                        // Save latitude and longitude to Firebase Realtime Database
                        saveLocationToDatabase(userId, latitude, longitude)
                    } else {
                        Log.d("Pembayaran", "User location data not found.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Pembayaran", "Error fetching user location data: $error")
                }
            })
    }

    private fun saveLocationToDatabase(userId: String, latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            // Save latitude and longitude to Firebase Realtime Database
            val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
            databaseReference.apply {
                child("latitude").setValue(latitude)
                child("longitude").setValue(longitude)
            }

            Toast.makeText(this, "Data pembayaran berhasil disimpan", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Pembayaran", "Latitude or longitude is null.")
            Toast.makeText(this, "Gagal menyimpan data pembayaran", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataFromRealtimeDatabaseToFirestore(userId: String?) {
        if (userId != null) {
            val databaseReference = firebaseDatabase.reference.child("riwayatTransaksi").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val namaPembeli = snapshot.child("namaPembeli").value as? String
                        val idTransaksi = snapshot.child("idTransaksi").value as? String
                        val pembeliId = snapshot.child("pembeliId").value as? String
                        val totalHarga = (snapshot.child("totalHarga").value as? Long)?.toInt()
                        val status = snapshot.child("status").value as? String
                        val latitude = snapshot.child("latitude").value as? Double
                        val longitude = snapshot.child("longitude").value as? Double
                        val alamatLengkap = snapshot.child("alamatLengkap").value as? String // Get alamatLengkap from Firestore

                        // Save data to Firestore
                        val riwayatTransaksi = hashMapOf(
                            "namaPembeli" to namaPembeli,
                            "idTransaksi" to idTransaksi,
                            "pembeliId" to pembeliId,
                            "totalHarga" to totalHarga,
                            "status" to status,
                            "latitude" to latitude,
                            "longitude" to longitude,
                            "alamatLengkap" to alamatLengkap // Add alamatLengkap to the HashMap
                        )

                        if (namaPembeli != null && idTransaksi != null && totalHarga != null && status != null && latitude != null && longitude != null && alamatLengkap != null) {
                            firestore.collection("riwayatTransaksi").document(userId)
                                .set(riwayatTransaksi)
                                .addOnSuccessListener {
                                    Log.d("Pembayaran", "Data berhasil disimpan di Firestore.")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Pembayaran", "Error saat menyimpan data di Firestore: $e")
                                }
                        } else {
                            Log.d("Pembayaran", "Ada data yang null saat menyimpan ke Firestore.")
                        }
                    } else {
                        Log.d("Pembayaran", "Data dari Realtime Database tidak ditemukan.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Pembayaran", "Error fetching data from Realtime Database: $error")
                }
            })
        } else {
            Log.d("Pembayaran", "User ID is null.")
        }
    }
}
