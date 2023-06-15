package com.intellisoft.pss.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "indicators_data")
data class IndicatorsData(
    @ColumnInfo(name = "json_data") val jsonData: String,
    var userId: String,
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}

@Entity(tableName = "responses")
data class IndicatorResponse(
    var userId: String,
    val submissionId: String,
    val indicatorId: String,
    val value: String,
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}

@Entity(tableName = "comments")
data class Comments(
    var userId: String,
    val indicatorId: String,
    val submissionId: String,
    val value: String,
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}

@Entity(tableName = "submissions")
data class Submissions(
    var date: String,
    var organization: String,
    var orgCode: String,
    val status: String,
    var userId: String,
    var period: String,
    @ColumnInfo(name = "json_data") val jsonData: String,
    var serverId: String,
    @ColumnInfo(name = "is_synced") var isSynced: Boolean = false
) {


  @PrimaryKey(autoGenerate = true) var id: Int? = null
}

@Entity(tableName = "organizations")
data class Organizations(
    var idcode: String,
    val displayName: String,
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}

@Entity(tableName = "images")
data class Image(
    val userId: String?,
    var submissionId: String?,
    var indicatorId: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) val image: ByteArray,
    val fileName: String,
    var imageUrl: String?,
    @ColumnInfo(name = "is_synced") var isSynced: Boolean = false,
    @ColumnInfo(name = "isImage") var isImage: Boolean = true
) {
  @PrimaryKey(autoGenerate = true) var id: Int? = null
}
