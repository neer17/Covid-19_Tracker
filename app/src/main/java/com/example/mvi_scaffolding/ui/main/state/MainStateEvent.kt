package com.example.mvi_scaffolding.ui.main.state

sealed class MainStateEvent  {

    class GetNationalDataNetworkEvent: MainStateEvent()

    class GetNationalDataCacheEvent: MainStateEvent()

    class GetTimeSeriesNetworkEvent : MainStateEvent()

    class GetTimeSeriesCacheEvent : MainStateEvent()

    class GetNationalResourceNetworkEvent : MainStateEvent()

    class GetNationalResourceCacheEvent: MainStateEvent()

    class WarnUsersOfContraction: MainStateEvent()

    class None: MainStateEvent()
}