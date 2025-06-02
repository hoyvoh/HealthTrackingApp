package com.example.fitnessapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object GeminiApiClient {

    private const val API_KEY = "API KEY"
    private const val API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$API_KEY"

    suspend fun fetchMotivationalQuote(steps: Int, calories: Float, points: Int): String = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(steps, calories, points)

            val requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "${prompt.replace("\"", "\\\"")}"
                        }
                      ]
                    }
                  ]
                }
            """.trimIndent()

            val url = URL(API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            OutputStreamWriter(connection.outputStream).use { it.write(requestBody) }

            val response = StringBuilder()
            BufferedReader(InputStreamReader(connection.inputStream)).useLines { lines ->
                lines.forEach { response.append(it) }
            }

            val json = JSONObject(response.toString())
            val text = json
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            return@withContext text
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "You're doing great! Keep going!"
        }
    }

    private fun buildPrompt(steps: Int, calories: Float, points: Int): String {
        return "The user walked $steps steps, burned ${"%.1f".format(calories)} calories, and earned $points points today. Based on this performance, give a short, motivating message that encourages them to reach their goal."
    }
}
