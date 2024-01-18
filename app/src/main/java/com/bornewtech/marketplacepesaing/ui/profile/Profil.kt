package com.bornewtech.marketplacepesaing.ui.profile

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityProfilBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.bornewtech.marketplacepesaing.ui.login.PraLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

class Profil : AppCompatActivity() {
    private lateinit var binding: ActivityProfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var dbProfil = Firebase.firestore
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        storageReference = FirebaseStorage.getInstance().reference

        setData()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.editProfil.setOnClickListener {
            startActivity(Intent(this, EditProfil::class.java))
        }
        binding.backToDP.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnGantiPassword.setOnClickListener {
            startActivity(Intent(this, ResetPassword::class.java))
            finish()
        }

        binding.btnLogoutPdg.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, PraLogin::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setData() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val refProfil = dbProfil.collection("Pembeli").document(userId)

        refProfil.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val nama = documentSnapshot.getString("namaLengkap")
                    val noHp = documentSnapshot.getString("noHpAktif")

                    binding.namaPengguna.text = nama
                    binding.noPengguna.text = noHp

                    // Load and display profile image
                    loadProfileImage(userId, binding.imgUser)

                    // Tambahkan ID dokumen sebagai field "userId"
                    val userIdField = documentSnapshot.id
                    refProfil.update("userId", userIdField)
                        .addOnSuccessListener {
                            // Field "userId" berhasil ditambahkan
                            Toast.makeText(this, "Data profil berhasil dimuat", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            // Gagal menambahkan field "userId"
                            Toast.makeText(this, "Gagal menambahkan field userId", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                // Gagal mendapatkan data profil
                Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileImage(userId: String, imageView: CircleImageView) {
        val profileImageRef = storageReference?.child("ProfilePembeli")?.child(userId)?.child("FotoProfil.jpg")

        profileImageRef?.getBytes(1024 * 1024) // Max size of image: 1MB
            ?.addOnSuccessListener { bytes ->
                // Successfully loaded image
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageView.setImageBitmap(bitmap)
            }
            ?.addOnFailureListener {
                // Failed to load image
                imageView.setImageResource(R.drawable.ic_baseline_circle_24) // Default image if loading fails
            }
    }
}