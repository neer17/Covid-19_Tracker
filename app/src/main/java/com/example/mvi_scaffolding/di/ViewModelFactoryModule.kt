package com.example.mvi_scaffolding.di

import androidx.lifecycle.ViewModelProvider
import com.example.mvi_scaffolding.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}