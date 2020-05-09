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
import com.facebook.shimmer.ShimmerFrameLayout


class CovidUpdatesFragment : BaseMainFragment() {
    lateinit var shimmerContainer: ShimmerFrameLayout

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

        subscribeObservers()

        view.findViewById<TextView>(R.id.view_data_by_state_tv).setOnClickListener {
            findNavController().navigate(R.id.action_covidUpdatesFragment_to_nationWideDataDisplayFragment)
        }
    }

    override fun onResume() {
        super.onResume()
    }


    private fun subscribeObservers() {
        //  observe data -> location -> update UI
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { mainViewState ->
                //  set data from api request
                mainViewState.nationalData?.let { nationalData ->
//
//                    Log.d(
//                        TAG,
//                        "subscribeObservers: nation wide data ${nationalData.nationWideDataList}"
//                    )
                    //  observing location, shimmer animation started
                    shimmerContainer.startShimmer()

                    mainViewState.cityAndState?.let {
                        updateCard(nationalData.nationWideDataList, it[1])
                    } ?: updateCard(nationalData.nationWideDataList)
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
