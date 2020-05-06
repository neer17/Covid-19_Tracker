package com.example.mvi_scaffolding.ui.main

import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mvi_scaffolding.R
import com.example.mvi_scaffolding.api.main.network_responses.NationalDataResponse
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.GetNationalDataEvent
import com.google.android.gms.location.LocationServices
import java.util.*


class CovidUpdatesFragment : BaseMainFragment() {

    lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_covid_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geocoder = Geocoder(activity, Locale.getDefault())

        //  making an event to trigger data request
        viewModel.setStateEvent(GetNationalDataEvent())

        getUsersLocation()

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

        //  observe data -> location -> update UI
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { mainViewState ->
                //  set data from api request
                mainViewState.nationalData?.let { nationalData ->

//                   Log.d(TAG, "subscribeObservers: nation wide data ${it.nationWideDataList}")
                    //  observing location
                    mainViewState.location?.let {
                        val state =
                            geocoder.getFromLocation(it.latitude, it.longitude, 1)[0].adminArea
                        Log.d(TAG, "subscribeObservers: state $state")

                        updateCard(nationalData.nationWideDataList, state)
                    } ?: updateCard(nationalData.nationWideDataList)
                }
            }
        })
    }

    private fun getUsersLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(view!!.context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d(TAG, "getUsersLocation: location $location")

                location?.let {
                    viewModel.setCurrentLocation(it)
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "getUsersLocation: error on getting location", exception)

            }
    }

    //  update UI
    private fun updateCard(data: List<NationalDataResponse>, state: String = "") {
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
    }

    //  return NationalDataResponse based on current state
    private fun getTheDataOfState(
        data: List<NationalDataResponse>,
        state: String
    ): NationalDataResponse {
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
