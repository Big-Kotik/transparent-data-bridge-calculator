package com.bigkotik.calculator.events.queuehandler

import android.net.Uri
import android.util.Log
import com.bigkotik.calculator.transport.FileSender
import com.bigkotik.calculator.voice.VoiceState

class StartRecordingEvent<T>(sequence: Array<T>, private val state: VoiceState) :
    ButtonsSequenceEvent<T>(sequence) {
    override fun execute() {
        FileSender(Uri.parse("http://10.0.2.2:10000"), 1024).sendFile("dickPick", "Your mum is fat".byteInputStream())
    }
}