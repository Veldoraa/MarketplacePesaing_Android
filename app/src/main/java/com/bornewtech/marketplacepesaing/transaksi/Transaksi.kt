package com.bornewtech.marketplacepesaing.transaksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityTransaksiBinding

class Transaksi : AppCompatActivity() {
    private lateinit var binding: ActivityTransaksiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
    }
}