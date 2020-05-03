package com.example.mvi_scaffolding.repository

import android.util.Log
import kotlinx.coroutines.Job

/*
*  extend the repository to this, store the job with class name
* */
open class JobManager(
    private val className: String
) {
    private val TAG = "AppDebug: " + JobManager::class.java.simpleName

    private val jobs: HashMap<String, Job> = HashMap()

    fun addJob(methodName: String, job: Job){
        cancelJob(methodName)
        jobs[methodName] = job
    }

    fun cancelJob(methodName: String){
        getJob(methodName)?.cancel()
    }

    fun getJob(methodName: String): Job? {
        if(jobs.containsKey(methodName)){
            jobs[methodName]?.let {
                return it
            }
        }
        return null
    }

    fun cancelActiveJobs(){
        for((methodName, job) in jobs){
            if(job.isActive){
                Log.e(TAG, "$className: cancelling job in method: '$methodName'")
                job.cancel()
            }
        }
    }
}
