package com.umer.networklogger

import android.app.Application
import umer.sheraz.shakelibrary.ShakeLibrary

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        ShakeLibrary.initialize(this) // Start shake detection globally
    }
}