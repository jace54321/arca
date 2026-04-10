package com.arca.android.data.repository

import com.arca.android.data.api.ArcaApiService
import com.arca.android.data.api.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val apiService: ArcaApiService,
) {

    suspend fun getSyncLogs(): Result<List<SyncLogDTO>> {
        return try {
            val response = apiService.getSyncLogs()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.error ?: "Failed to fetch sync logs"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun triggerSync(
        deviceName: String = "Android App",
        deviceType: String = "mobile",
    ): Result<Boolean> {
        return try {
            val response = apiService.triggerSync(deviceName, deviceType)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Sync failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDevices(): Result<List<DeviceDTO>> {
        return try {
            val response = apiService.getDevices()
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Result.success(body.data ?: emptyList())
                } else {
                    Result.failure(Exception(body?.error ?: "Failed to fetch devices"))
                }
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<ProfileResponse> {
        return try {
            val response = apiService.getProfile()
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw Exception("Empty response"))
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(username: String, avatarUrl: String?): Result<Boolean> {
        return try {
            val response = apiService.updateProfile(UpdateProfileRequest(username, avatarUrl))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(true)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
