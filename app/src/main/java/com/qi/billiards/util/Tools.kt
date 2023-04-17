package com.qi.billiards.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

fun String.safeToInt(): Int {
    return try {
        toInt()
    } catch (t: Throwable) {
        0
    }
}

fun Context.getScreenWidth(): Int {
    val displayMetrics = DisplayMetrics()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun Context.getScreenHeight(): Int {
    val displayMetrics = DisplayMetrics()
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}

fun Context.dp2Px(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        .toInt()
}

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