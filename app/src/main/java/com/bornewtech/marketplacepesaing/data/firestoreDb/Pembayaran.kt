package com.bornewtech.marketplacepesaing.data.firestoreDb

data class Pembayaran(
    val idTransaksi: String = "",
    val pembeliId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val jumlahHarga: Int = 0,
)
