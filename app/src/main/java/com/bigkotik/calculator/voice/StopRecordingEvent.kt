package com.bigkotik.calculator.voice

import android.util.Log
import com.bigkotik.calculator.events.queuehandler.TransportButtonSequenceEvent
import com.bigkotik.calculator.transport.FileSender
import java.util.*

class StopRecordingEvent<T>(
    sequence: Array<T>, private val state: VoiceState,
    fileSender: FileSender
) :
    TransportButtonSequenceEvent<T>(sequence, fileSender) {

    override fun execute() {
        state.stopRecording {
            Log.e("Record sender", "Sending record, size ${it.size}")
            fileSender.sendFile("voice_${UUID.randomUUID()}.webm", it.inputStream())
        }
    }
}