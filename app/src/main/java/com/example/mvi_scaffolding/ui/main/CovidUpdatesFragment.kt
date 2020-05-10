package com.example.mvi_scaffolding.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.models.NationalDataTable
import com.example.mvi_scaffolding.models.TimeSeriesTable
import com.facebook.shimmer.ShimmerFrameLayout
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*


class CovidUpdatesFragment : BaseMainFragment() {
    companion object {
        private val PRIMARY_RED = Color.parseColor("#ff073a")
        private val SECONDARY_RED = Color.parseColor("#ff073a99")
        private val TEXT_RED = Color.parseColor("#ff073a99")
        private val PRIMARY_GREEN = Color.parseColor("#28a745")
        private val SECONDARY_GREEN = Color.parseColor("#28a74599")
        private val TEXT_GREEN = Color.parseColor("#28a74599")
        private val PRIMARY_GRAY = Color.parseColor("#6c757d")
        private val SECONDARY_GRAY = Color.parseColor("#6c757d99")
        private val TEXT_GRAY = Color.parseColor("#6c757d99")
    }

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
        graph.description = null
        graph.legend.isEnabled = false
        graph.xAxis.position = XAxis.XAxisPosition.BOTTOM
        graph.xAxis.setDrawGridLines(false)
        graph.axisLeft.isEnabled = false
        graph.axisRight.setDrawGridLines(false)

