package com.intellisoft.pss.network_request

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.widget.Toast
import com.intellisoft.pss.DbSaveDataEntry
import com.intellisoft.pss.helper_class.FormatterClass
import com.intellisoft.pss.UrlData
import com.intellisoft.pss.room.Converters
import com.intellisoft.pss.room.IndicatorsData
import com.intellisoft.pss.room.PssViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RetrofitCalls {

    fun submitData(context: Context, dbSaveDataEntry: DbSaveDataEntry) {
        CoroutineScope(Dispatchers.Main).launch {
            val job = Job()
            CoroutineScope(Dispatchers.IO + job).launch {
                submitDataBackground(context, dbSaveDataEntry)
            }.join()
        }

    }
    private suspend fun submitDataBackground(context: Context, dbSaveDataEntry: DbSaveDataEntry) {

        val job1 = Job()
        CoroutineScope(Dispatchers.Main + job1).launch {
            var progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait..")
            progressDialog.setMessage("Posting data in progress..")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            var messageToast = ""
            val job = Job()
            CoroutineScope(Dispatchers.IO + job).launch {
                val formatterClass = FormatterClass()
                val baseUrl = formatterClass.getSharedPref("serverUrl", context)

                if (baseUrl != null){
                    val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
                    try {
                        val apiInterface = apiService.submitData(dbSaveDataEntry)
                        messageToast = if (apiInterface.isSuccessful){
                            val statusCode = apiInterface.code()
                            val body = apiInterface.body()
                            if (statusCode == 200 || statusCode == 201){
                                "Saved successfully"
                            }else{
                                "Error: Body is null"
                            }

                        }else{
                            "Error: The request was not successful"
                        }


                    }catch (e: Exception){
                        messageToast = "There was an issue with the server"
                    }
                }



            }.join()
            CoroutineScope(Dispatchers.Main).launch{
                progressDialog.dismiss()
                Toast.makeText(context, messageToast, Toast.LENGTH_LONG).show()
            }


        }

    }


    fun getDataEntry(context: Context){
        CoroutineScope(Dispatchers.IO).launch {

            val myViewModel = PssViewModel(context.applicationContext as Application)


            val formatterClass = FormatterClass()
            val baseUrl = formatterClass.getSharedPref("serverUrl", context)
            val username = formatterClass.getSharedPref("username", context)
            if (baseUrl != null && username !=null){
                val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
                try {
                    val apiInterface = apiService.getDataEntry()
                    if (apiInterface.isSuccessful) {
                        val statusCode = apiInterface.code()
                        val body = apiInterface.body()
                        if (statusCode == 200 || statusCode == 201){
                            if (body != null){

                                val converters = Converters().toJson(body)
                                val indicatorsData = IndicatorsData(converters,username)
                                myViewModel.addIndicators(indicatorsData)

//                                val count = body.count
//                                val details = body.details
//                                if (details.isNotEmpty()){
//                                    details.forEach {
//                                        val categoryName = it.categoryName
//                                        val indicators = it.indicators
//                                        if (indicators.isNotEmpty()){
//                                            indicators.forEach { ind ->
//                                                val indicatorCode = ind.indicatorCode
//                                                val indicatorId = ind.indicatorId
//                                                val indicatorName = ind.indicatorName
//                                                val indicatorsList = ind.indicators
//                                                if (indicatorsList.isNotEmpty()){
//                                                    indicatorsList.forEach {indList ->
//                                                        val code = indList.code
//                                                        val name = indList.name
//                                                        val id = indList.id
//
//                                                        Log.e("----", code)
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
                            }
                        }
                    }
                }catch(e: Exception){
                    print(e)
                }
            }


        }
    }
}