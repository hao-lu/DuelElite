package com.example.haolu.duelmaster

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import kotlinx.android.synthetic.main.ruling_item_row.view.*

class RulingsRecyclerViewAdapter(val mList: ArrayList<HeaderOrItem>) : RecyclerView.Adapter<RulingsRecyclerViewAdapter.ViewHolder>() {

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

        fun bind(item: HeaderOrItem) {
            when (item.type) {
                HeaderOrItem.Types.H2 -> {
                    itemView.setBackgroundResource(R.color.colorGrayBg)
                    itemView.text_ruling.textSize = 20f
                }
                HeaderOrItem.Types.H3 -> {
                    itemView.text_ruling.textSize = 18f
                    itemView.ruling_color_bar.setBackgroundResource(R.color.colorYugiRed)
                }
                HeaderOrItem.Types.TABLE -> {
                    itemView.text_ruling.textSize = 12f
                    itemView.ruling_color_bar.setBackgroundResource(R.color.colorYugiBlue)
                }
                HeaderOrItem.Types.UL -> {
                    itemView.text_ruling.textSize = 14f
                    itemView.ruling_color_bar.setBackgroundResource(R.color.colorYugiYellow)
                }

            }
            itemView.text_ruling.text = item.data
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