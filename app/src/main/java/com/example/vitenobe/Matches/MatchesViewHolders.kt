package com.example.vitenobe.Matches

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vitenobe.Chat.ChatActivity
import com.example.vitenobe.R

class MatchesViewHolders(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    private val mMatchId: TextView = itemView.findViewById(R.id.Matchid)

    override fun onClick(view: View) {
        // Criação direta do Intent e passando os dados com putExtra
        val intent = Intent(view.context, ChatActivity::class.java).apply {
            putExtra("matchId", mMatchId.text.toString())
        }
        view.context.startActivity(intent)
    }

    init {
        // Configura o listener diretamente no itemView
        itemView.setOnClickListener(this)
    }
}
