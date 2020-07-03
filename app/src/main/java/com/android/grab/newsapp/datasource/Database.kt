package com.android.grab.newsapp.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.grab.newsapp.datasource.roomdb.dao.NewsRowDao
import com.android.grab.newsapp.datasource.roomdb.entity.NewsRowEntity

@Database(
    entities = [NewsRowEntity::class]
    , version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun trendingRepoDao(): NewsRowDao
}