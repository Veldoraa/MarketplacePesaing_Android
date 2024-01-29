package com.bornewtech.marketplacepesaing.ui.profile


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bornewtech.marketplacepesaing.databinding.ActivityEditProfilBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditProfil : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfilBinding
    private var dbProfil  = Firebase.firestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.addImgProfil.setOnClickListener {
            startActivity(Intent(this, AddPhoto::class.java))
        }
        binding.backToProfil.setOnClickListener {
            startActivity(Intent(this, Profil::class.java))
        }
        setData()

        binding.btnSimpanProfil.setOnClickListener {

            val nameProfil = binding.inpNamaProfil.text.toString().trim()
            val noHpProfil = binding.inpNoHpProfil.text.toString().trim()
            val emailProfil = binding.inpEmailProfil.text.toString().trim()
            val alamatProfil = binding.inpAlamatProfil.text.toString().trim()
            val tempatPengiriman = binding.inpTempat.text.toString().trim()

            val profilMap = hashMapOf(
                "namaLengkap" to nameProfil,
                "noHpAktif" to noHpProfil,
                "email" to emailProfil,
                "alamatLengkap" to alamatProfil,
                "tempatPengiriman" to tempatPengiriman
            )
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            dbProfil.collection("Pembeli").document(userId).set(profilMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
                    binding.inpNamaProfil.text
                    binding.inpNoHpProfil.text
                    binding.inpAlamatProfil.text
                    binding.inpAlamatProfil.text
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal Memasukkan Data Profil", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setData(){
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val refProfil = dbProfil.collection("Pembeli").document(userId)
        refProfil.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val nama = documentSnapshot.getString("namaLengkap")
                    val noHp = documentSnapshot.getString("noHpAktif")
                    val email = documentSnapshot.getString("email")
                    val alamat = documentSnapshot.getString("alamatLengkap")
                    val tempat = documentSnapshot.getString("tempatPengiriman")

                    binding.inpNamaProfil.setText(nama)
                    binding.inpNoHpProfil.setText(noHp)
                    binding.inpEmailProfil.setText(email)
                    binding.inpAlamatProfil.setText(alamat)
                    binding.inpTempat.setText(tempat)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal mendapatkan data profil", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}