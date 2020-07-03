package com.android.grab.newsapp

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.android.grab.newsapp.di.DaggerAppComponent
import com.android.grab.newsapp.utils.ApplicationUtils.Companion.isNight
import com.android.grab.newsapp.utils.PreferenceUtil
import com.android.grab.newsapp.vo.AppConstants
import com.facebook.stetho.Stetho
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import java.io.PrintWriter
import java.io.StringWriter

class NewsApplication : DaggerApplication() {

    companion object {

        private var mContext: Context? = null

        fun getContext(): Context? {
            return mContext
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()

        //used to monitor DB
        Stetho.initializeWithDefaults(this)

        mContext = applicationContext

        val checkNight = isNight()
        val mode = if (checkNight) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }

        PreferenceUtil[getContext()!!, AppConstants.SharedPreferenceConstants.KEY_IS_DARK_MODE] =
            checkNight

        AppCompatDelegate.setDefaultNightMode(mode)

        Thread.setDefaultUncaughtExceptionHandler { _, exception ->
            val sw = StringWriter()

            exception.printStackTrace(PrintWriter(sw))
            val exceptionAsString = sw.toString()

            Log.e("  ---->  %s", exceptionAsString)
            Log.e("uncaughtException", ": Exception ENDS")

            //TODO: log runtime exception to remote API or Save in local directory
        }
    }
}