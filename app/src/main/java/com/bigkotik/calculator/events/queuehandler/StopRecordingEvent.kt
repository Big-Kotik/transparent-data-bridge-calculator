package com.bigkotik.calculator.events.queuehandler

import android.net.Uri
import com.bigkotik.calculator.transport.FileSender
import com.bigkotik.calculator.voice.VoiceState
import java.util.*

class StopRecordingEvent<T>(sequence: Array<T>, private val state: VoiceState) :
    ButtonsSequenceEvent<T>(sequence) {
    private val fileSender = FileSender(Uri.parse("http://10.0.2.2:10000"), 1024)

    override fun execute() {
        state.stopRecording{
            fileSender.sendFile("voice_${UUID.randomUUID()}.amr", it.inputStream())
        }
    }
}