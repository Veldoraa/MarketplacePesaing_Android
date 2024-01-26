package com.bornewtech.marketplacepesaing.ui.splashScreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bornewtech.marketplacepesaing.databinding.ActivitySplashScreenBinding
import com.bornewtech.marketplacepesaing.ui.login.Login
import com.bornewtech.marketplacepesaing.ui.login.PraLogin

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreen, PraLogin::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(androidx.constraintlayout.widget.R.anim.abc_fade_in, androidx.constraintlayout.widget.R.anim.abc_fade_out)
        }, limit)
    }

    companion object{
        private const val limit=3000L
    }
}