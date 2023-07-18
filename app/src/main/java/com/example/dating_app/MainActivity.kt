package com.example.dating_app

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dating_app.auth.IntroActivity
import com.example.dating_app.auth.UserDataModel
import com.example.dating_app.settting.SettingActivity
import com.example.dating_app.slider.CardStackAdapter
import com.example.dating_app.utils.FirebaseAuthUtils
import com.example.dating_app.utils.FirebaseRef
import com.example.dating_app.utils.MyInfo
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction

class MainActivity : AppCompatActivity() {

    lateinit var cardStackAdapter: CardStackAdapter

    // lineaer manager 같은거 -> view를 어떻게 보여줄건지(카드스택레이아웃매니저 형식으로) 선언
    lateinit var manager: CardStackLayoutManager

    private var TAG = "MainActivity"

    private var userCount = 0

    private lateinit var currentUserGender: String

    private var uid = FirebaseAuthUtils.getUid()
    private val usersDataList = mutableListOf<UserDataModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //나와 다른 성별의 유저를 받아와야한다.
        //1. 나의 성별을 알아햐함
        //2. 전체유저를 받을 때 나와 성별이 다른지 확인

        val setting = findViewById<ImageView>(R.id.settingIcon)
        setting.setOnClickListener {


//            val auth = Firebase.auth
//            auth.signOut()
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)


        }
        // 만들어둔 카드 스택뷰 틀을 가져온다.
        val cardStackView = findViewById<CardStackView>(R.id.cardStackView)

        manager = CardStackLayoutManager(baseContext, object : CardStackListener {

            override fun onCardDragging(direction: Direction?, ratio: Float) {

            }

            override fun onCardSwiped(direction: Direction?) {
                //refresh 설정
                if (direction == Direction.Right) {
//                    Toast.makeText(this@MainActivity, "right", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, usersDataList[userCount].toString())
                    userLikeOtherUser(uid, usersDataList[userCount].uid.toString())
                } else {
//                    Toast.makeText(this@MainActivity, "left", Toast.LENGTH_SHORT).show()
                }
                userCount = userCount + 1
                // 다 돌았으면 새로 데이터 수령
                if (userCount == usersDataList.count()) {
                    getUserDataList(currentUserGender)
//                    Toast.makeText(this@MainActivity, "데이터 새로고침", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCardRewound() {

            }

            override fun onCardCanceled() {

            }

            override fun onCardAppeared(view: View?, position: Int) {

            }

            override fun onCardDisappeared(view: View?, position: Int) {

            }
        })

        // adapter에 만든 cardstackadapter에 필요한 파ㅏ라미터들 입력
        cardStackAdapter = CardStackAdapter(baseContext, usersDataList)
        // cardstackview의 매니저 값은 선언해둔 manager를 쓰겠다.
        cardStackView.layoutManager = manager
        //cardstackview의 어뎁터 끼리 연결
        cardStackView.adapter = cardStackAdapter


        getMyUserData()
//        getUserDataList()
    }

    private fun getMyUserData() {
        val postListner = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, snapshot.toString())
                val data = snapshot.getValue(UserDataModel::class.java)

                currentUserGender = data?.gender.toString()
                MyInfo.myNickname = data?.nickname.toString()
                getUserDataList(currentUserGender)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        FirebaseRef.userInfoRef.child(uid).addValueEventListener(postListner)
    }

    private fun getUserDataList(currentUserGender: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
//                    Log.d(TAG, dataModel.toString())
                    val user = dataModel.getValue(UserDataModel::class.java)
//                    Log.d(TAG, user!!.gender.toString())
//                    Log.d(TAG, currentUserGender)
                    if (user!!.gender.toString().equals(currentUserGender)) {
                    } else {
                        usersDataList.add(user!!)
                    }
                }
                cardStackAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FirebaseRef.userInfoRef.addValueEventListener(postListener)
    }

    //유저에 좋아요를 표시하는 부분
    // 데이터에서 값을 저장
    // 나의 uid - 좋아요한 사람의 uid
    private fun userLikeOtherUser(myUid: String, OtherUid: String) {
        FirebaseRef.userLikeRef.child(myUid).child(OtherUid).setValue(true)
        getOtherUserLikeList(OtherUid)
    }

    private fun getOtherUserLikeList(OtherUid: String) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataModel in dataSnapshot.children) {
                    // Other의 Like 중에 내가 있는지 확인
                    val likeUserKey = dataModel.key.toString()
                    if (likeUserKey.equals(uid)) {
//                        Toast.makeText(this@MainActivity, "매칭 완료", Toast.LENGTH_SHORT).show()
                        createNotificationChannel()
                        sendNotification()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        FirebaseRef.userLikeRef.child(OtherUid).addValueEventListener(postListener)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test_Channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification() {
        if (ActivityCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.no)
            .setContentTitle("매칭완료")
            .setContentText("매칭이 완료되었습니다. ")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }
}