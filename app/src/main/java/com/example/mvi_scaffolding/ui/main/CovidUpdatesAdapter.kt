package com.example.mvi_scaffolding.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.models.NationalDataTable

class CovidUpdatesAdapter(val getUpdatedStringsCallback: (position: Int) -> Array<String>) :
    ListAdapter<NationalDataTable, CovidUpdatesAdapter.ViewHolder>(CovidUpdatesAdapter.DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NationalDataTable>() {
            override fun areItemsTheSame(
                oldItem: NationalDataTable,
                newItem: NationalDataTable
            ): Boolean {
                return oldItem.state == newItem.state
            }

            override fun areContentsTheSame(
                oldItem: NationalDataTable,
                newItem: NationalDataTable
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_covid_updates_view, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val updatedStrings: Array<String> = getUpdatedStringsCallback(position)
        holder.bind(getItem(position), updatedStrings)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: NationalDataTable, updatedStrings: Array<String>) = with(itemView) {
            itemView.findViewById<TextView>(R.id.state_name).text = item.state
            itemView.findViewById<TextView>(R.id.confirmed_total).text = item.confirmed
            itemView.findViewById<TextView>(R.id.recovered_total).text = item.recovered
            itemView.findViewById<TextView>(R.id.deceased_total).text = item.deaths
            itemView.findViewById<TextView>(R.id.confirmed_increase).text = updatedStrings[0]
            itemView.findViewById<TextView>(R.id.recovered_increase).text = updatedStrings[1]
            itemView.findViewById<TextView>(R.id.deceased_increase).text = updatedStrings[2]
        }
    }
}