package com.qi.billiards.util

import android.content.Context
import com.qi.billiards.AppContext

val sp by lazy { AppContext.getSharedPreferences("SP_FILE", Context.MODE_PRIVATE) }

inline fun <reified T> save(key: String, value: T) {
    when (value) {
        is Int -> sp.edit().putInt(key, value).apply()
        is Long -> sp.edit().putLong(key, value).apply()
        is Float -> sp.edit().putFloat(key, value).apply()
        is Boolean -> sp.edit().putBoolean(key, value).apply()
        is String -> sp.edit().putString(key, value).apply()
        else -> throw UnsupportedOperationException("不支持的类型")
    }

}

inline fun <reified T> get(key: String, default: T): T? {
    return when (default) {
        is Int -> sp.getInt(key, default)
        is Long -> sp.getLong(key, default)
        is Float -> sp.getFloat(key, default)
        is Boolean -> sp.getBoolean(key, default)
        is String -> sp.getString(key, default)
        else -> throw UnsupportedOperationException("不支持的类型")
    } as? T
}