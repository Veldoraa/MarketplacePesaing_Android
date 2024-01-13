package com.bornewtech.marketplacepesaing.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.databinding.ActivityMainBinding
import com.bornewtech.marketplacepesaing.maps.Maps
import com.bornewtech.marketplacepesaing.transaksi.Transaksi
import com.bornewtech.marketplacepesaing.ui.barang.recyclerview.RecViewBarang
import com.bornewtech.marketplacepesaing.ui.fragment.home.HomeFragment
import com.bornewtech.marketplacepesaing.ui.profile.Profil
import com.bornewtech.marketplacepesaing.ui.recyclerview.RecViewPedagang


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.navViewBottom.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.dashboard -> {
                    //untuk mengarahkan ke fragment ketika bottom nav ditekan
                    val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment).commit()
                }

                R.id.keranjang -> {
                    val intent = Intent(this, RecViewPedagang::class.java)
                    startActivity(intent)
                }

                R.id.maps_pengguna -> {
                    val intent = Intent(this, Maps::class.java)
                    startActivity(intent)
                }

                R.id.orderan -> {
                    val intent = Intent(this, Transaksi::class.java)
                    startActivity(intent)
                }

                R.id.akun_pengguna -> {
                    // untuk mengarahkan ke activity ketika bottom nav ditekan
                    val intent = Intent(this, Profil::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}