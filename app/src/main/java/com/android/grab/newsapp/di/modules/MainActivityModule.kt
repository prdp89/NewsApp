package com.android.grab.newsapp.di.modules

import com.android.grab.newsapp.ui.main.MainFragment
import com.android.grab.newsapp.ui.newsdetail.NewsDetailFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsDetailFragment(): NewsDetailFragment
}