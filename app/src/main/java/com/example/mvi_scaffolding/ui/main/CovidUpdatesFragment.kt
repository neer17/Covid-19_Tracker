package com.example.mvi_scaffolding.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.models.NationalDataTable
import com.example.mvi_scaffolding.models.TimeSeriesTable
import com.example.mvi_scaffolding.utils.Constants
import com.example.mvi_scaffolding.utils.Constants.Companion.PRIMARY_GRAY
import com.example.mvi_scaffolding.utils.Constants.Companion.PRIMARY_GREEN
import com.example.mvi_scaffolding.utils.Constants.Companion.PRIMARY_RED
import com.example.mvi_scaffolding.utils.Constants.Companion.SECONDARY_GRAY
import com.example.mvi_scaffolding.utils.Constants.Companion.SECONDARY_GREEN
import com.example.mvi_scaffolding.utils.Constants.Companion.SECONDARY_RED
import com.example.mvi_scaffolding.utils.Constants.Companion.TEXT_GRAY
import com.example.mvi_scaffolding.utils.Constants.Companion.TEXT_GREEN
import com.example.mvi_scaffolding.utils.Constants.Companion.TEXT_RED
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_covid_updates.*
import java.text.SimpleDateFormat
import java.util.*


class CovidUpdatesFragment : BaseMainFragment() {
    lateinit var shimmerContainer: ShimmerFrameLayout
    lateinit var graph: LineChart
    lateinit var graphTab: TabLayout
    private var mTimeSeriesData: List<TimeSeriesTable>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_covid_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shimmerContainer = view.findViewById(R.id.shimmer_container_covid_card)
        graph = view.findViewById(R.id.covid_graph)
        graphTab = view.findViewById(R.id.covid_graph_tab)

        // setup graph
        setUpGraph()

