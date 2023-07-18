package com.example.dating_app.settting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.dating_app.R
import com.example.dating_app.auth.IntroActivity
import com.example.dating_app.message.MyLIkeListActivity
import com.example.dating_app.message.MyMsgActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val mybtn = findViewById<Button>(R.id.myPageBtn)

        mybtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        val myMsg = findViewById<Button>(R.id.myMsg)
        myMsg.setOnClickListener {
            val intent = Intent(this,MyMsgActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            val auth = Firebase.auth
            auth.signOut()
            val intent = Intent(this,IntroActivity::class.java)
            startActivity(intent)
        }

        val mylikeBtn = findViewById<Button>(R.id.myLikeListBtn)
        mylikeBtn.setOnClickListener {
            val intent = Intent(this,MyLIkeListActivity::class.java)
            startActivity(intent)
        }
    }
}