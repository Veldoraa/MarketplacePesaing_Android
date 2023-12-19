package com.bornewtech.marketplacepesaing.data.maps

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.heatmaps.WeightedLatLng

object Constants {


    /**DUMMY DATA**/
    fun getPolylineCords(): ArrayList<LatLng> {
        return arrayListOf(
            LatLng(-0.045568, 109.366555),
            LatLng(-0.043392, 109.364330)
        )
    }

    fun getPolygonCords(): ArrayList<LatLng> {
        return arrayListOf(
            LatLng(-0.038153, 109.367337),
            LatLng(-0.032276, 109.366992)
        )
    }

    fun getHeatmapData(callback: (ArrayList<WeightedLatLng>) -> Unit) {
        val firebaseHelper = FirebaseHelper()
        firebaseHelper.getData { dataSnapshot ->
            val data = dataSnapshot.getValue(RealtimeLatLng::class.java)
//            println("Data from Firebase: $data")
            val cluster = data?.cluster ?: 0
            val jumlah = data?.jumlah ?: 0
            val latitude = data?.lat ?: 0.0
            val longitude = data?.lng ?: 0.0

            // Gunakan dataSnapshot untuk mendapatkan data dari Firebase
            // Contoh: dataSnapshot.getValue(NamaKelas::class.java)
            val heatmapData = ArrayList<WeightedLatLng>()
            heatmapData.add(WeightedLatLng(LatLng(latitude, longitude), cluster.toDouble()))

            // Call the callback with the result
            callback(heatmapData)
        }
    }

//    fun getHeatmapData(): ArrayList<WeightedLatLng> {
//        val heatmapData = ArrayList<WeightedLatLng>()
//
//        for (i in 1..300) {
//            val cluster = Random.nextInt(1, 25)
//            val latitude = Random.nextDouble(-0.06, -0.01)
//            val longitude = Random.nextDouble(109.3, 109.4)
//            heatmapData.add(WeightedLatLng(LatLng(latitude, longitude), cluster.toDouble()))
//        }
//        return heatmapData
//    }
}
