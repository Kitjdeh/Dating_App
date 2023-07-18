package com.example.dating_app.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.dating_app.R
import com.example.dating_app.auth.UserDataModel
import com.example.dating_app.message.fcm.NotiModel
import com.example.dating_app.message.fcm.PushNotification
import com.example.dating_app.message.fcm.Retrofitinstance
import com.example.dating_app.utils.FirebaseAuthUtils
import com.example.dating_app.utils.FirebaseRef
import com.example.dating_app.utils.MyInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


//내가 좋아요한 사람들이 나를 좋아요 한 리스트

class MyLIkeListActivity : AppCompatActivity() {

    private val TAG = "MyLIkeListActivityTAG"
    private val uid = FirebaseAuthUtils.getUid()

    private val likeUserListUid = mutableListOf<String>()
    private val likeUserList = mutableListOf<UserDataModel>()

    lateinit var listviewAdapter: ListViewAdapter

    lateinit var getteruid: String

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


//        userListView.setOnItemClickListener { parent, view, position, id ->
//            checkMatching(likeUserList[position].uid.toString())
//
//            val notiModel = NotiModel("ㅁㄴㅇㄻㄴㅇㄹ", "ㅁㄴㅇㄻㄴㅇㄻㄴㅇㄻㄴㅇㄹ")
//            val pushModel = PushNotification(notiModel,likeUserList[position].token.toString())
//            Log.d(TAG, likeUserList[position].token.toString())
//            testPush(pushModel)
//        }
        //내가 좋아요한 유저를 클릭하면
        userListView.setOnItemLongClickListener { parent, view, position, id ->

            checkMatching(likeUserList[position].uid.toString())
            getteruid = likeUserList[position].uid.toString()
            showDialog()

            return@setOnItemLongClickListener (true)
        }

        //메세지 보내기 창이 떠서 메세지를 보낼 수 있께 하기
        //메세지 보내고 상대방에게 PUSH 알람 띄워주기
        //서로 좋아요한 사람이 아니면 메세지 보내기 불가능하게 함
    }

    private fun checkMatching(otherUid: String) {

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.children.count() == 0) {
                    Log.d(TAG, "datamodel 망함")
                    Toast.makeText(this@MyLIkeListActivity, "상대가 없습니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    for (dataModel in snapshot.children) {
                        Log.d(TAG, dataModel.toString())
                        var useruid = dataModel.key.toString()
                        if (useruid.equals(uid)) {
                            Log.d(TAG, "매칭 상대입니다.")
                            Toast.makeText(this@MyLIkeListActivity, "매칭 상대입니다.", Toast.LENGTH_SHORT)
                                .show()
                            //Dialog

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

    //PUSH
    //pushnotification = token과 notinodel값이 들어잇는 파라미터
    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            Log.w(TAG, "testPush작동")
            Retrofitinstance.api.postNotification(notification)
        } catch (e: Exception) {
            Log.w(TAG, "testPush망함")
        }
    }


    //Dialog
    private fun showDialog() {
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("메세지보내기")
        val mAlertDialog = mBuilder.show()
        val sendBtn = mAlertDialog.findViewById<Button>(R.id.sendBtnArea)
        val textArea = mAlertDialog.findViewById<EditText>(R.id.sendTextArea)
        sendBtn?.setOnClickListener {
            //메세지 model 해둔거 선언
            val msgModel = MsgModel(MyInfo.myNickname,textArea!!.text.toString())
            FirebaseRef.userMsgRef.child(getteruid).push().setValue(msgModel)
            //Dialog 종료
            mAlertDialog.dismiss()
        }
        // message
        // 받는 사람 uid
        //message
        //보낸사람
    }
}