package com.intellisoft.pss

import android.app.Application
import com.intellisoft.pss.sync.SyncScheduler

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    val syncScheduler = SyncScheduler(this)
    syncScheduler.schedule() // Schedule the job to run every 5 minutes
  }
}
