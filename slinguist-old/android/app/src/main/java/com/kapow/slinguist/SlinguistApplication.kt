package com.kapow.slinguist

import android.app.Application
import com.kapow.slinguist.data.AppContainer

class SlinguistApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // Constructing the container verifies the Room configuration at application start.
        appContainer = AppContainer(this)
    }
}
