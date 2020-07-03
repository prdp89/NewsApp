package com.android.grab.newsapp.datasource.roomdb.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.grab.newsapp.datasource.roomdb.entity.NewsRowEntity

@Dao
interface NewsRowDao {
    @Query("SELECT * FROM news_desc")
    suspend fun loadNewsData(): List<NewsRowEntity>

    @Query("SELECT * FROM news_desc where id =:id")
    suspend fun loadNewsDetail(id: Int): NewsRowEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsData(trendingRepoEntity: NewsRowEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewsDataList(trendingRepoEntityList: List<NewsRowEntity>)

    @Query("DELETE FROM news_desc")
    suspend fun deleteNewsData()
}