package com.bornewtech.marketplacepesaing.ui.transaksi

import android.content.Intent
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

        binding.btnImgAddAlamat.setOnClickListener {
            startActivity(Intent(this, Alamat::class.java))
        }
    }
}