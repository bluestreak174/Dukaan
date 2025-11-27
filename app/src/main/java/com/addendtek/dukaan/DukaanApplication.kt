package com.addendtek.dukaan

import android.app.Application
import com.addendtek.dukaan.database.AppContainer
import com.addendtek.dukaan.database.AppDataContainer

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