        // listen for tab change
        graphTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // reset graph data
                graph.data = null
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                // if data non-null update graph
                when (tab?.position) {
                    0 -> {
                        graph.setBorderColor(PRIMARY_RED)
                        graph.setBackgroundColor(PRIMARY_RED)
                        graph.xAxis.axisLineColor = PRIMARY_RED
                        graph.xAxis.textColor = TEXT_RED
                        mTimeSeriesData?.let { updateGraph(it) }
                    }
                    1 -> mTimeSeriesData?.let { updateGraph(it, PRIMARY_GREEN, SECONDARY_GREEN) }
                }
                mTimeSeriesData?.let { updateGraph(it) }
            }

        })


        subscribeObservers()

        view.findViewById<TextView>(R.id.view_data_by_state_tv).setOnClickListener {
            findNavController().navigate(R.id.action_covidUpdatesFragment_to_nationWideDataDisplayFragment)
        }
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

                Log.d(TAG, "timeSeries:: ${mainViewState.timeSeries}")

                // set data from api request
                mainViewState.timeSeries?.let { timeSeries ->
//                    mTimeSeriesData = timeSeries.timeSeriesDataList
                    mTimeSeriesData = arrayListOf(
                        TimeSeriesTable(1, "871", "22", "151", "10 April ", "7599", "249", "786"),
                        TimeSeriesTable(1, "854", "41", "186", "11 April ", "8453", "290", "972"),
                        TimeSeriesTable(1, "758", "42", "114", "12 April ", "9211", "332", "1086"),
                        TimeSeriesTable(
                            1,
                            "1243",
                            "27",
                            "112",
                            "13 April ",
                            "10454",
                            "359",
                            "1198"
                        ),
                        TimeSeriesTable(
                            1,
                            "1031",
                            "37",
                            "167",
                            "14 April ",
                            "11485",
                            "396",
                            "1365"
                        ),
                        TimeSeriesTable(1, "886", "27", "144", "15 April ", "12371", "423", "1509"),
                        TimeSeriesTable(
                            1,
                            "1061",
                            "26",
                            "258",
                            "16 April ",
                            "13432",
                            "449",
                            "1767"
                        ),
                        TimeSeriesTable(1, "922", "38", "273", "17 April ", "14354", "487", "2040"),
                        TimeSeriesTable(
                            1,
                            "1371",
                            "35",
                            "426",
                            "18 April ",
                            "15725",
                            "522",
                            "2466"
                        ),
                        TimeSeriesTable(
                            1,
                            "1580",
                            "38",
                            "388",
                            "19 April ",
                            "17305",
                            "560",
                            "2854"
                        ),
                        TimeSeriesTable(
                            1,
                            "1239",
                            "33",
                            "419",
                            "20 April ",
                            "18544",
                            "593",
                            "3273"
                        ),
                        TimeSeriesTable(
                            1,
                            "1537",
                            "53",
                            "703",
                            "21 April ",
                            "20081",
                            "646",
                            "3976"
                        ),
                        TimeSeriesTable(
                            1,
                            "1292",
                            "36",
                            "394",
                            "22 April ",
                            "21373",
                            "682",
                            "4370"
                        ),
                        TimeSeriesTable(
                            1,
                            "1667",
                            "40",
                            "642",
                            "23 April ",
                            "23040",
                            "722",
                            "5012"
                        ),
                        TimeSeriesTable(
                            1,
                            "1408",
                            "59",
                            "484",
                            "24 April ",
                            "24448",
                            "781",
                            "5496"
                        ),
                        TimeSeriesTable(
                            1,
                            "1835",
                            "44",
                            "442",
                            "25 April ",
                            "26283",
                            "825",
                            "5938"
                        ),
                        TimeSeriesTable(
                            1,
                            "1607",
                            "56",
                            "585",
                            "26 April ",
                            "27890",
                            "881",
                            "6523"
                        ),
                        TimeSeriesTable(
                            1,
                            "1568",
                            "58",
                            "580",
                            "27 April ",
                            "29458",
                            "939",
                            "7103"
                        ),
                        TimeSeriesTable(
                            1,
                            "1902",
                            "69",
                            "636",
                            "28 April ",
                            "31360",
                            "1008",
                            "7739"
                        ),
                        TimeSeriesTable(
                            1,
                            "1705",
                            "71",
                            "690",
                            "29 April ",
                            "33065",
                            "1079",
                            "8429"
                        ),
                        TimeSeriesTable(
                            1,
                            "1801",
                            "75",
                            "630",
                            "30 April ",
                            "34866",
                            "1154",
                            "9059"
                        ),
                        TimeSeriesTable(
                            1,
                            "2396",
                            "77",
                            "962",
                            "01 May ",
                            "37262",
                            "1231",
                            "10021"
                        ),
                        TimeSeriesTable(
                            1,
                            "2564",
                            "92",
                            "831",
                            "02 May ",
                            "39826",
                            "1323",
                            "10852"
                        ),
                        TimeSeriesTable(
                            1,
                            "2952",
                            "140",
                            "911",
                            "03 May ",
                            "42778",
                            "1463",
                            "11763"
                        ),
                        TimeSeriesTable(
                            1,
                            "3656",
                            "103",
                            "1082",
                            "04 May ",
                            "46434",
                            "1566",
                            "12845"
                        ),
                        TimeSeriesTable(
                            1,
                            "2971",
                            "128",
                            "1295",
                            "05 May ",
                            "49405",
                            "1694",
                            "14140"
                        ),
                        TimeSeriesTable(
                            1,
                            "3602",
                            "91",
                            "1161",
                            "06 May ",
                            "53007",
                            "1785",
                            "15301"
                        ),
                        TimeSeriesTable(
                            1,
                            "3344",
                            "104",
                            "1475",
                            "07 May ",
                            "56351",
                            "1889",
                            "16776"
                        ),
                        TimeSeriesTable(
                            1,
                            "3339",
                            "97",
                            "1111",
                            "08 May ",
                            "59690",
                            "1986",
                            "17887"
                        ),
                        TimeSeriesTable(
                            1,
                            "3175",
                            "115",
                            "1414",
                            "09 May ",
                            "62865",
                            "2101",
                            "19301"
                        )
                    )
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
    private fun updateGraph(
        data: List<TimeSeriesTable>,
        primaryColor: Int = PRIMARY_RED,
        secondaryColor: Int = SECONDARY_RED
    ) {
        val entries = arrayListOf<Entry>()
        val dates = arrayListOf<String>()

        data.forEach {
            dates.add(it.date)
            val date = "${it.date}2020"
            val millis = SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
                .parse(date)!!.time

//            val l = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd MMMM yyyy"))
//            val unix = l.atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond

            when (graphTab.selectedTabPosition) {
                0 -> entries.add(Entry(millis.toFloat(), it.totalconfirmed.toFloat()))
                1 -> entries.add(Entry(millis.toFloat(), it.totalrecovered.toFloat()))
                2 -> entries.add(Entry(millis.toFloat(), it.totaldeceased.toFloat()))
            }
        }

        // the labels that should be drawn on the XAxis
        graph.xAxis.valueFormatter =
            object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return dates.getOrNull(value.toInt()) ?: value.toString()
                }
            }
//        graph.xAxis.granularity = 2f // minimum axis-step (interval) is 1

        graph.xAxis.axisLineColor = primaryColor
        graph.axisRight.axisLineColor = primaryColor
        val dataSet = LineDataSet(entries, "Covid Tracker")
        dataSet.setCircleColor(primaryColor)
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

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelActiveJobs()
    }
}
