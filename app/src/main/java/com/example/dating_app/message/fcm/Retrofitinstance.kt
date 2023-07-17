package com.example.dating_app.message.fcm

import android.util.Log
import com.example.dating_app.message.fcm.Repo.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofitinstance {

    companion object{

        private val retrofit by lazy {

            Log.d("MyLIkeListActivityTAG", "Retrofitinstance")
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory((GsonConverterFactory.create()))
                .build()
        }
        val api = retrofit.create(NotiAPI::class.java)

    }
}