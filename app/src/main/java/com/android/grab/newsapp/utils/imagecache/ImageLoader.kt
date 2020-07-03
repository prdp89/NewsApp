package com.android.grab.newsapp.utils.imagecache

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.widget.ImageView
import com.android.grab.newsapp.R
import com.android.grab.newsapp.utils.ApplicationUtils.Companion.copyStream
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageLoader(context: Context?) {
    // Initialize MemoryCache
    private val memoryCache = MemoryCache()
    private val fileCache: FileCache = FileCache(context!!)

    //Create Map (collection) to store image and image url in key value pair
    private val imageViews =
        Collections.synchronizedMap(
            WeakHashMap<ImageView, String>()
        )

    private val executorService: ExecutorService = Executors.newFixedThreadPool(5)

    //handler to display images in UI thread
    private val handler = Handler()

    // default image show in list (Before online image download)
    private val stubId = R.drawable.place_holder

    fun displayImage(
        url: String,
        imageView: ImageView
    ) { //Store image and url in Map
        imageViews[imageView] = url
        //Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        val bitmap = memoryCache[url]
        if (bitmap != null) { // if image is stored in MemoryCache Map then
            // Show image in row
            imageView.setImageBitmap(bitmap)
        } else { //queue Photo to download from url
            queuePhoto(url, imageView)
            //Before downloading image show default image
            imageView.setImageResource(stubId)
        }
    }

    private fun queuePhoto(
        url: String,
        imageView: ImageView
    ) { // Store image and url in PhotoToLoad object
        val p = PhotoToLoad(url, imageView)
        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executors to run runnable
        // Submits a PhotosLoader runnable task for execution
        executorService.submit(PhotosLoader(p))
    }

    //Task for the queue
    class PhotoToLoad internal constructor(var url: String, var imageView: ImageView)

    internal inner class PhotosLoader(private var photoToLoad: PhotoToLoad) : Runnable {
        override fun run() {
            try { //Check if image already downloaded
                if (imageViewReused(photoToLoad))
                    return

                // download image from web url
                val bmp = getBitmap(photoToLoad.url)

                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp)

                if (imageViewReused(photoToLoad))
                    return

                // Get bitmap to display
                val bd = BitmapDisplay(bmp, photoToLoad)
                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplay run method will call
                handler.post(bd)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }

    }

    private fun getBitmap(url: String): Bitmap? {
        val f = fileCache.getFile(url)
        val b = decodeFile(f)
        return b
            ?: try {
                var bitmap: Bitmap? = null
                val imageUrl = URL(url)

                val conn =
                    imageUrl.openConnection() as HttpURLConnection
                conn.connectTimeout = 30000
                conn.readTimeout = 30000
                conn.instanceFollowRedirects = true

                val `is` = conn.inputStream

                // Constructs a new FileOutputStream that writes to file
                // if file not exist then it will create file
                val os: OutputStream = FileOutputStream(f)
                // See Utils class CopyStream method
                // It will each pixel from input stream and
                // write pixels to output stream (file)
                copyStream(`is`, os)
                os.close()
                conn.disconnect()

                //Now file created and going to resize file with defined height
                // Decodes image and scales it to reduce memory consumption
                bitmap = decodeFile(f)
                return bitmap
            } catch (ex: Throwable) {
                ex.printStackTrace()

                if (ex is OutOfMemoryError)
                    memoryCache.clear()
                null
            }
    }

    //Decodes image and scales it to reduce memory consumption
    private fun decodeFile(f: File): Bitmap? {
        try { //Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true

            val stream1 = FileInputStream(f)
            BitmapFactory.decodeStream(stream1, null, o)
            stream1.close()

            //Find the correct scale value. It should be the power of 2.
            // Set width/height of recreated image
            val requiredSize = 85
            var widthTmp = o.outWidth
            var heightTmp = o.outHeight
            var scale = 1

            while (true) {
                if (widthTmp / 2 < requiredSize || heightTmp / 2 < requiredSize) break
                widthTmp /= 2
                heightTmp /= 2
                scale *= 2
            }

            //decode with current scale values
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            val stream2 = FileInputStream(f)
            val bitmap = BitmapFactory.decodeStream(stream2, null, o2)
            stream2.close()
            return bitmap
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun imageViewReused(photoToLoad: PhotoToLoad): Boolean {
        val tag = imageViews[photoToLoad.imageView]
        //Check url is already exist in imageViews MAP
        return tag == null || tag != photoToLoad.url
    }

    //Used to display bitmap in the UI thread
    internal inner class BitmapDisplay(
        private var bitmap: Bitmap?,
        private var photoToLoad: PhotoToLoad?
    ) :
        Runnable {
        override fun run() {
            if (imageViewReused(photoToLoad!!))
                return

            // Show bitmap on UI
            if (bitmap != null) photoToLoad?.imageView?.setImageBitmap(bitmap) else photoToLoad?.imageView?.setImageResource(
                stubId
            )
        }
    }

    fun clearCache() { //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear()
        fileCache.clear()
    }
}