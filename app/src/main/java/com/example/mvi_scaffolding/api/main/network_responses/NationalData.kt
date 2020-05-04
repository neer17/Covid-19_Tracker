package com.example.mvi_scaffolding.api.main.network_responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NationalData(
    @SerializedName("statewise")
    @Expose
    val nationalData: List<NationalDataResponse>
)

data class NationalDataResponse(
    val active: String,
    val confirmed: String,
    val deaths: String,
    val deltaconfirmed: String,
    val deltadeaths: String,
    val deltarecovered: String,
    val lastupdatedtime: String,
    val recovered: String,
    val state: String,
    val statecode: String
)