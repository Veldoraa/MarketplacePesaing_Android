package com.bornewtech.marketplacepesaing.data.firestoreDb

import java.io.Serializable

data class Products(
    val productList: List<ProductItem> = emptyList()
)

data class ProductItem(
    val userId: String? = null,
    val produkStok: String? = null,
    val produkNama: String? = null,
    val produkKategori: String? = null,
    val produkSatuan: String? = null,
    val produkHarga: String? = null
) : Serializable
