package com.bornewtech.marketplacepesaing.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bornewtech.marketplacepesaing.databinding.ActivityLoginBinding
import com.bornewtech.marketplacepesaing.main.MainActivity
import com.bornewtech.marketplacepesaing.ui.register.Registrasi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Tombol untuk registrasi
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, Registrasi::class.java))
        }

        // Tombol untuk login
        binding.btnLogin.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (validateForm(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            checkSuspensionStatus(it) // Periksa status penangguhan
                        }
                    } else {
                        Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkSuspensionStatus(user: FirebaseUser) {
        val userId = user.uid
        val currentTime = System.currentTimeMillis()

        firestore.collection("TolakPesanan")
            .get()
            .addOnSuccessListener { documents ->
                var suspended = false

                for (document in documents) {
                    val pembeliId = document.getString("pembeliId")
                    if (pembeliId == userId) {
                        val reason = document.getString("reason")
                        val timestamp = document.getLong("timestamp")
                        val suspensionStage = document.getLong("suspensionStage")

                        if (reason == "Karena Menipu" && timestamp != null && suspensionStage != null) {
                            val suspensionDuration = when (suspensionStage) {
                                1L -> 10L * 24L * 60L * 60L * 1000L // 1 hari
                                2L -> 30L * 24L * 60L * 60L * 1000L // 30 hari
                                3L -> Long.MAX_VALUE // Selamanya
                                else -> 0L // Tidak ada penangguhan
                            }

                            if (suspensionStage == 3L) {
                                Toast.makeText(this, "Maaf, akun anda sudah tidak bisa digunakan lagi", Toast.LENGTH_SHORT).show()
                                auth.signOut()
                                suspended = true
                                break
                            }

                            val elapsedTime = currentTime - timestamp
                            val timeLeft = suspensionDuration - elapsedTime

                            if (timeLeft > 0) {
                                val daysLeft = timeLeft / (24 * 60 * 60 * 1000)
                                val hoursLeft = (timeLeft % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
                                val minutesLeft = (timeLeft % (60 * 60 * 1000)) / (60 * 1000)
                                val secondsLeft = (timeLeft % (60 * 1000)) / 1000

                                val timeMessage = when {
                                    daysLeft > 0 -> "Akun Anda Ditangguhkan Selama $daysLeft Hari"
                                    hoursLeft > 0 -> "Akun Anda Ditangguhkan Selama $hoursLeft Jam"
                                    minutesLeft > 0 -> "Akun Anda Ditangguhkan Selama $minutesLeft Menit"
                                    else -> "Akun Anda Ditangguhkan Selama $secondsLeft Detik"
                                }

                                Toast.makeText(this, timeMessage, Toast.LENGTH_SHORT).show()

                                user.reload().addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        auth.signOut() // Keluarkan pengguna
                                    }
                                }

                                suspended = true
                                break
                            }
                        }
                    }
                }

                if (!suspended) {
                    // Jika tidak ada penangguhan atau periode penangguhan telah berakhir, izinkan pengguna masuk
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Terjadi Kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
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

            else -> true
        }
    }
}






