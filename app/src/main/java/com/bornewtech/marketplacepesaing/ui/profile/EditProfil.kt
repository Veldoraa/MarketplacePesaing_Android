package com.bornewtech.marketplacepesaing.ui.profile

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
import com.bornewtech.marketplacepesaing.databinding.ActivityEditProfilBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.Locale

class EditProfil : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilBinding
    private var dbProfil  = Firebase.firestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var storageRef = Firebase.storage
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.addImgProfil.setOnClickListener {
            startActivity(Intent(this, AddPhoto::class.java))
        }
        binding.backToProfil.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }
        binding.btnGetLoc.setOnClickListener {
            checkLocationPermission()
        }
        setData()
//        binding.addImgProfil.setOnClickListener { startCamera() }

        binding.btnSimpanProfil.setOnClickListener {

            requestLocationUpdates()
            val nameProfil = binding.inpNamaProfil.text.toString().trim()
            val noHpProfil = binding.inpNoHpProfil.text.toString().trim()
            val emailProfil = binding.inpEmailProfil.text.toString().trim()
            val alamatProfil = binding.inpAlamatProfil.text.toString().trim()

            val profilMap = hashMapOf(
                "namaLengkap" to nameProfil,
                "noHpAktif" to noHpProfil,
                "email" to emailProfil,
                "alamatLengkap" to alamatProfil
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbProfil.collection("Pembeli").document(userId).set(profilMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
                    binding.inpNamaProfil.text
                    binding.inpNoHpProfil.text
                    binding.inpAlamatProfil.text
                    binding.inpAlamatProfil.text
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
                }
        }

//        currentImageUri?.let { uri ->
//            storageRef.getReference("Gambar Barang")
//                .child(System.currentTimeMillis().toString())
//                .putFile(uri)
//                .addOnSuccessListener {
//                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
//                    val mapImage = mapOf(
//                        "url" to it.toString()
//                    )
//                    val databaseReferences =
//                        FirebaseDatabase.getInstance().getReference("gambarBarang")
//                    databaseReferences.child(userId).setValue(mapImage)
//                        .addOnSuccessListener {
//                            Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
//                        }
//                        .addOnFailureListener { error ->
//                            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
//                        }
//                }
//
//        }

//        // upload gambar dengan Kamera
//        private fun startCamera() {
//            currentImageUri = getImageUri(this)
//            launcherIntentCamera.launch(currentImageUri)
//        }
//        // ngelaunch camera
//        private val launcherIntentCamera = registerForActivityResult(
//            ActivityResultContracts.TakePicture()
//        ) { isSuccess ->
//            if (isSuccess) {
//                showImage()
//            }
//        }
//        // Menampilkan Gambar di Tampilan kotak image
//        private fun showImage() {
//            currentImageUri?.let {
//                Log.d("Image URI", "showImage: $it")
//                binding.imgUser.setImageURI(it)
//            }
//        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
        {
            checkGps()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
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
                            resolveApiException.startResolutionForResult(this, LOCATION_PERMISSION_REQUEST_CODE)
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
                    binding.inpCurrentLocProfil.setText(addressLine)
                    val addressLocation = address?.get(0)?.getAddressLine(0)
                    openLocation(addressLocation.toString())
                } catch (e : IOException) {

                }
            }
        }
    }

    private fun openLocation(location: String) {
        binding.inpCurrentLocProfil.setOnClickListener {
            if (!binding.inpCurrentLocProfil.text.isNotEmpty()) {
                val uri = Uri.parse("geo:0, 0?:=$location")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            }
        }
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val refProfil = dbProfil.collection("Pembeli").document(userId)
        refProfil.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val nama = documentSnapshot.getString("namaLengkap")
                    val noHp = documentSnapshot.getString("noHpAktif")
                    val email = documentSnapshot.getString("email")
                    val alamat = documentSnapshot.getString("alamatLengkap")

                    binding.inpNamaProfil.setText(nama)
                    binding.inpNoHpProfil.setText(noHp)
                    binding.inpEmailProfil.setText(email)
                    binding.inpAlamatProfil.setText(alamat)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
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
                LOCATION_PERMISSION_REQUEST_CODE
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
                        binding.inpCurrentLocProfil.setText("Latitude: $latitude, Longitude: $longitude")
                    }
                }
                .addOnFailureListener {
                    // Failed to get location data
                }
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}