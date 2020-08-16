package com.lucidity.haolu.searchcards.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lucidity.haolu.searchcards.BR
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.room.entity.Card

class SearchCardResultsListAdapter(private val listener: OnSearchResultListener) :
    ListAdapter<Card, SearchCardResultsListAdapter.ViewHolder>(SearchCardResultsDiffUtilCallback()) {

    class ViewHolder(view: View,
                     private val onSearchResultListener: OnSearchResultListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        val binding: ViewDataBinding? = DataBindingUtil.bind(view)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onSearchResultListener.onSearchResultClick(adapterPosition)
        }
    }

    private class SearchCardResultsDiffUtilCallback : DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_card_result, parent, false)
        return ViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.apply {
            setVariable(BR.card, getItem(position))
            executePendingBindings()
        }
    }
}

interface OnSearchResultListener {
    fun onSearchResultClick(position: Int)
}