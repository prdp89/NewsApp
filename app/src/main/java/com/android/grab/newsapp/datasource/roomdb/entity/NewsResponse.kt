package com.android.grab.newsapp.datasource.roomdb.entity

data class NewsResponse(
    var articles: List<NewsRowEntity>,

    var status: String
)