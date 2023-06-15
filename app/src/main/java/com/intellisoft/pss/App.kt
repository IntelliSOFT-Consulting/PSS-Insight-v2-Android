package com.intellisoft.pss

import android.app.Application
import android.content.Intent
import android.util.Log
import com.intellisoft.pss.sync.Dhis2

class App : Application() {

  override fun onCreate() {
    super.onCreate()
    try {
      val serviceIntent = Intent(this, Dhis2::class.java)
      startService(serviceIntent)
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("App","Service Failed to Start.....")
    }
  }
}
