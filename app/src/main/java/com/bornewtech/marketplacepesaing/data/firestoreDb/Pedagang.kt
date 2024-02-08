package com.bornewtech.marketplacepesaing.data.firestoreDb

import java.io.Serializable

data class Pedagang(
    val userId: String? = null,
    var imageUrl: String = "",
    val alamatLengkap: String? = null,
    val namaLengkap: String? = null,
    val noHpAktif: String? = null
) : Serializable
