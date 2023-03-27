package com.intellisoft.pss.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.intellisoft.pss.DbDataEntry

class Converters {
    private val gson = Gson()
    @TypeConverter
    fun fromJson(json: String): DbDataEntry {
        // convert json to MyJsonData object
        return gson.fromJson(json, DbDataEntry::class.java)

    }

    @TypeConverter
    fun toJson(data: DbDataEntry): String {
        // convert MyJsonData object to json
        return gson.toJson(data)

    }
}