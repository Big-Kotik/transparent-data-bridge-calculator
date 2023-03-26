package com.bigkotik.calculator.voice

import com.bigkotik.calculator.events.queuehandler.ButtonsSequenceEvent

class StartRecordingEvent<T>(sequence: Array<T>, private val state: VoiceState) :
    ButtonsSequenceEvent<T>(sequence) {
    override fun execute() {
        state.startRecording()
    }
}