package com.awaj.assistant

import android.app.Application
import com.awaj.assistant.di.AppModule

class AwajApplication : Application() {

    lateinit var appModule: AppModule
        private set

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}
