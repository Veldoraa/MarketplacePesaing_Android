package com.bornewtech.marketplacepesaing.data.maps
import com.google.firebase.database.*

class FirebaseHelper {

//    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

//    fun getData(callback: (List<LatLng>) -> Unit) {
//        val yourDataList = mutableListOf<LatLng>()
//
//        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (dataSnapshot in snapshot.children) {
//                    val yourData = dataSnapshot.getValue(LatLng::class.java)
//                    yourData?.let { yourDataList.add(it) }
//                }
//                callback(yourDataList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Handle errors
//                callback(emptyList())
//            }
//        })
//    }

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getData(callback: (DataSnapshot) -> Unit) {
        databaseReference.child("data").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Kirim hasil data ke callback
                callback(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tangani kesalahan
            }
        })
    }

}
