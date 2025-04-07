package com.example.vitenobe.Cards


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.vitenobe.R

class arrayAdapter(context: Context?, resourceId: Int, items: List<cards?>?) : ArrayAdapter<cards?>(context!!, resourceId, items!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val card_item = getItem(position)
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item, parent, false)

        val name = view.findViewById<TextView>(R.id.name)
        val image = view.findViewById<ImageView>(R.id.image)

        name.text = card_item!!.name

        if (card_item.profileImageUrl != null && card_item.profileImageUrl != "default") {
            Glide.with(view.context).load(card_item.profileImageUrl).into(image)
        } else {
            Glide.with(view.context).load(R.mipmap.ic_launcher).into(image)
        }


        return view
    }


}