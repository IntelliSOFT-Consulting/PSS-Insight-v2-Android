package com.intellisoft.pss.room

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.runBlocking

class PssViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: PssRepository
  init {
    val roomDao = PssDatabase.getDatabase(application).roomDao()
    repository = PssRepository(roomDao)
  }

  fun addIndicators(indicatorsData: IndicatorsData) {
    repository.addIndicators(indicatorsData)
  }
  fun clearIndicators() {
    repository.clearIndicators()
  }
  fun addResponse(indicatorResponse: IndicatorResponse) {
    repository.addResponse(indicatorResponse)
  }
  fun addComment(comments: Comments) {
    repository.addComment(comments)
  }

  fun addSubmissions(submissions: Submissions) {
    repository.addSubmissions(submissions)
  }
  fun initiateSubmissions(submissions: Submissions) {
    repository.initiateSubmissions(submissions)
  }
  fun getAllMyData(context: Context) = runBlocking { repository.getAllMyData(context) }
  fun getMyResponse(context: Context, indicatorId: String, submissionId: String) = runBlocking {
    repository.getMyResponse(context, indicatorId, submissionId)
  }
  fun getMyComment(context: Context, indicatorId: String, submissionId: String) = runBlocking {
    repository.getMyComment(context, indicatorId, submissionId)
  }
  fun getMyImage(context: Context, indicatorId: String, submissionId: String) = runBlocking {
    repository.getMyImage(context, indicatorId, submissionId)
  }
  fun getSubmissions(context: Context) = runBlocking { repository.getSubmissions(context) }
  fun getSubmitData(context: Context) = runBlocking { repository.getSubmitData(context) }
  fun getSubmitSync(context: Context, submissions: Submissions) = runBlocking {
    repository.getSubmitDataSync(context, submissions)
  }

  fun addOrganizations(organizationData: Organizations) {
    repository.addOrganizations(organizationData)
  }
  fun getOrganizations(context: Context) = runBlocking { repository.getAllOrganizations(context) }
  fun getMatchingOrganizations(context: Context, name: String) = runBlocking {
    val pattern = "%$name%"
    repository.getMatchingOrganizations(context, pattern)
  }

  fun getSubmission(submissionId: String, context: Context) = runBlocking {
    repository.getSubmission(submissionId, context)
  }

  fun getLatestSubmission(context: Context) = runBlocking {
    repository.getLatestSubmission(context)
  }

  fun updateSubmissions(submissions: Submissions, submissionId: String) {
    repository.updateSubmissions(submissions, submissionId)
  }

  fun getUnsyncedSubmissions(context: Context, status: String) = runBlocking {
    repository.getUnsyncedSubmissions(context, status)
  }
  fun markSynced(context: Context, id: String) = runBlocking { repository.markSynced(context, id) }

  fun getSubmissionResponses(context: Context, submissionId: String) = runBlocking {
    repository.getSubmissionResponses(context, submissionId)
  }

  fun uploadImage(context: Context, image: Image) = runBlocking {
    repository.uploadImage(context, image)
  }

  fun getAllImages(context: Context,is_synced:Boolean) = runBlocking { repository.getAllImages(context,is_synced) }

  fun deleteAllOrganizations() = runBlocking { repository.deleteAllOrganizations() }

  fun clearAppData() = runBlocking { repository.clearAppData() }

  fun getImage(context: Context, userId: String, indicatorId: String, submissionId: String) =
      runBlocking {
        repository.getImage(context, userId, indicatorId, submissionId)
      }

  fun updateImageLink(context: Context,image: Image, url: String) = runBlocking {
    repository.updateImageLink(context, image.submissionId,image.indicatorId, url)
  }

    fun getSubmissionsById(context: Context,userId: String, submissionId: String)= runBlocking {
      repository.getSubmissionById(context, userId,submissionId)
    }

    fun deleteAllSubmitted(context: Context,id: String,submissionId: String)= runBlocking {
      repository.deleteAllSubmitted(context, id,submissionId)
    }

  fun updateOriginalResponses(context: Context, id: String, response: String) =
    runBlocking{
      repository.updateOriginalResponses(context, id,response)
  }



  fun checkAddSubmissions(submissions: Submissions) = runBlocking{
    repository.checkAddSubmissions(submissions)
  }
}
