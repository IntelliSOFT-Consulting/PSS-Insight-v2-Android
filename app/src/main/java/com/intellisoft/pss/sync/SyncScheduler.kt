package com.intellisoft.pss.sync

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle


class SyncScheduler(private val context: Context) {

    private val JOB_ID = 1

    fun schedule() {
        val componentName = ComponentName(context, SyncJobService::class.java)
        val bundle = PersistableBundle()
        val jobInfo = JobInfo.Builder(JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPeriodic(1 * 60 * 1000) // 5 minutes
            .setExtras(bundle)
            .setPersisted(true)
            .build()

        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }

    fun cancel() {
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(JOB_ID)
    }
}