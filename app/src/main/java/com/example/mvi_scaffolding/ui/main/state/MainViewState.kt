package com.example.mvi_scaffolding.ui.main.state

import com.example.mvi_scaffolding.api.main.network_responses.NationalData
import com.example.mvi_scaffolding.api.main.network_responses.NationalResource

data class MainViewState(
    var nationalData: NationalData? = null,
    var nationalResource: NationalResource? = null,
    var cityAndState: Array<String>? = null,
    var internetConnectivity: Boolean? = null
)