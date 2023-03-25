package com.bigkotik.calculator.events.queuehandler

import com.bigkotik.calculator.camera.CameraState

class StartCameraEvent<T>(sequence: Array<T>, private val state: CameraState) :
    ButtonsSequenceEvent<T>(sequence) {
    override fun execute() {
        state.startCamera()
    }
}