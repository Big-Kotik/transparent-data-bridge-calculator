package com.bigkotik.calculator.voice


import android.media.MediaRecorder
import com.bigkotik.calculator.MainActivity
import java.io.IOException

class VoiceState(currentDir: String) {

    private var output: String? =
        "${currentDir}/voice${System.currentTimeMillis()}.3gp"
    private var mediaRecorder: MediaRecorder? = MediaRecorder(
    ).apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setOutputFile(output)
    }
    private var state: Boolean = false

    // TODO: pause of needed
//    private var recordingStopped: Boolean = false

    public fun startRecording() {
        try {
            mediaRecorder?.prepare()
            mediaRecorder?.start()
            state = true
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    public fun stopRecording() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        state = false
    }
}