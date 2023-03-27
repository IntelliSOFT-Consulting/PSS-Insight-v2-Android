package com.intellisoft.pss.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "indicators_data")
data class IndicatorsData(
    @ColumnInfo(name = "json_data") val jsonData: String,
    var userId: String ,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "responses")
data class IndicatorResponse(
    var userId: String ,
    val indicatorId: String,
    val value: String,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
@Entity(tableName = "comments")
data class Comments(
    var userId: String ,
    val indicatorId: String,
    val value: String,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}

@Entity(tableName = "submissions")
data class Submissions(
    var date: String ,
    val status: String,
    var userId: String ,
    var period: String ,
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}


