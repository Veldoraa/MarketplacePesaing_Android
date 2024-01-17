package com.bornewtech.marketplacepesaing.ui.transaksi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityAlamatBinding
import com.bornewtech.marketplacepesaing.ui.profile.EditProfil
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.Locale

class Alamat : AppCompatActivity() {
    private lateinit var binding: ActivityAlamatBinding
    private var dbProfil  = Firebase.firestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()

        binding.btnupdateCurrentLoc.setOnClickListener {
            requestLocationPermission()
        }

    }
    // Nerima data dari firestore
    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val refProfil = dbProfil.collection("Pembeli").document(userId)
        refProfil.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val alamat = documentSnapshot.getString("alamatLengkap")
                    binding.jalanRumah.setText(alamat)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
            }
    }

    // Menampilkan lokasi saat tombol ditekan
    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
        {
            checkGps()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                EditProfil.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun checkGps() {
        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(
            this.applicationContext
        )
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(
                    ApiException::class.java
                )
                getUserLocation()
            } catch (e : ApiException){
                e.printStackTrace()
                when (e.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        try {
                            val resolveApiException = e as ResolvableApiException
                            resolveApiException.startResolutionForResult(this,
                                EditProfil.LOCATION_PERMISSION_REQUEST_CODE
                            )
                        } catch (sendIntentException: IntentSender.SendIntentException){

                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnCompleteListener { task ->
            val location = task.getResult()
            if (location != null ){
                try {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val addressLine = address?.get(0)?.getAddressLine(0)
                    binding.edCurrentLoc.setText(addressLine)
                    val addressLocation = address?.get(0)?.getAddressLine(0)
                    openLocation(addressLocation.toString())
                } catch (e : IOException) {

                }
            }
        }
    }

    private fun openLocation(location: String) {
        binding.edCurrentLoc.setOnClickListener {
            if (!binding.edCurrentLoc.text.isNotEmpty()) {
                val uri = Uri.parse("geo:0, 0?:=$location")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
        }
    }

    private fun requestLocationUpdates() {
        // Check location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // If permission is granted, get the current location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Check if location is obtained successfully
                    if (location != null) {
                        // Use the current location
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // Save location to Firebase Realtime Database
                        saveLocationToFirebase(latitude, longitude)
                    }
                }
        } else {
            // If not, request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                EditProfil.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun saveLocationToFirebase(latitude: Double, longitude: Double) {
        // Get the user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Get a reference to the database
            val databaseReference = FirebaseDatabase.getInstance().reference

            // Create a location object to be saved in the database
            val locationData = hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )

            // Save the location to Firebase Realtime Database
            databaseReference.child("userLocations").child(userId).setValue(locationData)
                .addOnSuccessListener {
                    // Successful storage
                    // Add appropriate actions or feedback if needed
                    // Retrieve and display location data in a TextView if needed
                    retrieveAndDisplayLocation()
                }
                .addOnFailureListener {
                    // Failed storage
                    // Add appropriate actions or feedback if needed
                }
        }
    }

    private fun retrieveAndDisplayLocation() {
        // Get the user ID
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Get a reference to the database
            val databaseReference = FirebaseDatabase.getInstance().reference

            // Get location data from Firebase Realtime Database
            databaseReference.child("userLocations").child(userId)
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    // Check if data is obtained successfully
                    if (dataSnapshot.exists()) {
                        // Get latitude and longitude values from the data
                        val latitude = dataSnapshot.child("latitude").value as Double
                        val longitude = dataSnapshot.child("longitude").value as Double

                        // Display data in a TextView (e.g., inpCurrentLocProfil)
                        binding.edCurrentLoc.setText("Latitude: $latitude, Longitude: $longitude")
                    }
                }
                .addOnFailureListener {
                    // Failed to get location data
                }
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}