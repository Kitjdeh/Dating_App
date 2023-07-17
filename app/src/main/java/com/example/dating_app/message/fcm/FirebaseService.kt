package com.example.dating_app.message.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.dating_app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


// 목적 -> user의 token 정보를 받아와서 FIrebase 서버로 메세지 보내기
// FIrebase에서는 앱으로 메세지를 전달
// 앱에서 메세지를 받음
// 앱에서 알람을 띄워줌
class FirebaseService : FirebaseMessagingService() {

    private val TAG = "FirebaseServiceTAG"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

//        Log.e(TAG, message.notification?.title.toString())
//        Log.e(TAG, message.notification?.body.toString())
//
//        val title = message.notification?.title.toString()
//        val body = message.notification?.body.toString()

        val title = message.data["title"].toString()
        val body = message.data["content"].toString()

        createNotificationChannel()
        // 받은 notification을 띄운다
        sendNotification(title, body)
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

    private fun sendNotification(title : String, body: String) {
        if (ActivityCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.no)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }
}