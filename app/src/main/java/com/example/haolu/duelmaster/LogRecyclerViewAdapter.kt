package com.example.haolu.duelmaster

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class LogRecyclerViewAdapter(val mLog: LifePointCalculator.Log) : RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding: ViewDataBinding
        init {
            binding = DataBindingUtil.bind(view)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.log_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.log, mLog.log[position])
        val playerBar = holder.itemView.findViewById(R.id.cardView_log) as CardView
        val context = holder.itemView.context
        val red = ContextCompat.getColor(context, R.color.colorYugiRed)
        if (mLog.log[position].player == "PLAYER TWO")
            playerBar.setCardBackgroundColor(red)
//            playerBar.setBackgroundResource(R.color.colorYugiRed)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mLog.log.size
    }


}