package com.intellisoft.pss.network_request

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.intellisoft.pss.helper_class.DbSaveDataEntry
import com.intellisoft.pss.helper_class.FormatterClass
import com.intellisoft.pss.room.Converters
import com.intellisoft.pss.room.IndicatorsData
import com.intellisoft.pss.room.Organizations
import com.intellisoft.pss.room.PssViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RetrofitCalls {

  fun submitData(context: Context, dbSaveDataEntry: DbSaveDataEntry) {
    CoroutineScope(Dispatchers.Main).launch {
      val job = Job()
      CoroutineScope(Dispatchers.IO + job)
          .launch { submitDataBackground(context, dbSaveDataEntry) }
          .join()
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
      Log.e("Submit","Submitted Report $dbSaveDataEntry")

      var messageToast = ""
      val job = Job()
      CoroutineScope(Dispatchers.IO + job)
          .launch {
            val formatterClass = FormatterClass()
            val baseUrl = formatterClass.getSharedPref("serverUrl", context)

            if (baseUrl != null) {
              val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
              try {
                val apiInterface = apiService.submitData(dbSaveDataEntry)
                messageToast =
                    if (apiInterface.isSuccessful) {
                      val statusCode = apiInterface.code()
                      val body = apiInterface.body()
                      if (statusCode == 200 || statusCode == 201) {
                        "Saved and synced successfully"
                      } else {
                        "Error: Body is null"
                      }
                    } else {
                      "Error: The request was not successful"
                    }
              } catch (e: Exception) {
                messageToast = "There was an issue with the server"
              }
            }
          }
          .join()
      CoroutineScope(Dispatchers.Main).launch {
        progressDialog.dismiss()
        Toast.makeText(context, messageToast, Toast.LENGTH_LONG).show()
      }
    }
  }

  fun getDataEntry(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
      val myViewModel = PssViewModel(context.applicationContext as Application)

      val formatterClass = FormatterClass()
      val baseUrl = formatterClass.getSharedPref("serverUrl", context)
      val username = formatterClass.getSharedPref("username", context)
      if (baseUrl != null && username != null) {
        val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
        try {
          val apiInterface = apiService.getDataEntry()
          if (apiInterface.isSuccessful) {
            val statusCode = apiInterface.code()
            val body = apiInterface.body()
            if (statusCode == 200 || statusCode == 201) {
              if (body != null) {

                val converters = Converters().toJson(body)
                val indicatorsData = IndicatorsData(converters, username)
                myViewModel.addIndicators(indicatorsData)
              }
            }
          }
        } catch (e: Exception) {
          print(e)
        }
      }
    }
  }
  fun getOrganizations(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
      val myViewModel = PssViewModel(context.applicationContext as Application)

      val formatterClass = FormatterClass()
      val baseUrl = formatterClass.getSharedPref("serverUrl", context)
      val username = formatterClass.getSharedPref("username", context)
      if (baseUrl != null && username != null) {
        val apiService = RetrofitBuilder.getRetrofit(baseUrl).create(Interface::class.java)
        try {
          val apiInterface = apiService.getOrganizations()
          if (apiInterface.isSuccessful) {
            val statusCode = apiInterface.code()
            val body = apiInterface.body()
            if (statusCode == 200 || statusCode == 201) {
              if (body != null) {

                val converters = Converters().toJsonOrganization(body)
                try {
                  val json = Gson().fromJson(converters, JsonObject::class.java)
                  val jsonArray = json.getAsJsonArray("details")

                  for (i in 0 until jsonArray.size()) {
                    val jsonObject = jsonArray.get(i).asJsonObject
                    val id=jsonObject.get("id").asString
                    val displayName=jsonObject.get("displayName").asString
                    val organizationData = Organizations(idcode = id,displayName=displayName)
                    myViewModel.addOrganizations(organizationData)
                  }
                }catch (e:Exception){
                  e.printStackTrace()
                }
              }
            }
          }
        } catch (e: Exception) {
          print(e)
        }
      }
    }
  }
}
