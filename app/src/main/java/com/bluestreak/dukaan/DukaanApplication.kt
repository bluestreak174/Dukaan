package com.bluestreak.dukaan

import android.app.Application
import com.bluestreak.dukaan.database.AppContainer
import com.bluestreak.dukaan.database.AppDataContainer

class DukaanApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
