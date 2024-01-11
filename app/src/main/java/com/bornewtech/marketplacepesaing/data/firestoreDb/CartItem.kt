package com.bornewtech.marketplacepesaing.data.firestoreDb

data class CartItem(
    val productId: String?,
    val productName: String,
    val productPrice: Double,
    var productQuantity: Int // Ubah menjadi var agar dapat diubah
) {
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
        return "$productId,$productName,$productPrice,$productQuantity"
    }

    companion object {
        fun fromString(data: String): CartItem {
            val splitData = data.split(",")
            return CartItem(
                splitData[0],
                splitData[1],
                splitData[2].toDouble(),
                splitData[3].toInt()
            )
        }
    }
}