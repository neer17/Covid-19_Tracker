package com.example.mvi_scaffolding.di.main

import com.example.mvi_scaffolding.ui.main.AssessmentFragment
import com.example.mvi_scaffolding.ui.main.CovidUpdatesFragment
import com.example.mvi_scaffolding.ui.main.HomeFragment
import com.example.mvi_scaffolding.ui.main.NationWideDataDisplayFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeCovidUpdatesFragment(): CovidUpdatesFragment

    @ContributesAndroidInjector()
    abstract fun contributeNationWideDataDisplayFragment(): NationWideDataDisplayFragment

    @ContributesAndroidInjector()
    abstract fun contributeAssessmentFragment(): AssessmentFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment
}