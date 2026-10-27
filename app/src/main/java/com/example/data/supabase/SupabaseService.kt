package com.example.data.supabase

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.BuildConfig
import com.example.model.CheckInRecord
import com.example.model.StressAlert
import com.example.model.SupportRequestRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "supabase_settings")

class SupabaseService(private val context: Context) {

    private val KEY_URL = stringPreferencesKey("supabase_url")
    private val KEY_ANON_KEY = stringPreferencesKey("supabase_anon_key")

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val configFlow: Flow<Pair<String, String>> = context.dataStore.data.map { preferences ->
        val url = preferences[KEY_URL] ?: try {
            BuildConfig::class.java.getField("SUPABASE_URL").get(null) as? String ?: ""
        } catch (_: Exception) { "" }

        val key = preferences[KEY_ANON_KEY] ?: try {
            BuildConfig::class.java.getField("SUPABASE_ANON_KEY").get(null) as? String ?: ""
        } catch (_: Exception) { "" }

        Pair(url.trim(), key.trim())
    }

    suspend fun saveConfig(url: String, anonKey: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_URL] = url.trim()
            preferences[KEY_ANON_KEY] = anonKey.trim()
        }
    }

    suspend fun testConnection(): Result<String> = withContext(Dispatchers.IO) {
        val (url, key) = configFlow.first()
        if (url.isBlank() || key.isBlank()) {
            return@withContext Result.failure(Exception("Supabase URL and Anon Key are required."))
        }

        val cleanUrl = url.trimEnd('/')
        val requestUrl = "$cleanUrl/rest/v1/"

        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("apikey", key)
            .addHeader("Authorization", "Bearer $key")
            .get()
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful || response.code in 200..299 || response.code == 404 || response.code == 401) {
                if (response.code == 401) {
                    Result.failure(Exception("Supabase responded with 401 Unauthorized. Check your Anon Key."))
                } else {
                    Result.success("Connected to Supabase (${response.code} OK)")
                }
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncCheckIn(checkIn: CheckInRecord): Result<Boolean> = withContext(Dispatchers.IO) {
        val (url, key) = configFlow.first()
        if (url.isBlank() || key.isBlank()) {
            return@withContext Result.failure(Exception("Supabase not configured."))
        }

        val cleanUrl = url.trimEnd('/')
        val requestUrl = "$cleanUrl/rest/v1/checkins"

        val json = JSONObject().apply {
            put("id", checkIn.id)
            put("date", checkIn.date)
            put("day_of_week", checkIn.dayOfWeek)
            put("mood", checkIn.mood)
            put("sleep_hours", checkIn.sleepHours)
            put("water_glasses", checkIn.waterGlasses)
            put("study_hours", checkIn.studyHours)
            put("stress_level", checkIn.stressLevel)
            put("user_email", checkIn.userEmail)
            put("user_name", checkIn.userName)
            put("user_role", checkIn.userRole)
            put("timestamp", checkIn.timestamp)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("apikey", key)
            .addHeader("Authorization", "Bearer $key")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .post(body)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful || response.code == 201 || response.code == 200) {
                Result.success(true)
            } else {
                Result.failure(Exception("Supabase sync failed: HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncSupportRequest(req: SupportRequestRecord): Result<Boolean> = withContext(Dispatchers.IO) {
        val (url, key) = configFlow.first()
        if (url.isBlank() || key.isBlank()) {
            return@withContext Result.failure(Exception("Supabase not configured."))
        }

        val cleanUrl = url.trimEnd('/')
        val requestUrl = "$cleanUrl/rest/v1/support_requests"

        val json = JSONObject().apply {
            put("id", req.id)
            put("topic", req.topic)
            put("message", req.message)
            put("date", req.date)
            put("user_email", req.userEmail)
            put("user_name", req.userName)
            put("status", req.status)
            put("timestamp", req.timestamp)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("apikey", key)
            .addHeader("Authorization", "Bearer $key")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .post(body)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful || response.code in 200..204) {
                Result.success(true)
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncStressAlert(alert: StressAlert): Result<Boolean> = withContext(Dispatchers.IO) {
        val (url, key) = configFlow.first()
        if (url.isBlank() || key.isBlank()) {
            return@withContext Result.failure(Exception("Supabase not configured."))
        }

        val cleanUrl = url.trimEnd('/')
        val requestUrl = "$cleanUrl/rest/v1/stress_alerts"

        val json = JSONObject().apply {
            put("id", alert.id)
            put("student_name", alert.studentName)
            put("student_email", alert.studentEmail)
            put("stress_level", alert.stressLevel)
            put("alert_type", alert.alertType.name)
            put("ai_message", alert.aiMessage)
            put("faculty_informed_name", alert.facultyInformedName)
            put("timestamp", alert.timestamp)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(requestUrl)
            .addHeader("apikey", key)
            .addHeader("Authorization", "Bearer $key")
            .addHeader("Prefer", "resolution=merge-duplicates")
            .post(body)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful || response.code in 200..204) {
                Result.success(true)
            } else {
                Result.failure(Exception("HTTP ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
