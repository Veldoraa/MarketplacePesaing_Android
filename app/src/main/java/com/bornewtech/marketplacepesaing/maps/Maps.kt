//package com.bornewtech.marketplacepesaing.maps
//
//import android.content.DialogInterface
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Location
//import android.os.Bundle
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import com.bornewtech.marketplacepesaing.R
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.Marker
//import com.google.android.gms.maps.model.MarkerOptions
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.firestore.FirebaseFirestore
//import kotlin.math.acos
//import kotlin.math.cos
//import kotlin.math.sin
//
//class Maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
//
//    private lateinit var mMap: GoogleMap
//    private lateinit var database: FirebaseDatabase
//    private lateinit var pedagangRef: DatabaseReference
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    private lateinit var currentUserLocation: Location
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
//        supportActionBar?.hide()
//
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        // Initialize Firebase Realtime Database
//        database = FirebaseDatabase.getInstance()
//        pedagangRef = database.reference.child("userLocations").child("pedagang")
//
//        // Initialize Firestore
//        firestore = FirebaseFirestore.getInstance()
//
//        // Initialize FusedLocationProviderClient
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        // Initialize current user location
//        currentUserLocation = Location("")
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
//        val minZoomLevel = 13.0f
//        mMap.setMinZoomPreference(minZoomLevel)
//
//        // Set the camera position to Pontianak
//        val pontianak = LatLng(-0.02800127398174045, 109.34220099978418)
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pontianak, 15f))
//
//        // Add a marker at the user's current location
//        enableMyLocation()
//
//        // Retrieve pedagang location from Firebase Realtime Database
//        pedagangRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (pedagangSnapshot in dataSnapshot.children) {
//                    val lat = pedagangSnapshot.child("latitude").getValue(Double::class.java)
//                    val lng = pedagangSnapshot.child("longitude").getValue(Double::class.java)
//                    val userId = pedagangSnapshot.key
//                    if (lat != null && lng != null && userId != null) {
//                        // Retrieve namaLengkap from Firestore
//                        firestore.collection("Pedagang").document(userId)
//                            .get()
//                            .addOnSuccessListener { document ->
//                                val namaLengkap = document.getString("namaLengkap")
//                                if (namaLengkap != null) {
//                                    val pedagangLocation = LatLng(lat, lng)
//                                    val distance = calculateDistance(currentUserLocation.latitude, currentUserLocation.longitude, lat, lng)
//                                    if (distance <= 50000) { // Check if distance is within 500 meters
//                                        mMap.addMarker(MarkerOptions().position(pedagangLocation).title(namaLengkap))
//                                    }
//                                }
//                            }
//                            .addOnFailureListener { exception ->
//                                // Handle failure to retrieve namaLengkap
//                                Toast.makeText(this@Maps, "Failed to retrieve namaLengkap: $exception", Toast.LENGTH_SHORT).show()
//                            }
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle error
//                Toast.makeText(this@Maps, "Failed to retrieve pedagang location", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//        // Set the marker click listener
//        mMap.setOnMarkerClickListener(this)
//    }
//
//    override fun onMarkerClick(marker: Marker): Boolean {
//        // Handle marker click if needed
//        return false
//    }
//
//    private fun calculateDistance(
//        lat1: Double,
//        lon1: Double,
//        lat2: Double,
//        lon2: Double
//    ): Double {
//        val theta = lon1 - lon2
//        var dist = sin(Math.toRadians(lat1)) * sin(Math.toRadians(lat2)) +
//                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
//                cos(Math.toRadians(theta))
//        dist = acos(dist)
//        dist = Math.toDegrees(dist)
//        dist *= 60 * 1.1515 * 1.609344 * 1000 // Convert to meters
//        return dist
//    }
//
//    private fun enableMyLocation() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            mMap.isMyLocationEnabled = true
//            // Get current user location
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    // Got last known location. In some rare situations this can be null.
//                    if (location != null) {
//                        currentUserLocation = location
//                    }
//                }
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                enableMyLocation()
//            } else {
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
//    }
//}



package com.bornewtech.marketplacepesaing.maps

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.ui.barang.recyclerview.RecViewBarang
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var database: FirebaseDatabase
    private lateinit var pedagangRef: DatabaseReference
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentUserLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance()
        pedagangRef = database.reference.child("userLocations").child("pedagang")

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize current user location
        currentUserLocation = Location("")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val minZoomLevel = 13.0f
        mMap.setMinZoomPreference(minZoomLevel)

        // Set the camera position to Pontianak
        val pontianak = LatLng(-0.02800127398174045, 109.34220099978418)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pontianak, 15f))

        // Add a marker at the user's current location
        enableMyLocation()

        // Retrieve pedagang location from Firebase Realtime Database
        pedagangRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (pedagangSnapshot in dataSnapshot.children) {
                    val lat = pedagangSnapshot.child("latitude").getValue(Double::class.java)
                    val lng = pedagangSnapshot.child("longitude").getValue(Double::class.java)
                    val userId = pedagangSnapshot.key
                    if (lat != null && lng != null && userId != null) {
                        // Calculate distance between user and pedagang using Haversine formula
                        val distance = calculateDistance(currentUserLocation.latitude, currentUserLocation.longitude, lat, lng)
                        if (distance >= 1000) { // Cek jika dengan jarak 1000 meters radius
                            // Retrieve namaLengkap from Firestore
                            firestore.collection("Pedagang").document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    val namaLengkap = document.getString("namaLengkap")
                                    if (namaLengkap != null) {
                                        val pedagangLocation = LatLng(lat, lng)
                                        val marker = mMap.addMarker(MarkerOptions().position(pedagangLocation).title(namaLengkap))
                                        if (marker != null) {
                                            marker.tag = userId
                                        } // Attach userId to marker for later use
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    // Handle failure to retrieve namaLengkap
                                    Toast.makeText(this@Maps, "Failed to retrieve namaLengkap: $exception", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Toast.makeText(this@Maps, "Failed to retrieve pedagang location", Toast.LENGTH_SHORT).show()
            }
        })

        // Set the marker click listener
        mMap.setOnMarkerClickListener(this)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c * 1000 // convert to meters
        return distance
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val userId = marker.tag as String? // Retrieve userId from marker
        val namaLengkap = marker.title // Retrieve namaLengkap from marker
        val pedagangLocation = Location("")
        pedagangLocation.latitude = marker.position.latitude
        pedagangLocation.longitude = marker.position.longitude
        val distance = currentUserLocation.distanceTo(pedagangLocation)
        if (userId != null && namaLengkap != null) {
            // Handle marker click, navigate to RecyclerViewBarang
            showAlertDialog(userId, namaLengkap, distance)
        }
        return true
    }

    private fun showAlertDialog(userId: String, namaLengkap: String, distance: Float) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Pilih Barang dari $namaLengkap")
        alertDialogBuilder.setMessage("Anda ingin memilih barang dari pedagang ini?\nJarak Anda dari pedagang ini: ${distance} meter")
        alertDialogBuilder.setPositiveButton("Ya") { dialogInterface: DialogInterface, i: Int ->
            // Navigasi ke RecyclerViewBarang dengan userId yang dipilih
            val intent = Intent(this, RecViewBarang::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        alertDialogBuilder.setNegativeButton("Tidak", null)
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            // Get current user location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        currentUserLocation = location
                    }
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
