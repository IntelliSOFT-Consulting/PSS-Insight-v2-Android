package com.intellisoft.pss.network_request

import com.intellisoft.pss.helper_class.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface Interface {

  @POST("data-entry/response/save")
  suspend fun submitData(@Body dbSaveDataEntry: DbSaveDataEntry): Response<Any>
  @PUT("data-entry/response/{id}")
  suspend fun reSubmitData(
    @Path("id") id: String,
    @Body dbSaveDataEntry: DbSaveDataEntry
  ): Response<Any>

  @GET("national-template/published-indicators") suspend fun getDataEntry(): Response<DbDataEntry>
  @GET("data-entry/response")
  suspend fun getResponses(
      @Query("dataEntryPersonId") dataEntryPersonId: String
  ): Response<DbSubmissionEntry>
  @GET("data-entry/response/{id}")
  suspend fun getResponseDetails(@Path("id") id: String): Response<DbReportDetailsEntry>

  @GET("/api/me.json?fields=organisationUnits[name,id]")
  suspend fun getOrganizations(): Response<DbOrganizationEntry>

  @Multipart
  @POST("file/upload")
  suspend fun uploadImageFileData(@Part file: MultipartBody.Part): Response<ImageResponse>
}
