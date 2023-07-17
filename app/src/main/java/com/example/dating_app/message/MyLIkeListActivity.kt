package com.example.dating_app.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.example.dating_app.R
import com.example.dating_app.auth.UserDataModel
import com.example.dating_app.utils.FirebaseAuthUtils
import com.example.dating_app.utils.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


//내가 좋아요한 사람들이 나를 좋아요 한 리스트

class MyLIkeListActivity : AppCompatActivity() {

    private val TAG = "MyLIkeListActivityTAG"
    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    lateinit var listviewAdapter: ListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_like_list)

        // listview중에 어떤거 view인지 선언
        val userListView = findViewById<ListView>(R.id.userListView)

        //adapter 중에 likeUserList에 사용할 거라 선언
        listviewAdapter = ListViewAdapter(this, likeUserList)

        userListView.adapter = listviewAdapter

        //1. 전체 유저 데이터 호출

        //2. 내가 좋아요한 사람들
        // 나를 좋아요한 사람의 리스트를 받아와야 한다
        getMyLikeList()
        userListView.setOnItemClickListener { parent, view, position, id ->
            checkMatching(likeUserList[position].uid.toString())
        }
    }

    private fun checkMatching(otherUid: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataModel in snapshot.children) {
                    if (dataModel.children.count() == 0) {

                    } else {
                        var useruid = dataModel.key.toString()
                        if (useruid.equals(uid)) {
                            Toast.makeText(this@MyLIkeListActivity, "매칭 상대입니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        FirebaseRef.userLikeRef.child(otherUid).addValueEventListener(postListener)
    }

    private fun getMyLikeList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    // 내가 좋아요 한 사람들의 uid를 가져온다.
                    likeUserListUid.add(dataModel.key.toString())
                }
                getUserDataList()

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        FirebaseRef.userLikeRef.child(uid).addValueEventListener(postListener)
    }

    private fun getUserDataList() {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    val user = dataModel.getValue(UserDataModel::class.java)
                    //모든 유저들을 순회하는 중 이 user의 uid가 내가 좋아요 한 리스트에 있다면
                    if (likeUserListUid.contains(user?.uid)) {
                        likeUserList.add(user!!)
                    }
                }
                listviewAdapter.notifyDataSetChanged()
                Log.d(TAG, likeUserList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }
}