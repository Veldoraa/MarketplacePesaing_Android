package com.bornewtech.marketplacepesaing.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityResetPasswordBinding
import com.bornewtech.marketplacepesaing.ui.login.Login
import com.google.firebase.auth.FirebaseAuth

class ResetPassword : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.imageCloseResetPw.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }

        auth = FirebaseAuth.getInstance()
        binding.btnKonfResetPassword.setOnClickListener {
            val email = binding.emailEd.text.toString()
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    startActivity(Intent(this, Login::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                }
        }
    }
}