package com.example.dating_app.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.dating_app.R
import com.example.dating_app.auth.UserDataModel

class MsgAdapter (val content : Context, val item : MutableList<MsgModel>) : BaseAdapter() {
    override fun getCount(): Int {
        return item.size
    }

    override fun getItem(position: Int): Any {
        return item[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null){
            convertView = LayoutInflater.from(parent?.context).inflate(R.layout.list_view_item,parent, false)
        }
        val nicknameArea = convertView!!.findViewById<TextView>(R.id.listviewItemNicknameArea)
        val textArea = convertView!!.findViewById<TextView>(R.id.listviewItemNickname)
//        nickname.text = item[position].nickname
        nicknameArea.text = item[position].senderInfo
        textArea.text = item[position].sendText
        return convertView!!
    }
}