package com.example.mvi_scaffolding.ui.main.state

import com.example.mvi_scaffolding.api.main.network_responses.NationalData

data class MainViewState(
    val nationalData: NationalData? = null
)