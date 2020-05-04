package com.example.mvi_scaffolding.ui.main

import androidx.lifecycle.LiveData
import com.example.mvi_scaffolding.repository.main.MainRepository
import com.example.mvi_scaffolding.ui.BaseViewModel
import com.example.mvi_scaffolding.ui.DataState
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.GetNationalDataEvent
import com.example.mvi_scaffolding.ui.main.state.MainStateEvent.None
import com.example.mvi_scaffolding.ui.main.state.MainViewState
import com.example.mvi_scaffolding.utils.AbsentLiveData
import javax.inject.Inject

class MainViewModel
@Inject
constructor(val mainRepository: MainRepository) : BaseViewModel<MainStateEvent, MainViewState>() {
    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
      when(stateEvent) {
          is GetNationalDataEvent -> {
             return mainRepository.getNationalData()
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
        cancelActiveJobs()
    }

    fun cancelActiveJobs(){
        mainRepository.cancelActiveJobs() // cancel active jobs
    }
}