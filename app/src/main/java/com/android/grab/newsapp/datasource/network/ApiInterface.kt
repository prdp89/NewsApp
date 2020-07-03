package com.android.grab.newsapp.datasource.network

import com.android.grab.newsapp.BuildConfig
import com.android.grab.newsapp.datasource.roomdb.entity.NewsResponse
import retrofit2.http.GET

interface ApiInterface {

    @GET("v2/top-headlines?country=in&apiKey=" + BuildConfig.newsApiKey)
    suspend fun getAllNews(): NewsResponse
}