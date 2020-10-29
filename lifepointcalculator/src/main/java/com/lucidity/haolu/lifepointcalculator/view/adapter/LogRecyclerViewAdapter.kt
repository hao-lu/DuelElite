package com.lucidity.haolu.lifepointcalculator.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lucidity.haolu.lifepointcalculator.BR
import com.lucidity.haolu.lifepointcalculator.R
import com.lucidity.haolu.lifepointcalculator.model.LifePointLogItem

class LogRecyclerViewAdapter(val log: List<LifePointLogItem>) :
    RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.log_item_new, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return log.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding?.apply {
            setVariable(BR.log, log[position])
            executePendingBindings()
        }
    }
}