package com.bornewtech.marketplacepesaing.data.firestoreDb

import android.util.Log

data class CartItem(
    val productId: String?,
    val productName: String,
    val productPrice: Double,
    var productQuantity: Int,
    var pedagangId: String? = null
) {

    // Tambahkan ini di dalam kelas CartItem
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "producytId" to productId,
            "productName" to productName,
            "productQuantity" to productQuantity,
            "productPrice" to productPrice,
            "pedagangId" to pedagangId
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
        return "$productId,$productName,$productPrice,$productQuantity,$pedagangId"
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
                    splitData.getOrNull(4)             // Index 4, handle potential null
                )
            } catch (e: Exception) {
                // Handle the case where conversion fails, log the error, and return a default CartItem.
                Log.e("CartItem", "Error parsing CartItem from string: $data", e)
                CartItem(null, "", 0.0, 0, null)
            }
        }
    }


}
