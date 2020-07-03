package com.android.grab.newsapp.ui.main

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.android.grab.newsapp.R
import com.android.grab.newsapp.databinding.ItemNewsFeedBinding
import com.android.grab.newsapp.datasource.roomdb.entity.NewsRowEntity
import com.android.grab.newsapp.utils.AppExecutors
import com.android.grab.newsapp.utils.imagecache.ImageLoader
import com.android.grab.newsapp.vo.databoundadapter.DataBoundListAdapter

class NewsFeedAdapter(
    appExecutors: AppExecutors,
    private val imageLoader: ImageLoader,
    private val callback: ((ItemNewsFeedBinding) -> Unit)?
) :
    DataBoundListAdapter<NewsRowEntity,
            ItemNewsFeedBinding>(mAppExecutors = appExecutors,
        mDiffCallback = object : DiffUtil.ItemCallback<NewsRowEntity>() {
            override fun areItemsTheSame(
                oldItem: NewsRowEntity,
                newItem: NewsRowEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: NewsRowEntity,
                newItem: NewsRowEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ItemNewsFeedBinding {
        val itemBinding = DataBindingUtil.inflate<ItemNewsFeedBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_news_feed,
            parent,
            false
        )

        itemBinding.root.setOnClickListener {
            callback?.invoke(itemBinding)
        }

        return itemBinding
    }

    @SuppressLint("SetTextI18n")
    override fun bind(
        binding: ItemNewsFeedBinding,
        item: NewsRowEntity,
        position: Int
    ) {
        val author =
            if (TextUtils.isEmpty(item.author)) "" else item.author
        binding.tvNewsTitle.text =
            item.source?.name + " : " + author

        binding.entity = item

        imageLoader.displayImage(item.urlToImage!!, binding.ivPerson)
    }
}