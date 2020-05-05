package com.example.mvi_scaffolding.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.api.main.network_responses.NationalDataResponse

class CovidUpdatesAdapter :
    ListAdapter<NationalDataResponse, CovidUpdatesAdapter.ViewHolder>(CovidUpdatesAdapter.DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NationalDataResponse>() {
            override fun areItemsTheSame(
                oldItem: NationalDataResponse,
                newItem: NationalDataResponse
            ): Boolean {
                return oldItem.state == newItem.state
            }

            override fun areContentsTheSame(
                oldItem: NationalDataResponse,
                newItem: NationalDataResponse
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
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: NationalDataResponse) = with(itemView) {
            itemView.findViewById<TextView>(R.id.state_name).text = item.state
            itemView.findViewById<TextView>(R.id.confirmed_total).text = item.confirmed
            itemView.findViewById<TextView>(R.id.recovered_total).text = item.recovered
            itemView.findViewById<TextView>(R.id.deceased_total).text = item.deaths

            addIncDecSymbol(item)
        }

        private fun addIncDecSymbol(item: NationalDataResponse) {
            var finalStringDeltaConfirmed = ""
            var finalStringDeltaRecovered = ""
            var finalStringDeltaDeceased = ""

            if (item.deltaconfirmed.toInt() > 0) {
                val upArrowHexCode = "A71B".toInt(16).toChar()
                finalStringDeltaConfirmed = upArrowHexCode + " " + item.deltaconfirmed
            } else if (item.deltaconfirmed.toInt() < 0) {
                val downArrowHexCode = "A71C".toInt(16).toChar()
                finalStringDeltaConfirmed = downArrowHexCode + " " + item.deltaconfirmed
            }

            if (item.deltarecovered.toInt() > 0) {
                val upArrowHexCode = "A71B".toInt(16).toChar()
                finalStringDeltaRecovered = upArrowHexCode + " " + item.deltarecovered
            } else if (item.deltaconfirmed.toInt() < 0) {
                val downArrowHexCode = "A71C".toInt(16).toChar()
                finalStringDeltaRecovered = downArrowHexCode + " " + item.deltarecovered
            }

            if (item.deltadeaths.toInt() > 0) {
                val upArrowHexCode = "A71B".toInt(16).toChar()
                finalStringDeltaDeceased = upArrowHexCode + " " + item.deltadeaths
            } else if (item.deltadeaths.toInt() < 0) {
                val downArrowHexCode = "A71C".toInt(16).toChar()
                finalStringDeltaDeceased = downArrowHexCode + " " + item.deltadeaths
            }

            itemView.findViewById<TextView>(R.id.confirmed_increase).text =
                finalStringDeltaConfirmed
            itemView.findViewById<TextView>(R.id.deceased_increase).text =
                finalStringDeltaDeceased
            itemView.findViewById<TextView>(R.id.recovered_increase).text =
                finalStringDeltaRecovered
        }
    }
}