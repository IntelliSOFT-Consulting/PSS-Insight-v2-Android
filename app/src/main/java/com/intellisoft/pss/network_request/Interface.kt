package com.intellisoft.pss.network_request

import com.intellisoft.pss.DbDataEntry
import com.intellisoft.pss.DbSaveDataEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Interface {

    @POST("data-entry/response/save")
    suspend fun submitData(@Body dbSaveDataEntry: DbSaveDataEntry): Response<Any>

    @GET("national-instance/indicators")
    suspend fun getDataEntry(): Response<DbDataEntry>

}