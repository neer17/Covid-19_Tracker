package com.example.mvi_scaffolding.api.main

import androidx.lifecycle.LiveData
import com.example.mvi_scaffolding.api.main.network_responses.NationalData
import com.example.mvi_scaffolding.api.main.network_responses.NationalResource
import com.example.mvi_scaffolding.api.main.network_responses.TimeSeries
import com.example.mvi_scaffolding.utils.GenericApiResponse
import retrofit2.http.GET

interface OpenApiMainService {
    //  to get all data
    @GET("/data.json")
    fun getAllData(): LiveData<GenericApiResponse<NationalData>>

    // to get time series
    @GET("/data.json")
    fun getTimeSeries(): LiveData<GenericApiResponse<TimeSeries>>

    //  get all the resources
    @GET("/resources/resources.json")
    fun getResources(): LiveData<GenericApiResponse<NationalResource>>
}