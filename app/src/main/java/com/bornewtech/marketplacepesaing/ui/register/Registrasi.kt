package com.bornewtech.marketplacepesaing.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityRegistrasiBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Registrasi : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrasiBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth
        binding.btnRegister.setOnClickListener {registerUser()}
    }

    // registrasi Pengguna
    private fun registerUser(){
        val nama = binding.etMitraname.text.toString()
        val noHp = binding.etMitraphone.text.toString()
        val email = binding.etMitraemail.text.toString()
        val password = binding.etMitrapassword.text.toString()
        val konfirmPassword = binding.etConfirmmitrapassword.text.toString()

        if (validateForm(nama, noHp, email, password, konfirmPassword)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "User Id telah Terbuat", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    //fungsi untuk validasi form pengisian
    private fun validateForm(nama: String, noHp: String, email: String, password: String, konfirmPassword: String): Boolean {
        return when {
            TextUtils.isEmpty(nama) -> {
                binding.tilname.error = "Masukkan Nama"
                false
            }
            TextUtils.isEmpty(noHp) -> {
                binding.tilphone.error = "Masukkan Nomor Hp"
                false
            }
            TextUtils.isEmpty(email) -> {
                binding.tilemail.error = "Masukkan Email"
                false
            }
            TextUtils.isEmpty(password) -> {
                binding.tilpassword.error = "Masukkan Password"
                false
            }
            TextUtils.isEmpty(konfirmPassword) -> {
                binding.tilkonfirmpassword.error = "Konfirmasi Password"
                false
            }
            else -> {true}
        }
    }
}