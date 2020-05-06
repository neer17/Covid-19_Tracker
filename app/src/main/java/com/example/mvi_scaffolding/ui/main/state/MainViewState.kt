package com.example.mvi_scaffolding.ui.main.state

import android.location.Location
import com.example.mvi_scaffolding.api.main.network_responses.NationalData

data class MainViewState(
    var nationalData: NationalData? = null,
    var location: Location? = null,
    var internetConnectivity: Boolean? = null
)