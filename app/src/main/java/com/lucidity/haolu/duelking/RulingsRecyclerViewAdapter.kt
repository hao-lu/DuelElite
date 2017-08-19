package com.lucidity.haolu.duelking

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import kotlinx.android.synthetic.main.ruling_item_row.view.*
import android.widget.TextView

class RulingsRecyclerViewAdapter(private val mList: ArrayList<ArrayList<HeaderOrItem>>) : RecyclerView.Adapter<RulingsRecyclerViewAdapter.ViewHolder>() {

    data class HeaderOrItem(val type: Types, val data: String) {

        enum class Types {
            H2,
            H3,
            TABLE,
            DIV,
            UL
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val TAG = "ViewHolder"

        fun bind(items: ArrayList<HeaderOrItem>) {
            for (item in items) {
                val row = View.inflate(itemView.context, R.layout.table_row_ruling, null)
                val text = row.findViewById(R.id.text_table_ruling) as TextView
                when (item.type) {
                    HeaderOrItem.Types.H2 -> {
                        text.textSize = 20f
                        text.setTypeface(text.typeface, Typeface.BOLD)
                        text.text = item.data
                    }
                    HeaderOrItem.Types.H3 -> {
                        text.textSize = 16f
                        text.setTypeface(text.typeface, Typeface.BOLD)
                        text.text = item.data
                    }
//                    HeaderOrItem.Types.TABLE -> {
//                    }
                    else -> {
                        text.textSize = 14f
                        text.text = "• " + item.data
                    }

                }
//                text.text = item.data
                itemView.table.addView(row)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.ruling_item_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}