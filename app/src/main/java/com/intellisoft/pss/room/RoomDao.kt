package com.intellisoft.pss.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RoomDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE) fun addIndicators(indicatorsData: IndicatorsData)
  @Query("SELECT EXISTS (SELECT 1 FROM indicators_data WHERE userId =:userId)")
  fun checkUserIndicator(userId: String): Boolean
  @Query("SELECT * FROM indicators_data WHERE userId =:userId")
  fun getAllMyData(userId: String): IndicatorsData

  /** Responses */
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun addResponse(indicatorResponse: IndicatorResponse)
  @Query(
      "SELECT EXISTS (SELECT 1 FROM responses WHERE userId =:userId AND indicatorId =:indicatorId)")
  fun checkUserResponse(userId: String, indicatorId: String): Boolean
  @Query("SELECT * FROM responses WHERE userId =:userId AND indicatorId =:indicatorId")
  fun getMyResponse(userId: String, indicatorId: String): IndicatorResponse?

  @Query("SELECT * FROM responses WHERE userId =:userId")
  fun getUserResponses(userId: String): List<IndicatorResponse>
  @Query("UPDATE responses SET value =:value WHERE id =:id")
  fun updateResponse(value: String, id: Int)

  /** Comments */
  @Insert(onConflict = OnConflictStrategy.REPLACE) fun addComment(comments: Comments)
  @Query(
      "SELECT EXISTS (SELECT 1 FROM comments WHERE userId =:userId AND indicatorId =:indicatorId)")
  fun checkComment(userId: String, indicatorId: String): Boolean
  @Query("SELECT * FROM comments WHERE userId =:userId AND indicatorId =:indicatorId")
  fun getComment(userId: String, indicatorId: String): Comments?
  @Query("UPDATE comments SET value =:value WHERE id =:id")
  fun updateComment(value: String, id: Int)

  /** Submissions */
  @Insert(onConflict = OnConflictStrategy.REPLACE) fun addSubmissions(submissions: Submissions)
  @Query(
      "SELECT EXISTS (SELECT 1 FROM submissions WHERE userId =:userId AND date =:date AND status =:status)")
  fun checkSubmissions(userId: String, date: String, status: String): Boolean
  @Query("SELECT * FROM submissions WHERE userId =:userId")
  fun getSubmissions(userId: String): List<Submissions>

  @Query("SELECT * FROM submissions WHERE userId =:userId AND date =:date")
  fun getSubmissionsDetails(userId: String, date: String): Submissions?

  @Query("UPDATE submissions SET status =:status AND period =:period WHERE id =:id")
  fun updateSubmissions(status: String, id: Int, period: String)
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun addOrganizations(organizationData: Organizations)

  @Query("SELECT * FROM organizations") fun getAllOrganizations(): List<Organizations>
  @Query("SELECT * FROM submissions WHERE userId =:userId AND id =:submissionId")
  fun getSubmission(submissionId: String, userId: String): Submissions?
}
