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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class Maps : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var database: FirebaseDatabase
    private lateinit var pedagangRef: DatabaseReference
    private lateinit var firestore: FirebaseFirestore
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
                        // Retrieve namaLengkap from Firestore
                        firestore.collection("Pedagang").document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                val namaLengkap = document.getString("namaLengkap")
                                if (namaLengkap != null) {
                                    val pedagangLocation = LatLng(lat, lng)
                                    val marker = mMap.addMarker(MarkerOptions().position(pedagangLocation).title(namaLengkap))
//                                    val marker = mMap.addMarker(MarkerOptions()
//                                        .position(pedagangLocation)
//                                        .title(namaLengkap)
//                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pedagang)))

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

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Toast.makeText(this@Maps, "Failed to retrieve pedagang location", Toast.LENGTH_SHORT).show()
            }
        })

        // Set the marker click listener
        mMap.setOnMarkerClickListener(this)
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
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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