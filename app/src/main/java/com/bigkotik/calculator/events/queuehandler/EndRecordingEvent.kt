package com.bigkotik.calculator.events.queuehandler

import com.bigkotik.calculator.voice.VoiceState

class EndRecordingEvent<T>(sequence: Array<T>, private val state: VoiceState) :
    ButtonsSequenceEvent<T>(sequence) {
    override fun execute() {
        TODO("Not yet implemented")
    }
}