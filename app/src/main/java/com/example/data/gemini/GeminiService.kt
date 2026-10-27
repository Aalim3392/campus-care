package com.example.data.gemini

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun getApiKey(): String {
        return try {
            val key = BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
            if (key == "MY_GEMINI_API_KEY") "" else key
        } catch (_: Exception) {
            ""
        }
    }

    /**
     * Multi-turn chat with CampusWell AI Counselor using gemini-3.8-flash
     */
    suspend fun chatWithCounselor(
        history: List<Pair<String, String>>, // sender ("user" or "model") to text
        userMessage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(
                Exception("Gemini API key is not configured. Please set GEMINI_API_KEY in the Secrets panel.")
            )
        }

        val model = "gemini-3.8-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val contentsArray = JSONArray()

        // Include conversation history
        for ((role, text) in history) {
            val roleObj = JSONObject().apply {
                put("role", if (role.equals("user", true)) "user" else "model")
                put("parts", JSONArray().put(JSONObject().put("text", text)))
            }
            contentsArray.put(roleObj)
        }

        // Add latest user message
        contentsArray.put(
            JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            }
        )

        val systemInstruction = JSONObject().apply {
            put(
                "parts",
                JSONArray().put(
                    JSONObject().put(
                        "text",
                        "You are CampusWell AI, a gentle, compassionate, and supportive campus wellness guide " +
                                "and academic counselor for engineering and university students. " +
                                "Provide empathetic, evidence-based self-care, study-life balance advice, stress management tips, " +
                                "and calm encouragement. Keep responses constructive, warm, concise, and easy to read. " +
                                "If a student appears in acute crisis, gently remind them of 24/7 campus safety/crisis helpline (ext 4100)."
                    )
                )
            )
        }

        val requestJson = JSONObject().apply {
            put("contents", contentsArray)
            put("systemInstruction", systemInstruction)
            put(
                "generationConfig",
                JSONObject().apply {
                    put("temperature", 0.7)
                    put("topP", 0.95)
                }
            )
        }

        val body = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        try {
            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Gemini API Error: HTTP ${response.code} $responseString"))
            }

            val json = JSONObject(responseString)
            val candidates = json.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (text != null && text.isNotBlank()) {
                Result.success(text)
            } else {
                Result.failure(Exception("No response received from model."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Generate Wellness Insights based on daily metrics using gemini-3.1-flash-lite-preview
     */
    suspend fun generateMetricInsight(
        sleep: Float,
        water: Int,
        study: Float,
        mood: String,
        stress: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.success(
                "Great work keeping up with your daily check-in! Maintaining 7h+ of sleep and regular hydration will help sustain deep focus during your engineering lectures."
            )
        }

        val model = "gemini-3.1-flash-lite-preview"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val prompt = "Student wellness check-in: Sleep = ${sleep}h, Water = ${water} glasses, Study = ${study}h, " +
                "Mood = $mood, Stress Level = $stress / 5. " +
                "In 2-3 motivating and helpful sentences, provide a personalized wellness tip and cognitive pacing recommendation."

        val requestJson = JSONObject().apply {
            put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", prompt))
                    )
                )
            )
        }

        val body = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        try {
            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""
            if (response.isSuccessful) {
                val json = JSONObject(responseString)
                val text = json.optJSONArray("candidates")
                    ?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                    ?.optJSONObject(0)
                    ?.optString("text")
                Result.success(text ?: "Stay hydrated and remember to take rest pauses!")
            } else {
                Result.success("Keep pacing your studies and take scheduled restorative breaks between lectures.")
            }
        } catch (e: Exception) {
            Result.success("Keep pacing your studies and take scheduled restorative breaks between lectures.")
        }
    }

    /**
     * Generate high-quality wallpaper or image using gemini-3-pro-image-preview
     * with size options: 1K, 2K, 4K
     */
    suspend fun generateHighQualityImage(
        prompt: String,
        imageSize: String = "1K", // "1K", "2K", "4K"
        aspectRatio: String = "16:9"
    ): Result<Bitmap> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext Result.failure(Exception("Gemini API key is required."))
        }

        val model = "gemini-3-pro-image-preview"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            put(
                "contents",
                JSONArray().put(
                    JSONObject().put(
                        "parts",
                        JSONArray().put(JSONObject().put("text", prompt))
                    )
                )
            )
            put(
                "generationConfig",
                JSONObject().apply {
                    put("responseModalities", JSONArray().put("IMAGE").put("TEXT"))
                    put(
                        "imageConfig",
                        JSONObject().apply {
                            put("aspectRatio", aspectRatio)
                            put("imageSize", imageSize)
                        }
                    )
                }
            )
        }

        val body = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        try {
            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Image generation failed: HTTP ${response.code}"))
            }

            val json = JSONObject(responseString)
            val parts = json.optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")

            var foundBitmap: Bitmap? = null
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val p = parts.getJSONObject(i)
                    val inlineData = p.optJSONObject("inlineData")
                    if (inlineData != null) {
                        val base64Data = inlineData.optString("data")
                        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
                        foundBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        break
                    }
                }
            }

            if (foundBitmap != null) {
                Result.success(foundBitmap)
            } else {
                Result.failure(Exception("No image returned by model."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
