package com.android.grab.newsapp.utils.imagecache

import android.graphics.Bitmap
import android.util.Log
import java.util.*

class MemoryCache {
    //Last argument true for LRU ordering
    private val cache =
        Collections.synchronizedMap(
            LinkedHashMap<String, Bitmap?>(10, 1.5f, true)
        )

    //current allocated size
    private var size: Long = 0

    //max memory cache folder used to download images in bytes
    private var limit: Long = 1000000

    private fun setLimit(new_limit: Long) {
        limit = new_limit
        Log.i(
            TAG,
            "MemoryCache will use up to " + limit / 1024.0 / 1024.0 + "MB"
        )
    }

    operator fun get(id: String?): Bitmap? {
        return try {
            if (!cache.containsKey(id)) null else cache[id]
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
            null
        }
    }

    fun put(id: String, bitmap: Bitmap?) {
        try {
            if (cache.containsKey(id)) size -= getSizeInBytes(cache[id])
            cache[id] = bitmap
            size += getSizeInBytes(bitmap)
            checkSize()
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }

    private fun checkSize() {
        Log.i(
            TAG,
            "cache size=" + size + " length=" + cache.size
        )
        if (size > limit) { //least recently accessed item will be the first one iterated
            val iterator: MutableIterator<Map.Entry<String, Bitmap?>> =
                cache.entries.iterator()

            while (iterator.hasNext()) {
                val entry = iterator.next()
                size -= getSizeInBytes(entry.value)
                iterator.remove()
                if (size <= limit) break
            }

            Log.i(
                TAG,
                "Clean cache. New size " + cache.size
            )
        }
    }

    fun clear() {
        try { // Clear cache
            cache.clear()
            size = 0
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }

    private fun getSizeInBytes(bitmap: Bitmap?): Long {
        return if (bitmap == null) 0 else (bitmap.rowBytes * bitmap.height).toLong()
    }

    companion object {
        private const val TAG = "MemoryCache"
    }

    init { //use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4)
    }
}