package com.lucidity.haolu.duelking

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * The RecyclerView adapter used in LogActivity
 */

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
        holder.binding.setVariable(BR.mLog, mLog.mLog[position])
        val turnLp = holder.itemView.findViewById(R.id.text_turnLp) as TextView
        val imageOperation = holder.itemView.findViewById(R.id.image_operation) as ImageView
        val context = holder.itemView.context
        if (mLog.mLog[position].mOperation == "+") {
            turnLp.setTextColor(ContextCompat.getColor(context, R.color.colorGain))
            imageOperation.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add_24dp))
        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return mLog.mLog.size
    }


}