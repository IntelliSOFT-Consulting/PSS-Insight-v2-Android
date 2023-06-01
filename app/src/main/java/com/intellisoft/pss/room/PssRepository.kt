package com.intellisoft.pss.room

import android.content.Context
import com.intellisoft.pss.helper_class.DataEntryPerson
import com.intellisoft.pss.helper_class.DbResponses
import com.intellisoft.pss.helper_class.DbSaveDataEntry
import com.intellisoft.pss.helper_class.FormatterClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PssRepository(private val roomDao: RoomDao) {

  private val formatterClass = FormatterClass()

  fun addIndicators(indicatorsData: IndicatorsData) {

    val userId = indicatorsData.userId
    val isIndicators = roomDao.checkUserIndicator(userId)
    if (!isIndicators) {
      roomDao.addIndicators(indicatorsData)
    } else {}
  }
  fun clearIndicators() {
    roomDao.clearIndicators()
  }
  fun getAllMyData(context: Context): IndicatorsData? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getAllMyData(userId)
    }
    return null
  }
  fun getSubmissions(context: Context): List<Submissions> {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getSubmissions(userId)
    }
    return emptyList()
  }

  fun addResponse(indicatorResponse: IndicatorResponse) {

    CoroutineScope(Dispatchers.IO).launch {
      val userId = indicatorResponse.userId
      val indicatorId = indicatorResponse.indicatorId
      val value = indicatorResponse.value
      val submissionId = indicatorResponse.submissionId

      val isResponse = roomDao.checkUserResponse(userId, indicatorId, submissionId)
      if (!isResponse) {
        roomDao.addResponse(indicatorResponse)
      } else {
        val response = roomDao.getMyResponse(userId, indicatorId, submissionId)
        if (response != null) {
          val id = response.id
          if (id != null) {
            roomDao.updateResponse(value, id)
          }
        }
      }
    }
  }

  fun getMyResponse(context: Context, indicatorId: String, submissionId: String): String? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      val response = roomDao.getMyResponse(userId, indicatorId, submissionId)
      if (response != null) {
        val value = response.value
        return value
      }
    }
    return null
  }
  fun getMyComment(context: Context, indicatorId: String, submissionId: String): String? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      val response = roomDao.getMyComment(userId, indicatorId, submissionId)
      if (response != null) {
        val value = response.value
        return value
      }
    }
    return null
  }
  fun getMyImage(context: Context, indicatorId: String, submissionId: String): String? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      val response = roomDao.getMyImage(userId, indicatorId, submissionId)
      if (response != null) {
        val value = response.fileName
        return value
      }
    }
    return null
  }

  fun addSubmissions(submissions: Submissions) {
    val userId = submissions.userId
    val date = submissions.date
    val status = submissions.status

    val isSubmissions = roomDao.checkSubmissions(userId, date, status)
    if (!isSubmissions) {
      roomDao.addSubmissions(submissions)
    } else {
      val submissionsDetails = roomDao.getSubmissionsDetails(userId, date)
      if (submissionsDetails != null) {
        val id = submissionsDetails.id
        val period = submissionsDetails.period
        if (id != null) {
          roomDao.updateSubmissions(status, id, period)
        }
      }
    }
  }
  fun checkAddSubmissions(submissions: Submissions) {
    val serverId = submissions.serverId
    val isSubmissions = roomDao.checkServerSubmissions(serverId)
    if (!isSubmissions) {
      roomDao.addSubmissions(submissions)
    }
  }

  fun initiateSubmissions(submissions: Submissions) {
    roomDao.addSubmissions(submissions)
  }

  fun addComment(comments: Comments) {

    CoroutineScope(Dispatchers.IO).launch {
      val userId = comments.userId
      val indicatorId = comments.indicatorId
      val submissionId = comments.submissionId
      val value = comments.value

      val isResponse = roomDao.checkComment(userId, indicatorId, submissionId)
      if (!isResponse) {
        roomDao.addComment(comments)
      } else {
        val response = roomDao.getComment(userId, indicatorId, submissionId)
        if (response != null) {
          val id = response.id
          if (id != null) {
            roomDao.updateComment(value, id)
          }
        }
      }
    }
  }

  fun getSubmitData(context: Context): DbSaveDataEntry? {

    // get the logged in user
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {

      // Get responses
      val dbResponsesList = ArrayList<DbResponses>()
      val responseList = roomDao.getUserResponses(userId)
      responseList.forEach {
        val valueResponse = it.value
        val indicatorId = it.indicatorId
        //        val selectedPeriod=it.p
        // Get comments
        val comment = roomDao.getComment(userId, indicatorId, "")
        var valueComment = ""
        if (comment != null) {
          valueComment = comment.value
        }
        // Get Attachment

        val dbResponses = DbResponses(indicatorId, valueResponse, valueComment, "")
        dbResponsesList.add(dbResponses)
      }

      val id = formatterClass.getSharedPref("userId", context)
      val firstName = formatterClass.getSharedPref("firstName", context)
      val userSurname = formatterClass.getSharedPref("userSurname", context)
      val dataEntryPerson = DataEntryPerson("$id", username = userId, firstName = "$firstName", surname = "$userSurname")
      return DbSaveDataEntry("", "2023", "COMPLETED", userId, "", dbResponsesList, dataEntryPerson)
    }
    return null
  }
  fun getSubmitDataSync(context: Context, submissions: Submissions): DbSaveDataEntry? {

    // get the logged in user
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {

      // Get responses
      val dbResponsesList = ArrayList<DbResponses>()
      val responseList = roomDao.getUserSubmissionResponses(userId, submissions.id.toString())
      responseList.forEach {
        val valueResponse = it.value
        val indicatorId = it.indicatorId
        //        val selectedPeriod=it.p
        // Get comments
        val comment = roomDao.getComment(userId, indicatorId, submissions.id.toString())
        var valueComment = ""
        if (comment != null) {
          valueComment = comment.value
        }
        // Get Attachment
        val attached = roomDao.getIndicatorImage(userId, indicatorId, submissions.id.toString())
        var attachmentValue = ""
        if (attached != null) {
          val imgURL = attached.imageUrl
          if (imgURL != null) {
            attachmentValue = imgURL
          }
        }
        val dbResponses = DbResponses(indicatorId, valueResponse, valueComment, attachmentValue)
        dbResponsesList.add(dbResponses)
      }
      val id = formatterClass.getSharedPref("userId", context)
      val firstName = formatterClass.getSharedPref("firstName", context)
      val userSurname = formatterClass.getSharedPref("userSurname", context)
      val dataEntryPerson = DataEntryPerson("$id", username = userId, firstName = "$firstName", surname = "$userSurname")
      return DbSaveDataEntry(
          submissions.orgCode,
          submissions.period,
          "COMPLETED",
          userId,
          submissions.date,
          dbResponsesList,
          dataEntryPerson)
    }
    return null
  }

  fun addOrganizations(organizationData: Organizations) {
    roomDao.addOrganizations(organizationData)
  }

  fun getAllOrganizations(context: Context): List<Organizations>? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getAllOrganizations()
    }
    return emptyList()
  }

  fun getMatchingOrganizations(context: Context, name: String): List<Organizations>? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getMatchingOrganizations(name)
    }
    return emptyList()
  }

  fun getSubmission(submissionId: String, context: Context): Submissions? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getSubmission(submissionId, userId)
    }
    return null
  }

  fun getLatestSubmission(context: Context): Submissions? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getLatestSubmission()
    }
    return null
  }

  fun updateSubmissions(submissions: Submissions, submissionId: String) {
    val userId = submissions.userId
    val date = submissions.date
    val status = submissions.status
    val org = submissions.organization
    val peri = submissions.period
    val orgCode = submissions.orgCode

    val isSubmissions = roomDao.checkSubmissionPerId(userId, submissionId)
    if (!isSubmissions) {
      roomDao.addSubmissions(submissions)
    } else {
      val submissionsDetails = roomDao.getSubmissionsById(userId, submissionId)

      roomDao.updateSubmissionOrg(status, submissionId, peri, org, orgCode)
    }
  }

  fun getUnsyncedSubmissions(context: Context, status: String): List<Submissions> {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getUnsyncedSubmissions(userId, status)
    }
    return emptyList()
  }

  fun markSynced(context: Context, id: String): Boolean {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      val submissionsDetails = roomDao.getSubmissionsById(userId, id)
      if (submissionsDetails != null) {
        roomDao.updateSubmissionSync(true, id)
      }
      return true
    }
    return false
  }

  fun getSubmissionResponses(context: Context, submissionId: String): Int {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getResponsesCount(userId, submissionId)
    }
    return 0
  }

  fun uploadImage(context: Context, file: Image) {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      val submissionId = file.submissionId
      val indicatorId = file.indicatorId
      val exists =
          roomDao.getImageByUserIdInSubId(userId, submissionId.toString(), indicatorId.toString())
      if (exists != null) {
        roomDao.updateImageByte(
            file.image, file.fileName, submissionId.toString(), indicatorId.toString())
      } else {
        roomDao.uploadImage(file)
      }
    }
  }

  fun getAllImages(context: Context, is_synced: Boolean): List<Image> {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getAllImages(is_synced, userId)
    }
    return emptyList()
  }

  fun deleteAllOrganizations() {
    roomDao.deleteAllOrganizations()
  }

  fun clearAppData() {
    roomDao.clearAppData()
    roomDao.clearResponses()
    roomDao.clearComments()
  }

  fun getImage(
      context: Context,
      userId: String,
      indicatorId: String,
      submissionId: String
  ): Image? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getMyImage(userId, indicatorId, submissionId)
    }
    return null
  }

  fun updateImageLink(context: Context, submissionId: String?, indicatorId: String?, url: String) {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      roomDao.updateImageLink(userId, indicatorId.toString(), submissionId.toString(), url, true)
    }
  }

  fun getSubmissionById(context: Context, userId: String, submissionId: String): Submissions? {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      return roomDao.getSubmissionsById(userId, submissionId)
    }
    return null
  }

  fun deleteAllSubmitted(context: Context, id: String, submissionId: String) {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      roomDao.deleteAllSubmitted(userId, id, submissionId)
    }
  }

  fun updateOriginalResponses(context: Context, id: String, response: String) {
    val userId = formatterClass.getSharedPref("username", context)
    if (userId != null) {
      roomDao.updateOriginalResponses(userId, id, response)
    }
  }
}
