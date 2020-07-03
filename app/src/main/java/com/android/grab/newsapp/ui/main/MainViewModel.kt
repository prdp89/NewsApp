package com.android.grab.newsapp.ui.main

import androidx.lifecycle.*
import com.android.grab.newsapp.datasource.repository.HomeRepository
import com.android.grab.newsapp.datasource.roomdb.entity.NewsRowEntity
import com.android.grab.newsapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class MainViewModel @Inject constructor(private val mHomeRepository: HomeRepository) : ViewModel() {

    private val data = MutableLiveData<Boolean>()

    val newsLiveData: LiveData<Resource<List<NewsRowEntity>>> = data.switchMap {
        mHomeRepository.getAllNews(it)
    }

    fun refreshLiveData() = liveData(Dispatchers.IO) {
        emitSource(mHomeRepository.clearNewsFeed())
    }

    fun triggerLiveData(isRefresh: Boolean) {
        data.value = isRefresh
    }
}
