package com.lucidity.haolu.searchcards.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lucidity.haolu.searchcards.BR
import com.lucidity.haolu.searchcards.R

class CardInformationRecyclerViewAdapter(val mList: List<Pair<String,String>>) : RecyclerView.Adapter<CardInformationRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_detail_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.apply {
            setVariable(BR.details, mList[position])
            executePendingBindings()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}