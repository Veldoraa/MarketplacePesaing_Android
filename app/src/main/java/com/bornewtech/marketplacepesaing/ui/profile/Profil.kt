package com.bornewtech.marketplacepesaing.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityProfilBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.bornewtech.marketplacepesaing.ui.login.PraLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Profil : AppCompatActivity() {
    private lateinit var binding: ActivityProfilBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var dbProfil = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

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

                    binding.namaPengguna.setText(nama)
                    binding.noPengguna.setText(noHp)

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
}