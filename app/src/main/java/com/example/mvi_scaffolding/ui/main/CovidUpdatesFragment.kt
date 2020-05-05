package com.example.mvi_scaffolding.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.api.main.network_responses.NationalDataResponse
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.GetNationalDataEvent

class CovidUpdatesFragment : BaseMainFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_covid_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //  making an event to trigger data request
        viewModel.setStateEvent(GetNationalDataEvent())

        subscribeObservers()

        view.findViewById<TextView>(R.id.view_data_by_state_tv).setOnClickListener {
            findNavController().navigate(R.id.action_covidUpdatesFragment_to_nationWideDataDisplayFragment)
        }
    }


    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState.data?.let {
                it.data?.let {
                    it.getContentIfNotHandled()?.let {
                        it.nationalData?.let { nationalData ->
                            viewModel.setNationalData(nationalData) //  updating view state
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { viewState ->
                viewState.nationalData?.let {
//                   Log.d(TAG, "subscribeObservers: nation wide data ${it.nationWideDataList}")
                    updateCard(it.nationWideDataList)
                }
            }
        })
    }

    private fun updateCard(data: List<NationalDataResponse>) {
        val indiaData = data[0]
        val indiaConfirmedTotal = indiaData.confirmed
        val indiaRecoveredTotal = indiaData.recovered
        val indiaDeceasedTotal = indiaData.deaths

        val indiaUpdatedStrings: Array<String> = addIncDecSymbol(indiaData)
        val indiaConfirmedDelta = indiaUpdatedStrings[0]
        val indiaRecoveredDelta = indiaUpdatedStrings[1]
        val indiaDeceasedDelta = indiaUpdatedStrings[2]


        //  for the state based on user's location
        val stateConfirmedTotal = data[1].confirmed
        val stateRecoveredTotal = data[1].recovered
        val stateDeceasedTotal = data[1].deaths

        val stateUpdatedStrings: Array<String> = addIncDecSymbol(data[1])
        val stateConfirmedDelta = stateUpdatedStrings[0]
        val stateRecoveredDelta = stateUpdatedStrings[1]
        val stateDeceasedDelta = stateUpdatedStrings[2]

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.cancelActiveJobs()
    }

}
