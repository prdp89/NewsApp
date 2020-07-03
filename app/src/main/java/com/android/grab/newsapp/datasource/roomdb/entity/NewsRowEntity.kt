package com.android.grab.newsapp.datasource.roomdb.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_desc")
data class NewsRowEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var description: String?,
    var content: String?,
    var url: String?,
    var urlToImage: String?,
    var title: String?,
    var author: String?,

    @Embedded(prefix = "source_")
    var source: Source?
) {
    data class Source(var name: String)
}