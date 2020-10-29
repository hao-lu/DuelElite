package com.lucidity.haolu.searchcards.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.BR
import com.lucidity.haolu.searchcards.room.entity.Card
//
//class SearchCardRecyclerViewAdapter(
//    private val searchResults: MutableList<Card>,
//    private val listener: OnSearchResultListener2
//) :
//    RecyclerView.Adapter<SearchCardRecyclerViewAdapter.ViewHolder>() {
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val binding: ViewDataBinding? = DataBindingUtil.bind(view)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view =
//            LayoutInflater.from(parent.context).inflate(R.layout.item_search_card_result, parent, false)
//        return ViewHolder(
//            view
//        )
//    }
//
//    override fun getItemCount(): Int {
//        return searchResults.size
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding?.apply {
//            setVariable(BR.card, searchResults[position])
//            // TODO: refactor
////            holder.itemView.setOnClickListener {
////                listener.onSearchResultClick(searchResults[position])
////            }
//            executePendingBindings()
//        }
//    }
//
//    fun setSearchResults(newList: List<Card>) {
//        val diffUtilCallback = SearchCardResultsDiffUtilCallback(searchResults, newList)
//        val diffResult = DiffUtil.calculateDiff(diffUtilCallback)
//        searchResults.clear()
//        searchResults.addAll(newList)
//        diffResult.dispatchUpdatesTo(this)
//    }
//
//    interface OnSearchResultListener2 {
//        fun onSearchResultClick(card: Card)
//    }
//
//    private class SearchCardResultsDiffUtilCallback(
//        val oldSearchCardResults: List<Card>,
//        val newSearchCardResults: List<Card>
//    ) : DiffUtil.Callback() {
//
//        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            return oldSearchCardResults[oldItemPosition] === newSearchCardResults[newItemPosition]
//        }
//
//        override fun getOldListSize(): Int {
//            return oldSearchCardResults.size
//        }
//
//        override fun getNewListSize(): Int {
//            return newSearchCardResults.size
//        }
//
//        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            return oldSearchCardResults[oldItemPosition].name == newSearchCardResults[newItemPosition].name
//        }
//    }
//}