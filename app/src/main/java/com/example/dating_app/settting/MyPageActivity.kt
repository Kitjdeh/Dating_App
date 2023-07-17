package com.example.dating_app.settting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.dating_app.R
import com.example.dating_app.auth.UserDataModel
import com.example.dating_app.utils.FirebaseAuthUtils
import com.example.dating_app.utils.FirebaseRef
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class MyPageActivity : AppCompatActivity() {

    private val TAG = "MyPageActivity"

    private val uid = FirebaseAuthUtils.getUid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)
        getMyData()
    }

    private fun getMyData() {
        val myImage = findViewById<ImageView>(R.id.myImage)
        val myUid = findViewById<TextView>(R.id.myUid)
        val myNickname = findViewById<TextView>(R.id.myNickname)
        val myAge = findViewById<TextView>(R.id.myage)
        val myCity = findViewById<TextView>(R.id.mycity)
        val mygender= findViewById<TextView>(R.id.mygender)

        val postListner = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, snapshot.toString())
                val data = snapshot.getValue(UserDataModel::class.java)
              myUid.text = data!!.uid
              myCity.text= data!!.city
              myNickname.text = data!!.nickname
              myAge.text = data!!.age
              mygender.text = data!!.gender

              //이미지 호출
              val storageRef = Firebase.storage.reference.child(data.uid + ".png")
              storageRef.downloadUrl.addOnCompleteListener(OnCompleteListener{
                    task ->
                    if(task.isSuccessful){
                        Glide.with(baseContext).load(task.result).into(myImage)
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListner)
    }
}