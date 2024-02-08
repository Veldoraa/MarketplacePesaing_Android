package com.bornewtech.marketplacepesaing.data.firestoreDb

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val productId: String?,
    val productName: String,
    val productPrice: Double,
    var productQuantity: Int,
    var pedagangId: String?,
    var pembeliId: String?,
    val imageUrl: String? // Tambahkan properti imageUrl untuk menyimpan URL gambar produk
) : Parcelable {

    // Tambahkan ini di dalam kelas CartItem
    // Updated toMap function
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "produkId" to productId,
            "productName" to productName,
            "productQuantity" to productQuantity,
            "productPrice" to productPrice,
            "pedagangId" to pedagangId,
            "pembeliId" to pembeliId,
            "imageUrl" to imageUrl // Sertakan imageUrl dalam map
        )
    }

    // Fungsi untuk menambah kuantitas
    fun incrementQuantity() {
        productQuantity++
    }

    // Fungsi untuk mengurangi kuantitas
    fun decrementQuantity() {
        if (productQuantity > 0) {
            productQuantity--
        }
    }

    override fun toString(): String {
        return "$productId,$productName,$productPrice,$productQuantity,$pedagangId,$pembeliId,$imageUrl"
    }

    companion object {
        fun fromString(data: String): CartItem {
            val splitData = data.split(",")

            return try {
                CartItem(
                    splitData[0],          // Index 0
                    splitData[1],          // Index 1
                    splitData[2].toDoubleOrNull() ?: 0.0, // Index 2, handle potential null
                    splitData[3].toIntOrNull() ?: 0,    // Index 3, handle potential null
                    splitData.getOrNull(4),             // Index 4, handle potential null
                    splitData.getOrNull(5),
                    splitData.getOrNull(6)             // Index 5, handle potential null
                )
            } catch (e: Exception) {
                // Handle the case where conversion fails, log the error, and return a default CartItem.
                Log.e("CartItem", "Error parsing CartItem from string: $data", e)
                CartItem(null, "", 0.0, 0, null, null, null)
            }
        }
    }
}
