package com.example.haolu.duelmaster

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class LogAdapter(val log: LifePointCalculator.Log) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

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
        holder.binding.setVariable(BR.log, log.log[position])
        val playerBar = holder.itemView.findViewById(R.id.cardView_log) as CardView
        val context = holder.itemView.context
        val red = ContextCompat.getColor(context, R.color.colorYugiRed)
        if (log.log[position].player == "PLAYER TWO")
            playerBar.setCardBackgroundColor(red)
//            playerBar.setBackgroundResource(R.color.colorYugiRed)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return log.log.size
    }


}