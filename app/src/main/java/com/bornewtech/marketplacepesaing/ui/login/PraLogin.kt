package com.bornewtech.marketplacepesaing.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.marketplacepesaing.databinding.ActivityPraLoginBinding
import com.bornewtech.marketplacepesaing.ui.register.Registrasi

class PraLogin : AppCompatActivity() {
    private lateinit var binding: ActivityPraLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPraLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.toRegister.setOnClickListener {
            startActivity(Intent(this, Registrasi::class.java))
            finish()
        }

        binding.toLoginPedagang.setOnClickListener {
            startActivity(Intent(this, Login::class.java))

        }
    }
}