package com.qi.billiards.util

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.qi.billiards.AppContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

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

const val DAY = 24 * 60 * 60 * 1000L // 1天有多少ms
const val HOUR = 60 * 60 * 1000L // 1小时有多少ms
const val MINUTE = 60 * 1000L // 1分钟有多少ms
const val SECOND = 1000L // 1秒有多少ms

fun Long.toStandardTime(ignoreMS: Boolean = true): String {
    if (this > DAY) {
        return "${this / DAY}天${(this % DAY).toStandardTime(ignoreMS)}"
    }
    if (this > HOUR) {
        return "${this / HOUR}时${(this % HOUR).toStandardTime(ignoreMS)}"
    }
    if (this > MINUTE) {
        return "${this / MINUTE}分${(this % MINUTE).toStandardTime(ignoreMS)}"
    }
    if (this > SECOND) {
        return "${this / SECOND}秒${(this % SECOND).toStandardTime(ignoreMS)}"
    }
    if (ignoreMS) {
        return ""
    }
    return "${this}毫秒"
}

fun Fragment.toast(text: String) {
    if (!isAdded || isDetached) {
        return
    }
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun <T> Continuation<T>.safeResume(value: T): Unit =
    try {
        resume(value)
    } catch (_: Throwable) {

    }

inline fun <reified T> T.toJson(): String = Gson().toJson(this)

inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, T::class.java)

fun Fragment.hideSystemKeyboard(et: EditText) {
    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(et.windowToken, 0)
}