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
              Log.w(TAG, "handleStateEvent: GetNationalDataNetworkEvent")
              
             return mainRepository.getNationalData(isNetworkRequest = true)
          }
          is GetTimeSeriesNetworkEvent -> {
              Log.w(TAG, "handleStateEvent: GetTimeSeriesNetworkEvent")

              return mainRepository.getTimeSeries(isNetworkRequest = true)
          }
          is GetNationalResourceNetworkEvent -> {
              Log.w(TAG, "handleStateEvent: GetNationalResourceNetworkEvent")
              
              return mainRepository.getNationalResources(isNetworkRequest = true)
          }
          
          is GetNationalDataCacheEvent -> {
              Log.w(TAG, "handleStateEvent: GetNationalDataCacheEvent")
              
              return mainRepository.getNationalData(isNetworkRequest = false)
          }
          is GetTimeSeriesCacheEvent -> {
              Log.w(TAG, "handleStateEvent: GetTimeSeriesCacheEvent")
              return mainRepository.getTimeSeries(isNetworkRequest = false)
          }
          is GetNationalResourceCacheEvent -> {
              Log.w(TAG, "handleStateEvent: GetNationalResourceCacheEvent")
              
              return mainRepository.getNationalResources(isNetworkRequest = false)
          }
          is WarnUsersOfContraction -> {
              return AbsentLiveData.create()
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


    fun setThreatLevel(threatLevel: String) {
        val update = getCurrentViewStateOrNew()
        update.threatLevel = threatLevel
        _viewState.value = update
    }

    fun setContractionLocation(location: Array<Double>) {
        val update = getCurrentViewStateOrNew()
        update.contractionLocation = location
        _viewState.value = update
    }

    fun setContractionTime(time: Long) {
        val update = getCurrentViewStateOrNew()
        update.contractionTime = time
        _viewState.value = update
    }

}