package com.bornewtech.marketplacepesaing.data.firestoreDb

data class Transaction(
    val pedagangId: String? = null,
    val pembeliId: String? = null,
    val namaBarang: String? = null,
    val status: String? = null,
    val jumlahBarang: Int = 0
)
