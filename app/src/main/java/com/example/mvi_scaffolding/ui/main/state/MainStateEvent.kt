package com.example.mvi_scaffolding.ui.main.state

sealed class MainStateEvent  {

    class GetNationalDataNetworkEvent: MainStateEvent()

    class GetNationalDataCacheEvent: MainStateEvent()

    class GetNationalResourceNetworkEvent: MainStateEvent()

    class GetNationalResourceCacheEvent: MainStateEvent()

    class None: MainStateEvent()
}