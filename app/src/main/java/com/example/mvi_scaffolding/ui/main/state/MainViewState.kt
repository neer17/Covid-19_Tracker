package com.example.mvi_scaffolding.ui.main.state

import android.location.Location
import com.example.mvi_scaffolding.api.main.network_responses.NationalData
import com.example.mvi_scaffolding.api.main.network_responses.NationalResource

data class MainViewState(
    var nationalData: NationalData? = null,
    var nationalResource: NationalResource? = null,
    var location: Location? = null,
    var internetConnectivity: Boolean? = null
)