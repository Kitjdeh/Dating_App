package com.example.dating_app.slider

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CardStackAdapter(val context: Context, val items: List<String>) :
    RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardStackAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CardStackAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }
}