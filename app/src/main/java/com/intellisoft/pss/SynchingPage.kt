package com.intellisoft.pss

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.intellisoft.pss.navigation_drawer.MainActivity
import com.intellisoft.pss.network_request.RetrofitCalls
import kotlinx.coroutines.*

class SynchingPage : AppCompatActivity() {

    private val retrofitCalls = RetrofitCalls()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_synching_page)


    }

    override fun onStart() {
        super.onStart()

        retrofitCalls.getDataEntry(this)

        CoroutineScope(Dispatchers.IO).launch {
            test()
        }

    }
    suspend fun test(){
        coroutineScope {
            launch {
                delay(1000)
                CoroutineScope(Dispatchers.Main).launch {
                    val intent = Intent(this@SynchingPage, MainActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }


}