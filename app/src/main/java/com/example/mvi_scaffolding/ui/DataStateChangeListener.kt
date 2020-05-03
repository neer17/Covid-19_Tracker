package com.example.mvi_scaffolding.ui



interface DataStateChangeListener{

    fun onDataStateChange(dataState: DataState<*>?)
}