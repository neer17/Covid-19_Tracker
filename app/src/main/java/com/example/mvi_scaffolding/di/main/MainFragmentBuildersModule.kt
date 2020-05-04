package com.example.mvi_scaffolding.di.main

import com.example.mvi_scaffolding.ui.main.CovidUpdatesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): CovidUpdatesFragment
}