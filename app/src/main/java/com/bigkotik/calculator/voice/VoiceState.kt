package com.bigkotik.calculator.voice

import android.media.MediaRecorder
import android.os.ParcelFileDescriptor
import android.util.Log
import com.bigkotik.calculator.events.queuehandler.EventException
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.Executors


class VoiceState {
    private var byteArrayOutputStream: ByteArrayOutputStream? = null
    private var recorder: MediaRecorder? = null
    private var executor = Executors.newSingleThreadExecutor()
    private var stop = false

    fun startRecording() {
        executor.submit {
            byteArrayOutputStream = ByteArrayOutputStream()

            val descriptors = ParcelFileDescriptor.createPipe()
            val parcelRead = ParcelFileDescriptor(descriptors[0])
            val parcelWrite = ParcelFileDescriptor(descriptors[1])

            val inputStream: InputStream = ParcelFileDescriptor.AutoCloseInputStream(parcelRead)

            recorder = MediaRecorder()
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder?.setOutputFile(parcelWrite.fileDescriptor)
            recorder?.prepare()

            try {
                recorder?.start()
            } catch (e: Exception) {
                Log.e(TAG, "Error on initializing recorder: ${e}: ${e.cause}: ${e.message}")
            }
            Log.e(TAG, "Started recorder")

            var read: Int
            val data = ByteArray(1024)

            var cnt = 0

            try {
                while (true) {
                    read = inputStream.read(data, 0, data.size)
                    if (read == -1) {
                        break
                    }
                    byteArrayOutputStream!!.write(data, 0, read)
                    if (cnt % 100 == 0) {
                        Log.e(TAG, "Still reading")
                    }
                    cnt++
                    if (stop) {
                        break
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "While reading ${e.message} because of ${e.cause} happened")
            }
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
            Log.e(TAG, "Flushing recorder")
            byteArrayOutputStream!!.flush()
            Log.e(TAG, "Ended recorder")
        }
    }

    fun stopRecording(onRecordingTaken: (ByteArray) -> Unit) {
        if (stop) {
            throw EventException("Voice is not recording")
        }

        Log.e(TAG, "Ending recorder")
        stop = true

        executor.submit{
            stop = false
            recorder = null
            Log.e(TAG, "Ready to execute sending context")
            onRecordingTaken(byteArrayOutputStream!!.toByteArray())
        }
    }

    companion object {
        const val TAG = "VoiceState"
    }
}