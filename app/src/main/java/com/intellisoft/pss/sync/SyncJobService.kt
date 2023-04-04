package com.intellisoft.pss.sync

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log

class SyncJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("SyncJobService", "Syncing data...")
        // Add your data syncing logic here
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}