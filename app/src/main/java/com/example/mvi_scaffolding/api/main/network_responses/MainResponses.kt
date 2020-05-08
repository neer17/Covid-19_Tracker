package com.example.mvi_scaffolding.api.main.network_responses

import com.example.mvi_scaffolding.models.NationalDataTable
import com.example.mvi_scaffolding.models.NationalResourceTable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NationalData(
    @SerializedName("statewise")
    @Expose
    val nationWideDataList: List<NationalDataTable>
)

data class NationalResource(
    @SerializedName("resources")
    @Expose
    val nationalResource: List<NationalResourceTable>
)
