package com.example.mvi_scaffolding.di.main

import androidx.lifecycle.ViewModel
import com.example.mvi_scaffolding.di.ViewModelKey
import com.example.mvi_scaffolding.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindAccountViewModel(mainViewModel: MainViewModel): ViewModel
}