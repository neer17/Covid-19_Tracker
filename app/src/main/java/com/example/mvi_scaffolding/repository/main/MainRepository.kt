package com.example.mvi_scaffolding.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mvi_scaffolding.api.main.OpenApiMainService
import com.example.mvi_scaffolding.api.main.network_responses.NationalData
import com.example.mvi_scaffolding.api.main.network_responses.NationalResource
import com.example.mvi_scaffolding.persistence.MainDao
import com.example.mvi_scaffolding.repository.JobManager
import com.example.mvi_scaffolding.repository.NetworkBoundResource
import com.example.mvi_scaffolding.session.SessionManager
import com.example.mvi_scaffolding.ui.DataState
import com.example.mvi_scaffolding.ui.main.state.MainViewState
import com.example.mvi_scaffolding.utils.ApiSuccessResponse
import com.example.mvi_scaffolding.utils.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager,
    val mainDao: MainDao
) : JobManager("MainRepository") {
    private val TAG = "AppDebug: " + MainRepository::class.java.simpleName

    fun getNationalData(): LiveData<DataState<MainViewState>> {
        return object : NetworkBoundResource<NationalData, Any, MainViewState>(
            sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = true,
            shouldCancelIfNoInternet = false
        ){
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) {viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<NationalData>) {
               val results =  mainDao.insertAllData(
                    response.body.nationWideDataList
                )
                Log.d(TAG, "handleApiSuccessResponse: insertAll results $results")
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<NationalData>> {
                return openApiMainService.getAllData()
            }

            override fun setJob(job: Job) {
                addJob("getNationalData", job)
            }

            override fun loadFromCache(): LiveData<MainViewState> {
                return mainDao.getAllData()
                    .switchMap {
                        object: LiveData<MainViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = MainViewState(nationalData = NationalData(it))
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
            }
        }.asLiveData()
    }

    fun getNationalResources(): LiveData<DataState<MainViewState>> {
        return object : NetworkBoundResource<NationalResource, Any, MainViewState>(
            sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = true,
            shouldCancelIfNoInternet = false
        ){
            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    result.addSource(loadFromCache()) {viewState ->
                        onCompleteJob(DataState.data(viewState, null))
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<NationalResource>) {
                val results =  mainDao.insertAllResources(
                    response.body.nationalResource
                )
                Log.d(TAG, "handleApiSuccessResponse: insertAll results $results")
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<NationalResource>> {
                return openApiMainService.getResources()
            }

            override fun setJob(job: Job) {
                addJob("getNationalResource", job)
            }

            override fun loadFromCache(): LiveData<MainViewState> {
                return mainDao.getAllResources()
                    .switchMap {
                        object: LiveData<MainViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = MainViewState(nationalResource = NationalResource(it))
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: Any?) {
            }
        }.asLiveData()
    }
}
