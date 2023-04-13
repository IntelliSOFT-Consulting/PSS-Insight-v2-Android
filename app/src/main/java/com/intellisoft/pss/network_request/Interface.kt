package com.intellisoft.pss.network_request

import com.intellisoft.pss.helper_class.DbDataEntry
import com.intellisoft.pss.helper_class.DbOrganizationEntry
import com.intellisoft.pss.helper_class.DbSaveDataEntry
import com.intellisoft.pss.helper_class.ImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface Interface {

  @POST("data-entry/response/save")
  suspend fun submitData(@Body dbSaveDataEntry: DbSaveDataEntry): Response<Any>

  @GET("national-template/published-indicators") suspend fun getDataEntry(): Response<DbDataEntry>
  @GET("national-template/organisation-units")
  suspend fun getOrganizations(): Response<DbOrganizationEntry>

  @Multipart
  @POST("file/upload")
  suspend fun uploadImageFileData(@Part file: MultipartBody.Part): Response<ImageResponse>
}
