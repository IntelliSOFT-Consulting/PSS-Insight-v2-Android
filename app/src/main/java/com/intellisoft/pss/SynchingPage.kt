package com.intellisoft.pss

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.intellisoft.pss.helper_class.FormatterClass
import com.intellisoft.pss.helper_class.PinLockStatus
import com.intellisoft.pss.navigation_drawer.MainActivity
import com.intellisoft.pss.navigation_drawer.PinActivity
import com.intellisoft.pss.network_request.RetrofitCalls
import kotlin.random.Random
import kotlinx.coroutines.*

class SynchingPage : AppCompatActivity() {

  private val retrofitCalls = RetrofitCalls()
  private val formatterClass = FormatterClass()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_synching_page)
    val score = Random.nextInt(10, 100)
    // Declare a variable for the TextView
    val textView: TextView = findViewById(R.id.loaderTextView)
    // Set the text of the TextView
    textView.text = "$score %"
  }

  override fun onStart() {
    super.onStart()

    retrofitCalls.getDataEntry(this)
    retrofitCalls.getOrganizations(this)
    retrofitCalls.getSubmissions(this)

    CoroutineScope(Dispatchers.IO).launch { test() }
  }
  suspend fun test() {
    coroutineScope {
      launch {
        delay(1000)
        CoroutineScope(Dispatchers.Main).launch {
          val completed = formatterClass.getSharedPref(PinLockStatus.CONFIRMED.name, this@SynchingPage)
          if (completed == null) {
            val intent = Intent(this@SynchingPage, MainActivity::class.java)
            startActivity(intent)
          }else{
            val intent = Intent(this@SynchingPage, PinActivity::class.java)
            startActivity(intent)
          }
          this@SynchingPage.finish()
        }
      }
    }
  }
}
