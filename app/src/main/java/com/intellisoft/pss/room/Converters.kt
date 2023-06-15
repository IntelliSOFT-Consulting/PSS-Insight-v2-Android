package com.intellisoft.pss.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.intellisoft.pss.helper_class.*

class Converters {
    private val gson = Gson()
    @TypeConverter
    fun fromJson(json: String): DbDataEntry {
        // convert json to MyJsonData object
        return gson.fromJson(json, DbDataEntry::class.java)

    }
  @TypeConverter
    fun fromResubmitJson(json: String): DbDataEntrySubmit {
        // convert json to MyJsonData object
        return gson.fromJson(json, DbDataEntrySubmit::class.java)

    }

    @TypeConverter
    fun toJson(data: DbDataEntry): String {
        // convert MyJsonData object to json
        return gson.toJson(data)

    }  @TypeConverter
    fun fromJsonOrganization(json: String): DbOrganizationEntry {
        // convert json to MyJsonData object
        return gson.fromJson(json, DbOrganizationEntry::class.java)

    }

    @TypeConverter
    fun toJsonOrganization(data: DbOrganizationEntry): String {
        // convert MyJsonData object to json
        return gson.toJson(data)

    }
    @TypeConverter
    fun toJsonResponses(data: DbSubmissionEntry): String {
        // convert MyJsonData object to json
        return gson.toJson(data)

    }   @TypeConverter
    fun toJsonResponseDetails(data: DbReportDetailsEntry): String {
        // convert MyJsonData object to json
        return gson.toJson(data)

    }
}