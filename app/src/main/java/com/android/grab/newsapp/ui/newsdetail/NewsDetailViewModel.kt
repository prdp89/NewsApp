package com.android.grab.newsapp.ui.newsdetail

import androidx.lifecycle.*
import com.android.grab.newsapp.datasource.repository.HomeRepository
import com.android.grab.newsapp.datasource.roomdb.entity.NewsRowEntity
import com.android.grab.newsapp.utils.Resource
import javax.inject.Inject

class NewsDetailViewModel @Inject constructor(private val mHomeRepository: HomeRepository) :
    ViewModel() {

    private val data = MutableLiveData<Int>()

    val newsLiveData: LiveData<Resource<NewsRowEntity>> = data.switchMap {
        mHomeRepository.getNewsDetail(it)
    }

    fun triggerLiveData(newsId: Int) {
        data.value = newsId
    }
}
