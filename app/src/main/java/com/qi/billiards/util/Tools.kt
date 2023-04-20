package com.qi.billiards.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.qi.billiards.AppContext
import java.text.SimpleDateFormat
import java.util.*

fun String.safeToInt(): Int {
    return try {
        toInt()
    } catch (t: Throwable) {
        0
    }
}

fun getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    val windowManager = AppContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    val windowManager = AppContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

val Int.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

fun Date?.format(pattern: String = "HH:mm:ss"): String {
    if (this == null) {
        return "?"
    }
    return SimpleDateFormat(pattern, Locale.CHINA).format(this)
}

fun Fragment.toast(text: String) {
    if (!isAdded || isDetached) {
        return
    }
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

const val SP_NAME = "my_preferences"

fun save(key: String, value: Any) {
    val sharedPreferences = AppContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val json = Gson().toJson(value)
    editor.putString(key, json)
    editor.apply()
}

inline fun <reified T> get(key: String): T? {
    val sharedPreferences = AppContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    val json = sharedPreferences.getString(key, null)
    return if (json != null) {
        Gson().fromJson(json, T::class.java)
    } else {
        null
    }
}