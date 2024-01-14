package com.bornewtech.marketplacepesaing.helper

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHelper {
    // ...

    // Mendapatkan ID pedagang berdasarkan ID produk
    fun getPedagangIdByProdukId(produkId: String, onComplete: (String?) -> Unit) {
        val productsCollection = FirebaseFirestore.getInstance().collection("Products")

        // Query untuk mencari produk dengan idProduk yang sesuai
        val query = productsCollection.whereArrayContains("productList", mapOf("idProduk" to produkId))

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Ambil data pertama karena produkId seharusnya unik
                    val documentSnapshot = querySnapshot.documents[0]

                    // Dapatkan array productList dari dokumen
                    val productList = documentSnapshot.get("productList") as? ArrayList<*>

                    // Cari objek dengan idProduk yang sesuai
                    val matchingProduct = productList?.firstOrNull {
                        (it as? Map<*, *>)?.get("idProduk") == produkId
                    } as? Map<*, *>?

                    // Ambil pedagangId jika objek ditemukan
                    val pedagangId = matchingProduct?.get("pedagangId") as? String

                    onComplete(pedagangId)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener { exception ->
                onComplete(null)
            }
    }
}