//package com.bornewtech.marketplacepesaing.ui.login
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.TextUtils
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.bornewtech.marketplacepesaing.databinding.ActivityLoginBinding
//import com.bornewtech.marketplacepesaing.main.MainActivity
//import com.bornewtech.marketplacepesaing.ui.register.Registrasi
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.ktx.Firebase
//
//class Login : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth: FirebaseAuth
//    private lateinit var firestore: FirebaseFirestore
//
//    override fun onCreate(savedBundleState: Bundle?) {
//        super.onCreate(savedBundleState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        auth = Firebase.auth
//        firestore = FirebaseFirestore.getInstance()
//
//        // Tombol untuk registrasi
//        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, Registrasi::class.java))
//        }
//
//        // Tombol untuk login
//        binding.btnLogin.setOnClickListener { loginUser() }
//    }
//
//    private fun loginUser() {
//        val email = binding.etEmail.text.toString()
//        val password = binding.etPassword.text.toString()
//
//        if (validateForm(email, password)) {
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val user = auth.currentUser
//                        user?.let {
//                            checkSuspensionStatus(it) // Periksa status penangguhan
//                        }
//                    } else {
//                        Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//
//    private fun checkSuspensionStatus(user: FirebaseUser) {
//        val userId = user.uid
//        val currentTime = System.currentTimeMillis()
//
//        firestore.collection("TolakPesanan")
//            .document(userId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val reason = document.getString("reason")
//                    val timestamp = document.getLong("timestamp")
//                    val suspensionStage = document.getLong("suspensionStage") ?: 2L // Tahap 1 secara default
//
//                    val suspensionDuration = when (suspensionStage) {
//                        1L -> 10 * 24 * 60 * 60 * 1000 // 10 hari
//                        2L -> 30 * 24 * 60 * 60 * 1000 // 30 hari
//                        else -> Long.MAX_VALUE // Selamanya
//                    }
//
//                    if (reason == "Karena Menipu" && timestamp != null) {
//                        val elapsedTime = currentTime - timestamp
//                        val timeLeft = suspensionDuration - elapsedTime
//
//                        if (timeLeft > 0) {
//                            val daysLeft = timeLeft / (24 * 60 * 60 * 1000)
//                            val hoursLeft = (timeLeft % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
//                            val minutesLeft = (timeLeft % (60 * 60 * 1000)) / (60 * 1000)
//                            val secondsLeft = (timeLeft % (60 * 1000)) / 1000
//
//                            val timeMessage = when {
//                                daysLeft > 0 -> "Akun Anda Ditangguhkan Selama $daysLeft Hari"
//                                hoursLeft > 0 -> "Akun Anda Ditangguhkan Selama $hoursLeft Jam"
//                                minutesLeft > 0 -> "Akun Anda Ditangguhkan Selama $minutesLeft Menit"
//                                else -> "Akun Anda Ditangguhkan Selama $secondsLeft Detik"
//                            }
//
//                            Toast.makeText(this, timeMessage, Toast.LENGTH_SHORT).show()
//
//                            user.reload().addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    auth.signOut() // Keluarkan pengguna
//                                }
//                            }
//                        } else {
//                            // Jika periode penangguhan telah berakhir atau tidak ada penangguhan, biarkan pengguna masuk
//                            startActivity(Intent(this, MainActivity::class.java))
//                        }
//                    } else {
//                        // Jika alasan bukan karena menipu atau timestamp tidak ditemukan, izinkan pengguna masuk
//                        startActivity(Intent(this, MainActivity::class.java))
//                    }
//                } else {
//                    // Jika tidak ada record di "TolakPesanan", izinkan pengguna masuk
//                    startActivity(Intent(this, MainActivity::class.java))
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Terjadi Kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun validateForm(email: String, password: String): Boolean {
//        return when {
//            TextUtils.isEmpty(email) -> {
//                binding.tilemail.error = "Masukkan Email"
//                false
//            }
//
//            TextUtils.isEmpty(password) -> {
//                binding.tilpassword.error = "Masukkan Password"
//                false
//            }
//
//            else -> true
//        }
//    }
//}



//package com.bornewtech.marketplacepesaing.ui.login
//
//import android.content.Intent
//import android.os.Bundle
//import android.text.TextUtils
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.bornewtech.marketplacepesaing.databinding.ActivityLoginBinding
//import com.bornewtech.marketplacepesaing.main.MainActivity
//import com.bornewtech.marketplacepesaing.ui.register.Registrasi
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.ktx.Firebase
//
//class Login : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth: FirebaseAuth
//    private lateinit var firestore: FirebaseFirestore
//
//    override fun onCreate(savedBundleState: Bundle?) {
//        super.onCreate(savedBundleState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        auth = Firebase.auth
//        firestore = FirebaseFirestore.getInstance()
//
//        // Tombol untuk registrasi
//        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, Registrasi::class.java))
//        }
//
//        // Tombol untuk login
//        binding.btnLogin.setOnClickListener { loginUser() }
//    }
//
//    private fun loginUser() {
//        val email = binding.etEmail.text.toString()
//        val password = binding.etPassword.text.toString()
//
//        if (validateForm(email, password)) {
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val user = auth.currentUser
//                        user?.let {
//                            checkSuspensionStatus(it) // Periksa status penangguhan
//                        }
//                    } else {
//                        Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//
//    private fun checkSuspensionStatus(user: FirebaseUser) {
//        val userId = user.uid
//        val currentTime = System.currentTimeMillis()
//
//        firestore.collection("TolakPesanan")
//            .document(userId)
//            .get()
//            .addOnSuccessListener { document ->
//                if (document.exists()) {
//                    val reason = document.getString("reason")
//                    val timestamp = document.getLong("timestamp")
//                    val suspensionStage = document.getLong("suspensionStage") ?: 1 // Tahap 1 secara default
//
//                    val suspensionDuration = when (suspensionStage) {
//                        1L -> 0 * 24 * 60 * 60 * 1000 // 10 hari
//                        2L -> 30 * 24 * 60 * 60 * 1000 // 30 hari
//                        else -> Long.MAX_VALUE // Selamanya
//                    }
//
//                    if (reason == "Karena Menipu" && timestamp != null) {
//                        val elapsedTime = currentTime - timestamp
//                        val timeLeft = suspensionDuration - elapsedTime
//
//                        if (timeLeft > 0) {
//                            val daysLeft = timeLeft / (24 * 60 * 60 * 1000)
//                            val hoursLeft = (timeLeft % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000)
//                            val minutesLeft = (timeLeft % (60 * 60 * 1000)) / (60 * 1000)
//                            val secondsLeft = (timeLeft % (60 * 1000)) / 1000
//
//                            val timeMessage = when {
//                                daysLeft > 0 -> "Akun Anda Ditangguhkan Selama $daysLeft Hari"
//                                hoursLeft > 0 -> "Akun Anda Ditangguhkan Selama $hoursLeft Jam"
//                                minutesLeft > 0 -> "Akun Anda Ditangguhkan Selama $minutesLeft Menit"
//                                else -> "Akun Anda Ditangguhkan Selama $secondsLeft Detik"
//                            }
//
//                            Toast.makeText(this, timeMessage, Toast.LENGTH_SHORT).show()
//
//                            user.reload().addOnCompleteListener {
//                                if (it.isSuccessful) {
//                                    auth.signOut() // Keluarkan pengguna
//                                }
//                            }
//                        } else {
//                            // Jika periode penangguhan telah berakhir atau tidak ada penangguhan, biarkan pengguna masuk
//                            startActivity(Intent(this, MainActivity::class.java))
//                        }
//                    } else {
//                        // Jika alasan bukan karena menipu atau timestamp tidak ditemukan, izinkan pengguna masuk
//                        startActivity(Intent(this, MainActivity::class.java))
//                    }
//                } else {
//                    // Jika tidak ada record di "TolakPesanan", izinkan pengguna masuk
//                    startActivity(Intent(this, MainActivity::class.java))
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(this, "Terjadi Kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun validateForm(email: String, password: String): Boolean {
//        return when {
//            TextUtils.isEmpty(email) -> {
//                binding.tilemail.error = "Masukkan Email"
//                false
//            }
//
//            TextUtils.isEmpty(password) -> {
//                binding.tilpassword.error = "Masukkan Password"
//                false
//            }
//
//            else -> true
//        }
//    }
//}





//package com.bornewtech.marketplacepesaing.ui.login
//
//import android.content.Context
//import android.content.Intent
//import android.content.SharedPreferences
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.TextUtils
//import android.widget.Toast
//import com.bornewtech.marketplacepesaing.databinding.ActivityLoginBinding
//import com.bornewtech.marketplacepesaing.main.MainActivity
//import com.bornewtech.marketplacepesaing.ui.register.Registrasi
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.ktx.Firebase
//
//class Login : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth:FirebaseAuth
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        supportActionBar?.hide()
//
//        auth = Firebase.auth
//
//        // tombol ke registrasi
//        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, Registrasi::class.java))
//        }
//        //tombol untuk login
//        binding.btnLogin.setOnClickListener {loginUser()}
//    }
//
//    private fun loginUser(){
//        val email = binding.etEmail.text.toString()
//        val password = binding.etPassword.text.toString()
//        if (validateForm(email, password)){
//            auth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        startActivity(Intent(this, MainActivity::class.java))
//                    } else {
//                        Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
//                    }
//                }
//        }
//    }
//    private fun validateForm(email: String, password: String): Boolean {
//        return when {
//            TextUtils.isEmpty(email) -> {
//                binding.tilemail.error = "Masukkan Email"
//                false
//            }
//
//            TextUtils.isEmpty(password) -> {
//                binding.tilpassword.error = "Masukkan Password"
//                false
//            }
//
//            else -> {
//                true
//            }
//        }
//    }
//
//}
