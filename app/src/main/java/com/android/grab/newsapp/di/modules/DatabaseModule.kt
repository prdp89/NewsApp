package com.android.grab.newsapp.di.modules

import android.app.Application
import androidx.room.Room
import com.android.grab.newsapp.datasource.Database
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): Database =
        Room.databaseBuilder(app, Database::class.java, "news_db")
            .build()
}