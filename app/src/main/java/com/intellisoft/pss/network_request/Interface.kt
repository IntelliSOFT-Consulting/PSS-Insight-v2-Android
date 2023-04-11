package com.intellisoft.pss.network_request

import com.intellisoft.pss.helper_class.DbDataEntry
import com.intellisoft.pss.helper_class.DbOrganizationEntry
import com.intellisoft.pss.helper_class.DbSaveDataEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Interface {

    @POST("data-entry/response/save")
    suspend fun submitData(@Body dbSaveDataEntry: DbSaveDataEntry): Response<Any>
    @POST("data-entry/response/save")
    suspend fun uploadImageData(@Body dbSaveDataEntry: DbSaveDataEntry): Response<Any>

    @GET("national-template/published-indicators")
    suspend fun getDataEntry(): Response<DbDataEntry>
    @GET("national-template/organisation-units")
    suspend fun getOrganizations(): Response<DbOrganizationEntry>

}