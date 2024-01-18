package com.bornewtech.marketplacepesaing.ui.profile

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bornewtech.marketplacepesaing.data.camera.utility.getImageUri
import com.bornewtech.marketplacepesaing.databinding.ActivityAddPhotoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddPhoto : AppCompatActivity() {
    private lateinit var binding: ActivityAddPhotoBinding
    private var currentImageUri: Uri? = null
    private var storageRef = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    // Memulai dengan Galeri
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    // Memulai dengan Kamera
    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    // Menampilkan Gambar
    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.ivPreviewAdd.setImageURI(it)
        }
    }

    // upload image
    private fun uploadImage() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            currentImageUri?.let { uri ->
                val userId = user.uid
                val storageReference = Firebase.storage.reference.child("ProfilePembeli")
                    .child(userId)
                    .child(System.currentTimeMillis().toString())

                storageReference.putFile(uri)
                    .addOnSuccessListener { taskSnapshot ->
                        storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                            val mapImage = mapOf(
                                "url" to downloadUri.toString()
                            )
                            val databaseReferences =
                                FirebaseDatabase.getInstance().getReference("profilsImg")
                            databaseReferences.child(userId).setValue(mapImage)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(
                                        this,
                                        "Gagal mengupload gambar: ${error.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Gagal mengupload gambar: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            Toast.makeText(this, "User tidak terautentikasi", Toast.LENGTH_SHORT).show()
        }
    }
}
