package com.umer.networklogger

import android.app.Application
import umer.sheraz.shakelibrary.NetworkLogger

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkLogger.initialize(this) // Initialize it in the Application class
    }
}