package com.bornewtech.marketplacepesaing.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
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
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class AddPhoto : AppCompatActivity() {
    private lateinit var binding: ActivityAddPhotoBinding
    private var currentImageUri: Uri? = null

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
                    .child("FotoProfil.jpg") // Use a consistent file name

                val compressedImageByteArray = compressImage(uri)

                val uploadTask: UploadTask = storageReference.putBytes(compressedImageByteArray)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageReference.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
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
                    } else {
                        // Handle failures
                        Toast.makeText(
                            this,
                            "Gagal mengupload gambar: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "User tidak terautentikasi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun compressImage(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val rotation = getRotationFromExif(uri)
        val rotatedBitmap = rotateBitmap(originalBitmap, rotation)

        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Adjust quality as needed
        val compressedImageByteArray = outputStream.toByteArray()
        outputStream.close()

        return compressedImageByteArray
    }

    private fun getRotationFromExif(uri: Uri): Int {
        val inputStream = contentResolver.openInputStream(uri)
        val exif = ExifInterface(inputStream!!)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        inputStream.close()

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = android.graphics.Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}