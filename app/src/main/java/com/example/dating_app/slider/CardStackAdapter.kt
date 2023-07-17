package com.example.dating_app.slider

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dating_app.R
import com.example.dating_app.auth.UserDataModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class CardStackAdapter(val context: Context, val items: List<UserDataModel>) :
    RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_card, parent, false)
        return ViewHolder(view)
    }
    private var TAG = "CardStackAdapter"
    override fun onBindViewHolder(holder: CardStackAdapter.ViewHolder, position: Int) {
        //데이터를 넣어주는 부분
        holder.binding(items[position])
    }

    override fun getItemCount(): Int {
        //items의 사이즈가 item의 갯수
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.profileImageArea)
        val nickname = itemView.findViewById<TextView>(R.id.itemNickname)
        val age = itemView.findViewById<TextView>(R.id.itemAge)
        val city = itemView.findViewById<TextView>(R.id.itemCity)
        fun binding(data: UserDataModel) {
            Log.w(TAG, "${data}")
            val storageRef = Firebase.storage.reference.child(data.uid + ".png")
            storageRef.downloadUrl.addOnCompleteListener({ task ->
                if (task.isSuccessful) {
                    Glide.with(context).load(task.result).into(image)
                }
            })
            nickname.text = data.nickname
            age.text = data.age
            city.text = data.city
        }
    }
}