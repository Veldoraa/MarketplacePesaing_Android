package com.bornewtech.marketplacepesaing.data.firestoreDb

import java.io.Serializable

data class Transaction(
    val idTransaksi: String? = null,
//    val pedagangId: String? = null,
//    val pembeliId: String? = null,
//    val namaBarang: String? = null,
    val status: String? = null,
//    val jumlahBarang: Int = 0,
//    val produkId: String? = null,
    val lokasiLatLng: Pair<Double, Double>? = null,
    val jumlahHarga: Double = 0.0,
    val cartItems: List<CartItem>? = null,
    val timestamp: Long = 0 // Tambahkan properti timestamp
)  : Serializable
