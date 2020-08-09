package com.lucidity.haolu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class SearchCardRecyclerViewAdapter(val searchResults: List<Card>?,
val listener: OnSearchResultListener) :
    RecyclerView.Adapter<SearchCardRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return searchResults?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.apply {
            setVariable(BR.card, searchResults?.get(position))
            // TODO: refactor
            holder.itemView.setOnClickListener {
                listener.onSearchResultClick(searchResults?.get(position)!!)
            }
            executePendingBindings()
        }
    }

    interface OnSearchResultListener {
        fun onSearchResultClick(card: Card)
    }
}