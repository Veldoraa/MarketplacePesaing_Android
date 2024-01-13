package com.bornewtech.marketplacepesaing.transaksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityAlamatBinding

class Alamat : AppCompatActivity() {
    private lateinit var binding: ActivityAlamatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlamatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}