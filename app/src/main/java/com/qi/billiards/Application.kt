package com.qi.billiards

import android.app.Application
import android.content.ContextWrapper

lateinit var instance: Application

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}

object AppContext : ContextWrapper(instance)