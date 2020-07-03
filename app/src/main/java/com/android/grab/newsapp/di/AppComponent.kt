package com.android.grab.newsapp.di

import com.android.grab.newsapp.NewsApplication
import com.android.grab.newsapp.di.modules.AppModule
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent : AndroidInjector<NewsApplication> {

    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<NewsApplication>
}