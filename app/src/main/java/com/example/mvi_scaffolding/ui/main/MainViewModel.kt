package com.example.mvi_scaffolding.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.mvi_scaffolding.api.main.network_responses.NationalData
import com.example.mvi_scaffolding.api.main.network_responses.NationalResource
import com.example.mvi_scaffolding.api.main.network_responses.TimeSeries
import com.example.mvi_scaffolding.repository.main.MainRepository
import com.example.mvi_scaffolding.ui.BaseViewModel
import com.example.mvi_scaffolding.ui.DataState
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.*
import com.example.mvi_scaffolding.ui.main.state.MainViewState
import com.example.mvi_scaffolding.utils.AbsentLiveData
import javax.inject.Inject

class MainViewModel
@Inject
constructor(val mainRepository: MainRepository) : BaseViewModel<MainStateEvent, MainViewState>() {
    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
      when(stateEvent) {
          is GetNationalDataNetworkEvent -> {
              Log.d(TAG, "handleStateEvent: GetNationalDataNetworkEvent")
              
             return mainRepository.getNationalData(isNetworkRequest = true)
          }
          is GetTimeSeriesNetworkEvent -> {
              Log.d(TAG, "handleStateEvent: GetTimeSeriesNetworkEvent")

              return mainRepository.getNationalData(isNetworkRequest = true)
          }
          is GetNationalResourceNetworkEvent -> {
              Log.d(TAG, "handleStateEvent: GetNationalResourceNetworkEvent")
              
              return mainRepository.getNationalResources(isNetworkRequest = true)
          }
          
          is GetNationalDataCacheEvent -> {
              Log.d(TAG, "handleStateEvent: GetNationalDataCacheEvent")
              
              return mainRepository.getNationalData(isNetworkRequest = false)
          }
          is GetTimeSeriesCacheEvent -> {
              Log.d(TAG, "handleStateEvent: GetTimeSeriesCacheEvent")
              return mainRepository.getTimeSeries(isNetworkRequest = false)
          }
          is GetNationalResourceCacheEvent -> {
              Log.d(TAG, "handleStateEvent: GetNationalResourceCacheEvent")
              
              return mainRepository.getNationalResources(isNetworkRequest = false)
          }
          is None -> {
              return AbsentLiveData.create()
          }
      }
    }

    override fun initNewViewState(): MainViewState {
       return MainViewState()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
        
        cancelActiveJobs()
    }

    fun cancelActiveJobs(){
        mainRepository.cancelActiveJobs() // cancel active jobs
    }

    fun setNationalData(nationalData: NationalData) {
        val update = getCurrentViewStateOrNew()
        update.nationalData = nationalData
        _viewState.value = update
    }

    fun setTimeSeries(timeSeries: TimeSeries) {
        val update = getCurrentViewStateOrNew()
        update.timeSeries = timeSeries
        _viewState.value = update
    }

    fun setNationalResource(nationalResource: NationalResource) {
        val update = getCurrentViewStateOrNew()
        update.nationalResource = nationalResource
        _viewState.value = update
    }

    fun setCurrentLocation(cityAndState: Array<String>) {
        val update = getCurrentViewStateOrNew()
        update.cityAndState = cityAndState
        _viewState.value = update
    }

    fun setInternetConnectivity(internetConnectivity: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.internetConnectivity = internetConnectivity
        _viewState.value = update
    }
}