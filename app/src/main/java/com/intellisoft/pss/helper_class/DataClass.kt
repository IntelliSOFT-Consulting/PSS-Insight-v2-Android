package com.intellisoft.pss

enum class NavigationValues {
  NAVIGATION,
  SUBMISSION,
  DATA_ENTRY
}

enum class SubmissionsStatus {
  SUBMITTED,
  DRAFT
}

data class DbSubmission(val date: String, val status: String)

data class DbDataEntry(val count: Int, val details: List<DbDataEntryDetails>)

data class DbDataEntryDetails(val categoryName: String, val indicators: List<DbIndicatorsDetails>)

data class DbIndicatorsDetails(
    val categoryId: String?,
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
    val categoryName: String?,
    val indicatorName: String?,
    val categoryId: String?,
    val forms: ArrayList<DbIndicators>
)
