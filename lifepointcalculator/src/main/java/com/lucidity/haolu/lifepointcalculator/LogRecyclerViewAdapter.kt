package com.lucidity.haolu.lifepointcalculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.lucidity.haolu.lifepointcalculator.databinding.LogItemNewBinding
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
//        val entry = log[position]
//        val context = holder.binding?.tvActionLp?.context
//        holder.binding?.apply {
//            if (entry.actionLp < 0) {
//                tvActionLp.setTextColor(ContextCompat.getColor(context!!, R.color.colorLose))
//            } else {
//                tvActionLp.setTextColor(ContextCompat.getColor(context!!, R.color.colorGain))
//            }
//            setVariable(BR.log, entry)
//            executePendingBindings()
//        }

        holder.binding?.apply {
            setVariable(BR.log, log[position])
            executePendingBindings()
        }
    }
}