package com.intellisoft.pss.room

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.runBlocking

class PssViewModel(application: Application) : AndroidViewModel(application) {

    private val repository:PssRepository
    init {
        val roomDao = PssDatabase.getDatabase(application).roomDao()
        repository = PssRepository(roomDao)
    }

    fun addIndicators(indicatorsData:IndicatorsData){
        repository.addIndicators(indicatorsData)
    }
    fun addResponse(indicatorResponse:IndicatorResponse){
        repository.addResponse(indicatorResponse)
    }
    fun addComment(comments:Comments){
        repository.addComment(comments)
    }

    fun addSubmissions(submissions:Submissions){
        repository.addSubmissions(submissions)
    }
    fun getAllMyData(context: Context) = runBlocking{
        repository.getAllMyData(context)
    }
    fun getMyResponse(context: Context, indicatorId: String) = runBlocking{
        repository.getMyResponse(context, indicatorId)
    }
    fun getSubmissions(context: Context) = runBlocking{
        repository.getSubmissions(context)
    }
    fun getSubmitData(context: Context) = runBlocking{
        repository.getSubmitData(context)
    }

    fun addOrganizations(organizationData: Organizations) {
        repository.addOrganizations(organizationData)
    }
    fun getOrganizations(context: Context) = runBlocking{
        repository.getAllOrganizations(context)
    }

    fun getSubmission(submissionId: String, context: Context)= runBlocking {
        repository.getSubmission(submissionId,context)
    }

}