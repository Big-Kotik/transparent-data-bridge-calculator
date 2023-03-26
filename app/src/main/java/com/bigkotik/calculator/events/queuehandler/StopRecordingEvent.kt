package com.bigkotik.calculator.events.queuehandler

import android.net.Uri
import android.util.Log
import com.bigkotik.calculator.transport.FileSender
import com.bigkotik.calculator.voice.VoiceState
import java.util.*

class StopRecordingEvent<T>(sequence: Array<T>, private val state: VoiceState) :
    ButtonsSequenceEvent<T>(sequence) {
    private val fileSender = FileSender(Uri.parse("http://192.168.1.127:10000"), 1024)

    override fun execute() {
        state.stopRecording {
            Log.e("Record sender", "Sending record, size ${it.size}")
            fileSender.sendFile("voice_${UUID.randomUUID()}.webm", it.inputStream())
        }
    }
}