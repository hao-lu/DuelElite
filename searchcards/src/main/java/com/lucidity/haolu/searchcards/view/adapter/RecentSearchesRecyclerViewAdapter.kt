package com.lucidity.haolu.searchcards.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lucidity.haolu.searchcards.BR
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.room.entity.RecentSearch

class RecentSearchesRecyclerViewAdapter(
    private val recentSearches: List<RecentSearch>,
    private val listener: OnRecentSearchListener
) :
    RecyclerView.Adapter<RecentSearchesRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(
        view: View,
        private val onRecentSearchListener: OnRecentSearchListener
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val binding: ViewDataBinding? = DataBindingUtil.bind(view)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            onRecentSearchListener.onRecentResultClick(adapterPosition)
            onRecentSearchListener.onRecentResultClick(adapterPosition, v)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_recent_search, parent, false), listener
        )
    }

    override fun getItemCount(): Int {
        return recentSearches.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ViewCompat.setTransitionName(holder.itemView, recentSearches[position].name)
        holder.binding?.apply {
            setVariable(BR.recentSearch, recentSearches[position])
            executePendingBindings()
        }
    }
}

interface OnRecentSearchListener {
    fun onRecentResultClick(position: Int)
    fun onRecentResultClick(position: Int, view: View)
}