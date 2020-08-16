package com.lucidity.haolu.searchcards.view.adapter

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.model.CardRulings
import com.lucidity.haolu.searchcards.model.HeaderOrItem

/**
 * RecyclerView adapter for RulingFragment, which splits the rulings into different sections
 * The RecyclerView in RulingFragment is a RecyclerView of TableLayouts
 */

class CardRulingsRecyclerViewAdapter(private val listOfCardRulings: ArrayList<CardRulings>) :
    RecyclerView.Adapter<CardRulingsRecyclerViewAdapter.ViewHolder>() {

//    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        private val TAG = "ViewHolder"
//
//        fun bind(rulings: CardRulings) {
//            for (ruling in rulings.rulings) {
//                val item = View.inflate(itemView.context, R.layout.row_table_ruling, null)
//                val text = item.findViewById(R.id.text_table_ruling) as TextView
//                when (ruling.type) {
//                    HeaderOrItem.Types.H2 -> {
//                        text.textSize = 20f
//                        text.setTypeface(text.typeface, Typeface.BOLD)
//                        text.text = ruling.data
//                    }
//                    HeaderOrItem.Types.H3 -> {
//                        text.textSize = 16f
//                        text.setTypeface(text.typeface, Typeface.BOLD)
//                        text.text = ruling.data
//                    }
//
//                    else -> {
//                        text.textSize = 14f
//                        text.text = "• " + ruling.data
//                    }
//                }
//                val table = itemView.findViewById<TableLayout>(R.id.table)
//                table.addView(item)
//            }
//        }
//
//    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ViewDataBinding? = DataBindingUtil.bind(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(R.layout.item_format_rulings, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardRulings = listOfCardRulings[position]
        for (ruling in cardRulings.rulings) {
            val item = View.inflate(holder.itemView.context, R.layout.item_ruling, null)
            val text = item.findViewById(R.id.text_table_ruling) as TextView
            when (ruling.type) {
                HeaderOrItem.Types.H2 -> {
                    text.textSize = 20f
                    text.setTypeface(text.typeface, Typeface.BOLD)
                    text.text = ruling.data
                }
                HeaderOrItem.Types.H3 -> {
                    text.textSize = 16f
                    text.setTypeface(text.typeface, Typeface.BOLD)
                    text.text = ruling.data
                }
                else -> {
                    text.textSize = 14f
                    text.text = "• " + ruling.data
                }
            }
            val linearLayout = holder.itemView.findViewById<LinearLayout>(R.id.ll_rulings)
            linearLayout.addView(item)
        }
    }

    override fun getItemCount(): Int {
        return listOfCardRulings.size
    }


}