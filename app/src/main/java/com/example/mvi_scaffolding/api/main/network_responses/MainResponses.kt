package com.example.mvi_scaffolding.api.main.network_responses

import com.example.mvi_scaffolding.models.NationalDataTable
import com.example.mvi_scaffolding.models.NationalResourceTable
import com.example.mvi_scaffolding.models.TimeSeriesTable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NationalData(
    @SerializedName("statewise")
    @Expose
    val nationWideDataList: List<NationalDataTable>
)

data class TimeSeries(
    @SerializedName("cases_time_series")
    @Expose
    val timeSeriesDataList: List<TimeSeriesTable>
)

data class NationalResource(
    @SerializedName("resources")
    @Expose
    val nationalResource: List<NationalResourceTable>
)
