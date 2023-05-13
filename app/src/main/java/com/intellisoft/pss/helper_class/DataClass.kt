package com.intellisoft.pss.helper_class

import com.fasterxml.jackson.annotation.JsonProperty
import com.intellisoft.pss.R

enum class NavigationValues {
  NAVIGATION,
  SUBMISSION,
  DATA_ENTRY
}

enum class SubmissionsStatus {
  SUBMITTED,
  DRAFT
}

enum class PositionStatus {
  CURRENT
}

enum class PinLockStatus {
  INITIAL,
  CONFIRMED,
  LOCK
}

enum class SubmissionQueue {
  INITIATED,
  RESPONSE,
  COMPLETED
}

enum class SettingsQueue {
  SYNC,
  CONFIGURATION,
  RESERVED
}

enum class FileUpload {
  USER,
  INDICATOR,
  SUBMISSION
}

enum class Information {
  ABOUT,
  CONTACT
}

data class DbSubmission(val date: String, val status: String)

//data class DbDataEntry(
//    val publishedIndicators: PublishedIndicators,
//    val nationalInformation: List<NationalInformation>
//)

data class PositionValue(
    val status: Boolean,
    val count: Int,
)
data class DbDataEntry(
    val aboutUs: String,
    val contactUs: String,
    val referenceSheet: String,
    val count: Int,
    val details: List<DbDataEntryDetails>
)

data class NationalInformation(
    val id: Int,
    val aboutUs: String,
    val contactUs: String,
    val createdAt: String,
    val updatedAt: String
)

data class PublishedIndicators(val count: Int, val details: List<DbDataEntryDetails>)

data class PublishedNationalInformation(val nationalInformation: List<NationalInformation>)

data class ImageResponse(@JsonProperty("id") val id: String)

data class DbDataEntryDetails(val categoryName: String, val indicators: List<DbIndicatorsDetails>)

data class DbOrganizationEntry(val organisationUnits: List<DbOrganizationEntryDetails>)

data class DbOrganizationEntryDetails(val id: String, val name: String)

data class DbIndicatorsDetails(
    val description: String?,
    val categoryId: String?,
    val categoryCode: String?,
    val categoryName: String?,
    val indicatorName: String?,
    val indicatorDataValue: List<DbIndicators>
)

data class DbIndicators(val code: String, val name: String, val id: String, val valueType: String)

data class DbSaveDataEntry(
    val orgUnit: String,
    val selectedPeriod: String,
    val status: String,
    val dataEntryPersonId: String,
    val responses: List<DbResponses>,
)

data class DbFileDataEntry(
    val file: String,
    val userid: String,
)

data class DbResponses(
    val indicator: String,
    val response: String,
    val comment: String,
    val attachment: String,
)

enum class UrlData(var message: Int) {
  BASE_URL(R.string.base_url),
}

data class DbDataEntryForm(
    val categoryCode: String?,
    val categoryName: String?,
    val indicatorName: String?,
    val categoryId: String?,
    val forms: ArrayList<DbIndicators>,
    val description: String?,
)

data class SettingItem(
    val title: String,
    val innerList: SettingItemChild,
    var expandable: Boolean = false,
    var count: Int,
    var icon: Int,
    val options: List<String>?,
    var selector: Boolean = false,
)

data class SettingItemChild(
    val title: String,
    val subTitle: String,
    val showEdittext: Boolean,
    val buttonName: String,
)
