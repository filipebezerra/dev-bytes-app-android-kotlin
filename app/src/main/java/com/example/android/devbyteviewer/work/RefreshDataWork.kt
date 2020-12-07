/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.devbyteviewer.database.getDatabase
import com.example.android.devbyteviewer.repository.VideoRepository
import retrofit2.HttpException
import timber.log.Timber

/**
 * Worker which uses [VideoRepository] to refresh data from the internet and cache data to the database.
 */
class RefreshDataWork(
        private val appContext: Context,
        params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = getDatabase(appContext)
        val repository = VideoRepository(database.videoDao)

        return try {
            Timber.i("Starting refresh work")
            repository.refreshVideos()
            Timber.i("Refresh work done successfully")
            Result.success()
        } catch (error: HttpException) {
            Timber.e(error, "Failed to complete refresh work. Signalling to retry")
            Result.retry()
            // TODO Log to Crashlytics/Bugsnag
        }
    }

    companion object {
        const val WORK_NAME = "RefreshDataWork"
    }
}