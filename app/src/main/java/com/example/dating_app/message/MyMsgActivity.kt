package com.example.dating_app.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.example.dating_app.R
import com.example.dating_app.auth.UserDataModel
import com.example.dating_app.utils.FirebaseAuthUtils
import com.example.dating_app.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyMsgActivity : AppCompatActivity() {

    private val TAG = "MYMSGTAG"

    lateinit var listviewAdapter: MsgAdapter

    val msgList = mutableListOf<MsgModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_msg)

        val listview = findViewById<ListView>(R.id.msgListView)

        listviewAdapter = MsgAdapter(this,msgList)

        listview.adapter = listviewAdapter

        getMyMsg()
    }


    private fun getMyMsg(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                msgList.clear()
                for (dataModel in dataSnapshot.children) {
                val msg = dataModel.getValue(MsgModel::class.java)
                    Log.d(TAG, msg.toString())
                    msgList.add(msg!!)
                }
                listviewAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userMsgRef.child(FirebaseAuthUtils.getUid()).addValueEventListener(postListener)
    }
}