        // listen for tab change
        graphTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // if data non-null update graph
                mTimeSeriesData?.let { updateGraph(it) }
                graphTab.setBackgroundColor(
                    when (tab?.position) {
                        0 -> PRIMARY_RED
                        1 -> PRIMARY_GREEN
                        2 -> PRIMARY_GRAY
                        else -> PRIMARY_RED
                    }
                )
            }
        })

        subscribeObservers()

        view.findViewById<TextView>(R.id.view_data_by_state_tv).setOnClickListener {
            findNavController().navigate(R.id.action_covidUpdatesFragment_to_nationWideDataDisplayFragment)
        }

        setLastUpdatedTime()
    }

    private fun setUpGraph() {
        graph.description = null
        graph.setMaxVisibleValueCount(0)
        graph.legend.isEnabled = false
        graph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        graph.xAxis.setDrawGridLines(false)
        graph.axisLeft.isEnabled = false
        graph.axisRight.setDrawGridLines(false)
    }


    private fun subscribeObservers() {
        //  observe data -> location -> update UI
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { mainViewState ->
                //  set data from api request
                mainViewState.nationalData?.let { nationalData ->
                    //  observing location, shimmer animation started
                    shimmerContainer.startShimmer()

                    mainViewState.cityAndState?.let {
                        updateCard(nationalData.nationWideDataList, it[1])
                    } ?: updateCard(nationalData.nationWideDataList)
                }

                // set data from api request
                mainViewState.timeSeries?.let { timeSeries ->
                    mTimeSeriesData = timeSeries.timeSeriesDataList.takeLast(30)
                    updateGraph(mTimeSeriesData!!)
                }
            }
        })
    }

    //  update UI
    private fun updateCard(data: List<NationalDataTable>, state: String = "") {
        val indiaData = data[0]
        val indiaConfirmedTotal = indiaData.confirmed
        val indiaRecoveredTotal = indiaData.recovered
        val indiaDeceasedTotal = indiaData.deaths

        val indiaUpdatedStrings: Array<String> = addIncDecSymbol(indiaData)
        val indiaConfirmedDelta = indiaUpdatedStrings[0]
        val indiaRecoveredDelta = indiaUpdatedStrings[1]
        val indiaDeceasedDelta = indiaUpdatedStrings[2]


        //  get data based on current location or data[1] by default
        val dataOfCurrentState = if (state == "")
            data[1]
        else
            getTheDataOfState(data, state)

        val stateName = dataOfCurrentState.state
        val stateConfirmedTotal = dataOfCurrentState.confirmed
        val stateRecoveredTotal = dataOfCurrentState.recovered
        val stateDeceasedTotal = dataOfCurrentState.deaths

        val stateUpdatedStrings: Array<String> = addIncDecSymbol(dataOfCurrentState)
        val stateConfirmedDelta = stateUpdatedStrings[0]
        val stateRecoveredDelta = stateUpdatedStrings[1]
        val stateDeceasedDelta = stateUpdatedStrings[2]

        view!!.findViewById<TextView>(R.id.card_state_name).text = stateName
        view!!.findViewById<TextView>(R.id.card_india_confirmed_total).text = indiaConfirmedTotal
        view!!.findViewById<TextView>(R.id.card_india_recovered_total).text = indiaRecoveredTotal
        view!!.findViewById<TextView>(R.id.card_india_deceased_total).text = indiaDeceasedTotal
        view!!.findViewById<TextView>(R.id.card_india_confirmed_delta).text = indiaConfirmedDelta
        view!!.findViewById<TextView>(R.id.card_india_recovered_delta).text = indiaRecoveredDelta
        view!!.findViewById<TextView>(R.id.card_india_deceased_delta).text = indiaDeceasedDelta
        view!!.findViewById<TextView>(R.id.card_state_confirmed_total).text = stateConfirmedTotal
        view!!.findViewById<TextView>(R.id.card_state_recovered_total).text = stateRecoveredTotal
        view!!.findViewById<TextView>(R.id.card_state_deceased_total).text = stateDeceasedTotal
        view!!.findViewById<TextView>(R.id.card_state_confirmed_delta).text = stateConfirmedDelta
        view!!.findViewById<TextView>(R.id.card_state_recovered_delta).text = stateRecoveredDelta
        view!!.findViewById<TextView>(R.id.card_state_deceased_delta).text = stateDeceasedDelta

        //  stop shimmer
        shimmerContainer.stopShimmer()
    }

    // update graph
    private fun updateGraph(data: List<TimeSeriesTable>) {
        val entries = arrayListOf<Entry>()
        val dates = arrayListOf<String>()

        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
        val initialTime = formatter.parse("${data[0].date}2020")!!.time

        data.forEach {
            dates.add(it.date)
            val date = "${it.date}2020"
            val xData = (formatter.parse(date)!!.time - initialTime) / 86400000f

            when (graphTab.selectedTabPosition) {
                0 -> entries.add(Entry(xData, it.totalconfirmed.toFloat()))
                1 -> entries.add(Entry(xData, it.totalrecovered.toFloat()))
                2 -> entries.add(Entry(xData, it.totaldeceased.toFloat()))
            }
        }

        // the labels that should be drawn on the XAxis
        graph.xAxis.valueFormatter =
            object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return dates.getOrNull(value.toInt()) ?: value.toString()
                }
            }

        var primaryColor = PRIMARY_RED
        var secondaryColor = SECONDARY_RED
        var textColor = TEXT_RED
        when (graphTab.selectedTabPosition) {
            0 -> {
                primaryColor = PRIMARY_RED
                secondaryColor = SECONDARY_RED
                textColor = TEXT_RED
            }
            1 -> {
                primaryColor = PRIMARY_GREEN
                secondaryColor = SECONDARY_GREEN
                textColor = TEXT_GREEN
            }
            2 -> {
                primaryColor = PRIMARY_GRAY
                secondaryColor = SECONDARY_GRAY
                textColor = TEXT_GRAY
            }
        }

        graph.setBackgroundColor(secondaryColor)
        graph.xAxis.axisLineColor = primaryColor
        graph.xAxis.textColor = textColor
        graph.xAxis.axisLineColor = primaryColor
        graph.axisRight.axisLineColor = primaryColor
        graph.axisRight.textColor = textColor
        val dataSet = LineDataSet(entries, null)
        dataSet.setCircleColor(primaryColor)
        dataSet.circleHoleColor = primaryColor
        dataSet.color = textColor
        graph.data = LineData(dataSet)
        graph.invalidate()
    }

    //  return NationalDataTable based on current state
    private fun getTheDataOfState(
        data: List<NationalDataTable>,
        state: String
    ): NationalDataTable {
        data.forEach {
            if (state == it.state)
                return it
        }

        return data[1]
    }

    private fun setLastUpdatedTime() {
        val lastUpdatedTime = sharedPreferences.getLong(Constants.LATEST_UPDATED_TIME, 0L)
        if (lastUpdatedTime != 0L) {
            val simpleDateFormat = SimpleDateFormat("dd/MM hh:mm:ss", Locale.UK)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = lastUpdatedTime
            val formattedTime = simpleDateFormat.format(calendar.time)
            val updatedText = last_time_updated_tv.text.toString() + " " + formattedTime
            last_time_updated_tv.text = updatedText
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelActiveJobs()
    }
}
