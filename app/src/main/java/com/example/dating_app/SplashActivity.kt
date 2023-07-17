package com.example.dating_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.dating_app.R
import com.example.dating_app.auth.IntroActivity
import com.example.dating_app.utils.FirebaseAuthUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {

    //    private lateinit var auth: FirebaseAuth
//    private val auth: FirebaseAuth.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        auth = Firebase.auth
        val uid = auth.currentUser?.uid.toString()
//        val uid = FirebaseAuthUtils.getUid()
        if (uid == "null") {
            Handler().postDelayed({
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)
        } else {
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 3000)

        }


    }
}