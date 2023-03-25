package com.bigkotik.calculator.camera

import android.util.Log
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.bigkotik.calculator.MainActivity
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraState(private val mainActivity: MainActivity) {
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(mainActivity)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindImageCapture(cameraProvider)
        }, ContextCompat.getMainExecutor(mainActivity))
    }

    fun takePicture(onPictureTaken: (File) -> Unit) {
        val file = File(mainActivity.externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(mainActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun bindImageCapture(cameraProvider: ProcessCameraProvider) {
        imageCapture = ImageCapture.Builder().build()

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(mainActivity, cameraSelector, imageCapture)
        } catch (exception: Exception) {
            Log.e(TAG, "Use case binding failed", exception)
        }
    }

    fun stopCamera() {
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXPictureTaker"
    }
}
