package com.example.mvi_scaffolding.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContractionDetails(
    var lat: Double,
    var lang: Double,
    var time: Long
)