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
    //    private lateinit var googleSignInClient: GoogleSignInClient
    private var firebaseAuth = FirebaseAuth.getInstance()

//    companion object {
//        private const val RC_SIGN_IN = 1001;
//    }

//    override fun onStart() {
//        super.onStart()
//        if (firebaseAuth.currentUser != null ){
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        auth = Firebase.auth

//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("459139532605-t6nfcoc68j5uj56tbpo1q6pl0bbtc5fa.apps.googleusercontent.com")
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // tombol ke registrasi
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, Registrasi::class.java))
        }
        //tombol untuk login
        binding.btnLogin.setOnClickListener {loginUser()}
//        // tombol login akun google
//        binding.btnGoogle.setOnClickListener {
//            val signInIntent = googleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//        }
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
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN){
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)!!
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                e.printStackTrace()
//                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken : String){
//        val credentials = GoogleAuthProvider.getCredential(idToken, null)
//        firebaseAuth.signInWithCredential(credentials)
//            .addOnSuccessListener {
//                startActivity(Intent(this, MainActivity::class.java))
//            }
//            .addOnFailureListener { error ->
//                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_SHORT).show()
//            }
//    }

}
//class Login : AppCompatActivity() {
//    private lateinit var binding: ActivityLoginBinding
//    private lateinit var auth:FirebaseAuth
//    private lateinit var sharedPreferences: SharedPreferences
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        auth = Firebase.auth
//        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//
//        // tombol ke registrasi
//        binding.btnRegister.setOnClickListener {
//            startActivity(Intent(this, Registrasi::class.java))
//        }
//        //tombol untuk login
//        binding.btnLogin.setOnClickListener {loginUser()}
//
//        // Memeriksa apakah ada data login yang tersimpan
//        checkStoredLoginData()
//    }
//
//    private fun loginUser() {
//        val email = binding.etEmail.text.toString()
//        val password = binding.etPassword.text.toString()
//
//        if (validateForm(email, password)) {
//            // Cek apakah ada data login yang tersimpan
//            val storedEmail = sharedPreferences.getString("user_email", "")
//            val storedPassword = sharedPreferences.getString("user_password", "")
//
//            if (email == storedEmail && password == storedPassword) {
//                // Jika ada data login yang tersimpan, langsung masuk ke MainActivity
//                startActivity(Intent(this, MainActivity::class.java))
//            } else {
//                // Jika tidak ada data login yang tersimpan, lakukan otentikasi Firebase
//                auth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            // Simpan data login ke SharedPreferences setelah login berhasil
//                            saveLoginData(email, password)
//
//                            // Masuk ke MainActivity
//                            startActivity(Intent(this, MainActivity::class.java))
//                        } else {
//                            Toast.makeText(this, "Periksa Kembali Email dan Kata Sandi Anda", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//            }
//        }
//    }
//
//    private fun saveLoginData(email: String, password: String) {
//        val editor = sharedPreferences.edit()
//        editor.putString("user_email", email)
//        editor.putString("user_password", password)
//        editor.apply()
//    }
//
//    private fun checkStoredLoginData() {
//        // Membaca nilai email yang tersimpan di SharedPreferences
//        val storedEmail = sharedPreferences.getString("user_email", "")
//        val storedPassword = sharedPreferences.getString("user_password", "")
//
//        if (!storedEmail.isNullOrBlank() && !storedPassword.isNullOrBlank()) {
//            // Data login tersimpan, lakukan sesuatu di sini
//            Toast.makeText(this, "Login otomatis dengan Email: $storedEmail", Toast.LENGTH_SHORT).show()
//            // Langsung masuk ke MainActivity
//            startActivity(Intent(this, MainActivity::class.java))
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