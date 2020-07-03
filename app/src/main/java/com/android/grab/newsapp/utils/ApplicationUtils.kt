package com.android.grab.newsapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class ApplicationUtils {
    companion object {

        //TODO: contain some deprecated Network components.
        fun isNetworkAvailable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo?
            activeNetwork = cm.activeNetworkInfo
            return null != activeNetwork && activeNetwork.isConnected
        }

        fun showSnackBar(v: View?, snackBarText: String?) {
            if (v == null || snackBarText == null) {
                return
            }
            Snackbar.make(v, snackBarText, Snackbar.LENGTH_LONG).show()
        }

        fun isNight(): Boolean {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            return currentHour <= 7 || currentHour >= 18
        }

        fun copyStream(`is`: InputStream, os: OutputStream) {
            val bufferSize = 1024
            try {
                val bytes = ByteArray(bufferSize)

                while (true) {
                    //Read byte from input stream
                    val count: Int = `is`.read(bytes, 0, bufferSize)
                    if (count == -1) break
                    //Write byte from output stream
                    os.write(bytes, 0, count)
                }
            } catch (ex: Exception) {
            }
        }
    }
}