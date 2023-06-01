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
import com.intellisoft.pss.helper_class.ImageResponse
import com.intellisoft.pss.helper_class.SubmissionsStatus
import com.intellisoft.pss.room.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class RetrofitCalls {

  fun submitData(context: Context, dbSaveDataEntry: DbSaveDataEntry) {
    CoroutineScope(Dispatchers.Main).launch {
      val job = Job()
      CoroutineScope(Dispatchers.IO + job)
          .launch { submitDataBackground(context, dbSaveDataEntry) }
          .join()
    }
  }
  fun submitSyncData(
      context: Context,
      dbSaveDataEntry: DbSaveDataEntry,
      sm: Submissions,
      myViewModel: PssViewModel
  ) {

    CoroutineScope(Dispatchers.Main).launch {
      val job = Job()

      CoroutineScope(Dispatchers.IO + job)
          .launch { submitSyncDataBackground(context, dbSaveDataEntry, sm, myViewModel) }
          .join()
    }
  }
  fun submitFileData(context: Context, image: Image, myViewModel: PssViewModel) {

    CoroutineScope(Dispatchers.Main).launch {
      val job = Job()

      CoroutineScope(Dispatchers.IO + job)
          .launch { submitFileDataBackground(context, myViewModel, image) }
          .join()
    }
  }
  private suspend fun submitFileDataBackground(
      context: Context,
      myViewModel: PssViewModel,
      image: Image
  ) {

    val job1 = Job()
    CoroutineScope(Dispatchers.Main + job1).launch {
      val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), image.image)
      val file = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
      var messageToast = ""
      val job = Job()
      CoroutineScope(Dispatchers.IO + job)
          .launch {
            val formatterClass = FormatterClass()
            val baseUrl = formatterClass.getSharedPref("serverUrl", context)

            if (baseUrl != null) {
              val apiService =
                  RetrofitBuilder.getRetrofit(context, baseUrl).create(Interface::class.java)
              try {
                val apiInterface = apiService.uploadImageFileData(file)
                messageToast =
                    if (apiInterface.isSuccessful) {
                      val statusCode = apiInterface.code()
                      val body = apiInterface.body()
                      if (statusCode == 200 || statusCode == 201) {
                        updateFileStatus(
                            context, body, myViewModel, image, "Saved and synced successfully")
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

        //        Toast.makeText(context, messageToast, Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun updateFileStatus(
      context: Context,
      body: ImageResponse?,
      myViewModel: PssViewModel,
      image: Image,
      s: String
  ): String {
    if (body != null) {
      myViewModel.updateImageLink(context, image, body.id)
    }
    return s
  }

  private suspend fun submitDataBackground(context: Context, dbSaveDataEntry: DbSaveDataEntry) {

    val job1 = Job()
    CoroutineScope(Dispatchers.Main + job1).launch {
      var progressDialog = ProgressDialog(context)
      progressDialog.setTitle("Please wait..")
      progressDialog.setMessage("Posting data in progress..")
      progressDialog.setCanceledOnTouchOutside(false)
      progressDialog.show()
      Log.e("Submit", "Submitted Report $dbSaveDataEntry")

      var messageToast = ""
      val job = Job()
      CoroutineScope(Dispatchers.IO + job)
          .launch {
            val formatterClass = FormatterClass()
            val baseUrl = formatterClass.getSharedPref("serverUrl", context)

            if (baseUrl != null) {
              val apiService =
                  RetrofitBuilder.getRetrofit(context, baseUrl).create(Interface::class.java)
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
  private suspend fun submitSyncDataBackground(
      context: Context,
      dbSaveDataEntry: DbSaveDataEntry,
      sm: Submissions,
      myViewModel: PssViewModel
  ) {

    val job1 = Job()
    CoroutineScope(Dispatchers.Main + job1).launch {
      var messageToast = ""
      val job = Job()
      CoroutineScope(Dispatchers.IO + job)
          .launch {
            val formatterClass = FormatterClass()
            val baseUrl = formatterClass.getSharedPref("serverUrl", context)

            if (baseUrl != null) {
              val apiService =
                  RetrofitBuilder.getRetrofit(context, baseUrl).create(Interface::class.java)
              try {
                val apiInterface =
                    if (sm.serverId.isNotEmpty()) {
                      apiService.reSubmitData(sm.serverId, dbSaveDataEntry)
                    } else {
                      apiService.submitData(dbSaveDataEntry)
                    }

                messageToast =
                    if (apiInterface.isSuccessful) {
                      val statusCode = apiInterface.code()
                      val body = apiInterface.body()
                      if (statusCode == 200 || statusCode == 201) {
                        updateSubmission(context, sm, "Saved and synced successfully", myViewModel)
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

        //        Toast.makeText(context, messageToast, Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun updateSubmission(
      context: Context,
      sm: Submissions,
      message: String,
      viewModel: PssViewModel
  ): String {
    viewModel.markSynced(context, sm.id.toString())
    return message
  }

  fun getDataEntry(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
      val myViewModel = PssViewModel(context.applicationContext as Application)

      val formatterClass = FormatterClass()
      val baseUrl = formatterClass.getSharedPref("serverUrl", context)
      val username = formatterClass.getSharedPref("username", context)
      if (baseUrl != null && username != null) {
        val apiService = RetrofitBuilder.getRetrofit(context, baseUrl).create(Interface::class.java)
        try {
          val apiInterface = apiService.getDataEntry()
          if (apiInterface.isSuccessful) {
            val statusCode = apiInterface.code()
            val body = apiInterface.body()
            if (statusCode == 200 || statusCode == 201) {
              if (body != null) {
                myViewModel.clearIndicators()
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
      val baseUrl = formatterClass.getSharedPref("serverUrl1", context)
      val username = formatterClass.getSharedPref("username", context)
      if (baseUrl != null && username != null) {
        val apiService = RetrofitBuilder.getRetrofit(context, baseUrl).create(Interface::class.java)
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
                  val jsonArray = json.getAsJsonArray("organisationUnits")
                  val surname = json.get("surname").asString
                  val username1 = json.get("username").asString
                  val id = json.get("id").asString
                  val firstName = json.get("firstName").asString

                  formatterClass.saveSharedPref("userId", id, context)
                  formatterClass.saveSharedPref("userSurname", surname, context)
                  formatterClass.saveSharedPref("userUsername", username1, context)
                  formatterClass.saveSharedPref("userFirstName", firstName, context)
                  myViewModel.deleteAllOrganizations()
                  for (i in 0 until jsonArray.size()) {
                    val jsonObject = jsonArray.get(i).asJsonObject
                    val id = jsonObject.get("id").asString
                    val displayName = jsonObject.get("name").asString
                    val organizationData = Organizations(idcode = id, displayName = displayName)
                    myViewModel.addOrganizations(organizationData)
                  }
                } catch (e: Exception) {
                  e.printStackTrace()
                  Log.e("TAG", "json:::: ${e.message}")
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
  private fun loadSpecificResponses(context: Context, id: String) {
    CoroutineScope(Dispatchers.IO).launch {
      val myViewModel = PssViewModel(context.applicationContext as Application)

      val formatterClass = FormatterClass()
      val baseUrl = formatterClass.getSharedPref("serverUrl", context)

      val username = formatterClass.getSharedPref("username", context)
      if (baseUrl != null && username != null) {
        val apiService = RetrofitBuilder.getRetrofit(context, baseUrl).create(Interface::class.java)
        try {
          val apiInterface = apiService.getResponseDetails(id)
          if (apiInterface.isSuccessful) {
            val statusCode = apiInterface.code()
            val body = apiInterface.body()
            if (statusCode == 200 || statusCode == 201) {
              if (body != null) {

                val converters = Converters().toJsonResponseDetails(body)
                try {
                  val json = Gson().fromJson(converters, JsonObject::class.java)
                  val indicatorsObject = json.getAsJsonObject("indicators")
                  myViewModel.updateOriginalResponses(context, id, indicatorsObject.toString())
                } catch (e: Exception) {
                  e.printStackTrace()
                  Log.e("TAG", "json:::: ${e.message}")
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

  private fun formatDateInput(inputDate: String?): String {
    val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val outputFormat = "yyyy-MM-dd"
    var date = ""
    val dateFormat = SimpleDateFormat(inputFormat, Locale.ENGLISH)
    dateFormat.timeZone = TimeZone.getTimeZone("UTC") // Assuming the input date is in UTC timezone

    try {
      val parsedDate = dateFormat.parse(inputDate)
      val outputDateFormat = SimpleDateFormat(outputFormat, Locale.US)
      val formattedDate = outputDateFormat.format(parsedDate)

      println(formattedDate) // Output: "2023-5-25"
      date = formattedDate
    } catch (e: Exception) {
      date = "$inputDate"
      println("Error occurred while parsing or formatting the date")
    }
    return date
  }

  fun getSubmissions(context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
      val myViewModel = PssViewModel(context.applicationContext as Application)

      val formatterClass = FormatterClass()
      val baseUrl = formatterClass.getSharedPref("serverUrl", context)

      val username = formatterClass.getSharedPref("username", context)
      if (baseUrl != null && username != null) {
        val apiService = RetrofitBuilder.getRetrofit(context, baseUrl).create(Interface::class.java)
        try {
          val apiInterface = apiService.getResponses(username)
          if (apiInterface.isSuccessful) {
            val statusCode = apiInterface.code()
            val body = apiInterface.body()
            if (statusCode == 200 || statusCode == 201) {
              if (body != null) {

                val converters = Converters().toJsonResponses(body)
                try {
                  val json = Gson().fromJson(converters, JsonObject::class.java)

                  val jsonArray = json.getAsJsonArray("details")
                  //                  myViewModel.deleteAllOrganizations()
                  for (i in 0 until jsonArray.size()) {
                    val jsonObject = jsonArray.get(i).asJsonObject
                    val id = jsonObject.get("id").asString
                    val status = jsonObject.get("status").asString
                    val dataEntryDate = jsonObject.get("dataEntryDate").asString
                    val selectedPeriod = jsonObject.get("selectedPeriod").asString
                    if (status.isNotEmpty()) {
                      var stat = SubmissionsStatus.PUBLISHED.name
                      if (status != "DRAFT") {
                        var sync = true
                        if (status == "PUBLISHED") {
                          stat = SubmissionsStatus.PUBLISHED.name
                        }
                        if (status == "SUBMITTED") {
                          stat = SubmissionsStatus.SUBMITTED.name
                        }
                        if (status == "REJECTED") {
                          sync = false
                          stat = SubmissionsStatus.REJECTED.name
                        }
                        val submissions =
                            Submissions(
                                formatDateInput(dataEntryDate),
                                "organizationsName",
                                "orgCode",
                                stat,
                                username,
                                selectedPeriod,
                                "",
                                id,
                                sync)

                        myViewModel.checkAddSubmissions(submissions)
                        loadSpecificResponses(context, id)
                      }
                    }
                  }
                } catch (e: Exception) {
                  e.printStackTrace()
                  Log.e("TAG", "json:::: ${e.message}")
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
