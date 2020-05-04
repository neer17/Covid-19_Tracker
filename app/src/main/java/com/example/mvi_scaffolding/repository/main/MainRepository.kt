package com.example.mvi_scaffolding.repository.main

import androidx.lifecycle.LiveData
import com.example.mvi_scaffolding.api.main.OpenApiMainService
import com.example.mvi_scaffolding.api.main.network_responses.NationalData
import com.example.mvi_scaffolding.repository.JobManager
import com.example.mvi_scaffolding.repository.NetworkBoundResource
import com.example.mvi_scaffolding.session.SessionManager
import com.example.mvi_scaffolding.ui.DataState
import com.example.mvi_scaffolding.ui.main.state.MainViewState
import com.example.mvi_scaffolding.utils.AbsentLiveData
import com.example.mvi_scaffolding.utils.ApiSuccessResponse
import com.example.mvi_scaffolding.utils.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager
) : JobManager("MainRepository") {

    private val TAG = "AppDebug: " + MainRepository::class.java.simpleName

    fun getNationalData(): LiveData<DataState<MainViewState>> {
        return object : NetworkBoundResource<NationalData, Any, MainViewState>(
            sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = false,
            shouldCancelIfNoInternet = true
        ){
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<NationalData>) {
               onCompleteJob(
                   DataState.data(
                       data = MainViewState(
                           nationalData = response.body
                       )
                   )
               )
            }

            override fun createCall(): LiveData<GenericApiResponse<NationalData>> {
                return openApiMainService.getAllData()
            }

            override fun setJob(job: Job) {
                addJob("getNationalData", job)
            }

            override fun loadFromCache(): LiveData<MainViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
            }
        }.asLiveData()
    }

}
