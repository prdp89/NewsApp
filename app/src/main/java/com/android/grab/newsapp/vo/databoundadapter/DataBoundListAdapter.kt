package com.android.grab.newsapp.vo.databoundadapter

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.grab.newsapp.utils.AppExecutors

abstract class DataBoundListAdapter<T, V : ViewDataBinding>(
    mAppExecutors: AppExecutors,
    mDiffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DataBoundViewHolder<V>>(
    AsyncDifferConfig.Builder<T>(mDiffCallback)
        .setBackgroundThreadExecutor(mAppExecutors.diskIO())
        .build()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<V> {
        val binding = createBinding(parent, viewType)
        return DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup, viewType: Int): V

    override fun onBindViewHolder(holder: DataBoundViewHolder<V>, position: Int) {
        bind(holder.binding, getItem(position), position)
        holder.binding.executePendingBindings()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    protected abstract fun bind(binding: V, item: T, position: Int)
}
