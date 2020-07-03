package com.android.grab.newsapp.datasource.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.android.grab.newsapp.datasource.Database
import com.android.grab.newsapp.datasource.network.ApiInterface
import com.android.grab.newsapp.datasource.roomdb.entity.NewsRowEntity
import com.android.grab.newsapp.utils.ApplicationUtils
import com.android.grab.newsapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val mApiInterface: ApiInterface,
    private val mContext: Context,
    private val mAppDatabase: Database
) {

    fun getAllNews(isRefresh: Boolean) =
        liveData<Resource<List<NewsRowEntity>>>(Dispatchers.IO) {

            val mutableLiveData = MutableLiveData<Resource<List<NewsRowEntity>>>()

            emit(Resource.loading(""))

            val newsData = mAppDatabase.trendingRepoDao().loadNewsData()
            if (newsData.isNotEmpty() && !isRefresh) {
                mutableLiveData.postValue(
                    Resource.success(
                        "", newsData
                    )
                )
                emitSource(mutableLiveData)
                return@liveData
            }

            if (ApplicationUtils.isNetworkAvailable(mContext)) {
                val response = mApiInterface.getAllNews()

                mAppDatabase.trendingRepoDao().insertNewsDataList(response.articles)
                mutableLiveData.postValue(Resource.success("", response.articles))

                emitSource(mutableLiveData)
            } else
                emit(Resource.error(""))

        }

    fun getNewsDetail(newsId: Int) =
        liveData<Resource<NewsRowEntity>>(Dispatchers.IO) {

            val mutableLiveData = MutableLiveData<Resource<NewsRowEntity>>()

            emit(Resource.loading(""))

            val newsData = mAppDatabase.trendingRepoDao().loadNewsDetail(newsId)
            mutableLiveData.postValue(
                Resource.success(
                    "", newsData
                )
            )
            emitSource(mutableLiveData)
        }

    fun clearNewsFeed() = liveData(Dispatchers.IO) {
        emit(mAppDatabase.trendingRepoDao().deleteNewsData())
    }
}