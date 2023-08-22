package com.qi.billiards

import android.app.Application
import android.content.ContextWrapper
import com.qi.billiards.data.AppData

lateinit var instance: Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this

        AppData.initFromSp()
    }
}

object AppContext : ContextWrapper(instance)