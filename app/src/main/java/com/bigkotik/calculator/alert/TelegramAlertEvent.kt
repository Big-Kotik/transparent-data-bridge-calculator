package com.bigkotik.calculator.alert

import android.util.Log
import com.bigkotik.calculator.events.queuehandler.ButtonsSequenceEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class TelegramAlertEvent<T>(sequence: Array<T>, accessToken: String, private val chatId: String) :
    ButtonsSequenceEvent<T>(sequence) {

    private val client = OkHttpClient()
    private val baseUrlBuilder = "https://api.telegram.org/bot$accessToken"
        .toHttpUrl()
        .newBuilder()

    override fun execute() {
        val url = baseUrlBuilder
            .addPathSegment("sendMessage")
            .addQueryParameter("chat_id", chatId)
            .addQueryParameter("text", "SOS-SOS-SOS")
            .build()
        val request = Request.Builder()
            .url(url)
            .build()
        Dispatchers.IO.asExecutor().execute {
            try {
                client.newCall(request).execute().use { response ->
                    val body = response.body!!.string()
                    if (response.code != 200) {
                        Log.e(TAG, body)
                    } else {
                        Log.i(TAG, body)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed sending alert: ${e.message}")
            }
        }
    }

    companion object {
        const val TAG = "TelegramAlert"
    }

}