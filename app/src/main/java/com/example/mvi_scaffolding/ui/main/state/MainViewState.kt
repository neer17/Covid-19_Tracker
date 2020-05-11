package com.example.mvi_scaffolding.ui.main.state

import com.example.mvi_scaffolding.api.main.network_responses.NationalData
import com.example.mvi_scaffolding.api.main.network_responses.NationalResource
import com.example.mvi_scaffolding.api.main.network_responses.TimeSeries

data class MainViewState(
    var nationalData: NationalData? = null,
    var timeSeries: TimeSeries? = null,
    var nationalResource: NationalResource? = null,
    var cityAndState: Array<String>? = null,
    var internetConnectivity: Boolean? = null,
    var threatLevel: String? = null
)
