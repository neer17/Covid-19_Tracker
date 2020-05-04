package com.example.mvi_scaffolding.ui.main.state

sealed class MainStateEvent  {

    class GetNationalDataEvent: MainStateEvent()

    class None: MainStateEvent()
}