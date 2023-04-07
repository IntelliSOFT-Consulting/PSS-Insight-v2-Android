package com.intellisoft.pss.room

import android.content.Context
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

  fun initiateSubmissions(submissions: Submissions) {
    roomDao.addSubmissions(submissions)
  }

  fun addComment(comments: Comments) {

    CoroutineScope(Dispatchers.IO).launch {
      val userId = comments.userId
      val indicatorId = comments.indicatorId
      val submissionId=comments.submissionId
      val value = comments.value

      val isResponse = roomDao.checkComment(userId, indicatorId,submissionId)
      if (!isResponse) {
        roomDao.addComment(comments)
      } else {
        val response = roomDao.getComment(userId, indicatorId,submissionId)
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
        val comment = roomDao.getComment(userId, indicatorId,"")
        var valueComment = ""
        if (comment != null) {
          valueComment = comment.value
        }
        // Get Attachment

        val dbResponses = DbResponses(indicatorId, valueResponse, valueComment, "")
        dbResponsesList.add(dbResponses)
      }
      return DbSaveDataEntry("", "2023", "COMPLETED", userId, dbResponsesList)
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
        val comment = roomDao.getComment(userId, indicatorId,submissions.id.toString())
        var valueComment = ""
        if (comment != null) {
          valueComment = comment.value
        }
        // Get Attachment

        val dbResponses = DbResponses(indicatorId, valueResponse, valueComment, "")
        dbResponsesList.add(dbResponses)
      }
      return DbSaveDataEntry(
          submissions.organization, submissions.period, "COMPLETED", userId, dbResponsesList)
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
    return null
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

    val isSubmissions = roomDao.checkSubmissionPerId(userId, date, submissionId)
    if (!isSubmissions) {
      roomDao.addSubmissions(submissions)
    } else {
      val submissionsDetails = roomDao.getSubmissionsById(userId, submissionId)

      roomDao.updateSubmissionOrg(status, submissionId, peri, org)
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

    fun getSubmissionResponses(context: Context, submissionId: String) :Int{
      val userId = formatterClass.getSharedPref("username", context)
      if (userId != null) {
        return  roomDao.getResponsesCount(userId, submissionId)
      }
      return 0
    }
}
