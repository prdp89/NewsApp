package com.android.grab.newsapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences.Editor
import androidx.preference.PreferenceManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * Shared preferences configuration utils
 */
@SuppressLint("CommitPrefEdits")
class PreferenceUtil
/**
 * Don't let anyone instantiate this class.
 */
private constructor() {

    init {
        throw Error("Do not need instantiate!")
    }

    companion object {

        private val APPLY_METHOD = findApplyMethod()

        fun contains(context: Context, resId: Int): Boolean {
            return contains(context, context.getString(resId))
        }

        fun contains(context: Context, key: String): Boolean {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            return prefs.contains(key)
        }

        fun remove(context: Context, key: String) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.remove(key)
            commitOrApply(editor)
        }

        operator fun set(context: Context, resId: Int, value: Boolean) {
            set(context, context.getString(resId), value)
        }

        operator fun set(context: Context, key: String, value: Boolean) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putBoolean(key, value)
            commitOrApply(editor)
        }

        operator fun set(context: Context, resId: Int, value: Float) {
            set(context, context.getString(resId), value)
        }

        operator fun set(context: Context, key: String, value: Float) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putFloat(key, value)
            commitOrApply(editor)
        }

        operator fun set(context: Context, resId: Int, value: Int) {
            set(context, context.getString(resId), value)
        }

        operator fun set(context: Context, key: String, value: Int) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putInt(key, value)
            commitOrApply(editor)
        }

        operator fun set(context: Context, key: String, value: Set<String>) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putStringSet(key, value)
            commitOrApply(editor)
        }

        operator fun set(context: Context, resId: Int, value: Long) {
            set(context, context.getString(resId), value)
        }

        operator fun set(context: Context, key: String, value: Long) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putLong(key, value)
            commitOrApply(editor)
        }

        operator fun set(context: Context, resId: Int, value: String) {
            set(context, context.getString(resId), value)
        }

        operator fun set(context: Context, key: String, value: String) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            editor.putString(key, value)
            commitOrApply(editor)
        }

        operator fun set(context: Context, valueMap: HashMap<String, Int>) {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            val editor = prefs.edit()
            for ((key, value) in valueMap)
                editor.putInt(key, value)
            commitOrApply(editor)
        }

        operator fun get(context: Context, resId: Int, defValue: Boolean): Boolean {
            return get(context, context.getString(resId), defValue)
        }

        operator fun get(context: Context, key: String, defValue: Boolean): Boolean {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            return prefs.getBoolean(key, defValue)
        }

        operator fun get(context: Context, resId: Int, defValue: Float): Float {
            return get(context, context.getString(resId), defValue)
        }

        operator fun get(context: Context, key: String, defValue: Float): Float {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            return prefs.getFloat(key, defValue)
        }

        operator fun get(context: Context, resId: Int, defValue: Int): Int {
            return get(context, context.getString(resId), defValue)
        }

        operator fun get(context: Context, key: String, defValue: Int): Int {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            return prefs.getInt(key, defValue)
        }

        operator fun get(context: Context, resId: Int, defValue: Long): Long {
            return get(context, context.getString(resId), defValue)
        }

        operator fun get(context: Context, key: String, defValue: Long): Long {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            return prefs.getLong(key, defValue)
        }

        operator fun get(context: Context, resId: Int, defValue: String): String? {
            return get(context, context.getString(resId), defValue)
        }

        operator fun get(context: Context, key: String, defValue: String): String? {
            val prefs = PreferenceManager
                .getDefaultSharedPreferences(context)
            return prefs.getString(key, defValue)
        }

        private fun findApplyMethod(): Method? {
            return try {
                val cls = Editor::class.java
                cls.getMethod("apply")
            } catch (unused: NoSuchMethodException) {
                null
            }

        }

        private fun commitOrApply(editor: Editor) {
            if (APPLY_METHOD != null) {
                try {
                    APPLY_METHOD.invoke(editor)
                    return
                } catch (e: InvocationTargetException) {
                } catch (e: IllegalAccessException) {
                }

            }

            editor.commit()
        }
    }
}
