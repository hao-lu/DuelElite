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

class SearchCardResultsListAdapter(private val listener: OnSearchResultListener) : ListAdapter<Card,
            SearchCardResultsListAdapter.ViewHolder>
        (SearchCardResultsDiffUtilCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(view)
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
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_card_result, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.apply {
            setVariable(BR.card, getItem(position))
            // refactor
                holder.itemView.setOnClickListener {
                listener.onSearchResultClick(getItem(position))
            }
            executePendingBindings()
        }
    }

    interface OnSearchResultListener {
        fun onSearchResultClick(card: Card)
    }
}