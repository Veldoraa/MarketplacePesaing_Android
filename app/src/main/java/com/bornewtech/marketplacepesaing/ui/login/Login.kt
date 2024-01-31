package com.bornewtech.marketplacepesaing.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityLoginBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.bornewtech.marketplacepesaing.ui.register.Registrasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth

        // tombol ke registrasi
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, Registrasi::class.java))
        }
        //tombol untuk login
        binding.btnLogin.setOnClickListener {loginUser()}
    }

    private fun loginUser(){
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        if (validateForm(email, password)){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                binding.tilemail.error = "Masukkan Email"
                false
            }

            TextUtils.isEmpty(password) -> {
                binding.tilpassword.error = "Masukkan Password"
                false
            }

            else -> {
                true
            }
        }
    }

}
