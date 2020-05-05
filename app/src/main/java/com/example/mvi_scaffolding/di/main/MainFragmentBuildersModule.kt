package com.example.mvi_scaffolding.di.main

import com.example.mvi_scaffolding.ui.main.CovidUpdatesFragment
import com.example.mvi_scaffolding.ui.main.NationWideDataDisplayFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeCovidUpdatesFragment(): CovidUpdatesFragment

    @ContributesAndroidInjector()
    abstract fun contributeNationWideDataDisplayFragment(): NationWideDataDisplayFragment
}