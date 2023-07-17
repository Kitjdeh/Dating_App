package com.example.dating_app.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import com.example.dating_app.MainActivity
import com.example.dating_app.R
//import kotlinx.android.synthetic.main.activity_intro.joinBtn

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

//        joinBtn.setOnClickListener {
//            val intent = Intent(this,JoinActivity::class.java)
//            startActivity(intent)
//        }

        val loginBtn : Button = findViewById(R.id.loginBtn)
        val joinBtn = findViewById<Button>(R.id.joinBtn)
        loginBtn.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        joinBtn.setOnClickListener {
            val intent = Intent(this,JoinActivity::class.java)
            startActivity(intent)
        }
    }
}