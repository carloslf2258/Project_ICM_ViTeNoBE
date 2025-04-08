package com.example.vitenobe.Matches


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vitenobe.R


class MatchesAdapter(private val matchesList: List<MatchesObject>, private val context: Context) : RecyclerView.Adapter<MatchesViewHolders>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchesViewHolders {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.item_matches, parent, false)
        return MatchesViewHolders(layoutView)
    }

    override fun onBindViewHolder(holder: MatchesViewHolders, position: Int) {
        val match = matchesList[position]
        holder.mMatchId.text = match.userId
        holder.mMatchName.text = match.name

        match.profileImageUrl.takeIf { it.isNotEmpty() }?.let {
            Glide.with(context.applicationContext).load(it).into(holder.mMatchImage)
        } ?: Glide.with(context.applicationContext).load(R.drawable.ic_launcher_foreground).into(holder.mMatchImage)
    }

    override fun getItemCount(): Int {
        return matchesList.size
    }
}
