package com.bigkotik.calculator

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bigkotik.calculator.alert.TelegramAlertEvent
import com.bigkotik.calculator.camera.CameraState
import com.bigkotik.calculator.camera.StartCameraEvent
import com.bigkotik.calculator.camera.StopCameraEvent
import com.bigkotik.calculator.camera.TakePictureEvent
import com.bigkotik.calculator.events.queuehandler.QueueEventHandler
import com.bigkotik.calculator.transport.FileSender
import com.bigkotik.calculator.voice.StartRecordingEvent
import com.bigkotik.calculator.voice.StopRecordingEvent
import com.bigkotik.calculator.voice.VoiceState
import kotlinx.android.synthetic.main.activity_main.*
import org.mariuszgromada.math.mxparser.Expression
import java.io.IOException
import java.text.DecimalFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var readyToUse = false
    private var fileSender: FileSender? = null
    private val cameraState = CameraState(this)
    private val voiceState = VoiceState()

    private var eventHandler: QueueEventHandler<String>? = null

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setup() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
            Toast.makeText(this, "You should give all permissions", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val valueToSet = input.text.toString()

        for (setting in SETTINGS) {
            if (!sharedPref.contains(setting.name)) {
                if (valueToSet.isEmpty()) {
                    Toast.makeText(this, "${setting.name} is not set!", Toast.LENGTH_SHORT).show()
                } else {
                    with(sharedPref.edit()) {
                        putString(setting.name, valueToSet)
                        apply()
                    }
                }
                return
            }
        }

        val destination = sharedPref.getString(SERVER_INDEX_SETTING, "")?.toInt() ?: return
        fileSender = FileSender(Uri.parse("http://192.168.1.127:10000"), destination, 1024)

        var arr: Array<String> =
            sharedPref.getString(PREFIX_SETTING, "")?.split("")?.toTypedArray() ?: return
        arr = arr.copyOfRange(1, arr.size - 1)

        val props = Properties()
        try {
            loadProperties(props)
        } catch (e: IOException) {
            Log.e(TAG, "Failed loading properties ${e.message}")
        }

        eventHandler = QueueEventHandler(
            arrayOf(
                StartRecordingEvent(arrayOf(*arr, "1"), voiceState),
                StopRecordingEvent(arrayOf(*arr, "2"), voiceState, fileSender!!),
                StartCameraEvent(arrayOf(*arr, "("), cameraState),
                StopCameraEvent(arrayOf(*arr, ")"), cameraState),
                TakePictureEvent(arrayOf("0"), cameraState, fileSender!!),
                TelegramAlertEvent(
                    arrayOf(*arr, "3"),
                    props.getProperty("bot_api_key", ""),
                    sharedPref.getString(TELEGRAM_CHAT_ID_SETTING, "")!!
                )
            )
        )

        readyToUse = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        button_clear.setOnClickListener {
            input.text = ""
            output.text = ""
            eventHandler?.clear()
        }
        button_bracket.setOnClickListener {
            input.text = addToInputText("(")
        }
        button_bracket_r.setOnClickListener {
            input.text = addToInputText(")")
        }
        button_croxx.setOnClickListener {
            input.text = input.text.toString().dropLast(1)
        }
        button_0.setOnClickListener {
            input.text = addToInputText("0")
        }
        button_1.setOnClickListener {
            input.text = addToInputText("1")
        }
        button_2.setOnClickListener {
            input.text = addToInputText("2")
        }
        button_3.setOnClickListener {
            input.text = addToInputText("3")
        }
        button_4.setOnClickListener {
            input.text = addToInputText("4")
        }
        button_5.setOnClickListener {
            input.text = addToInputText("5")
        }
        button_6.setOnClickListener {
            input.text = addToInputText("6")
        }
        button_7.setOnClickListener {
            input.text = addToInputText("7")
        }
        button_8.setOnClickListener {
            input.text = addToInputText("8")
        }
        button_9.setOnClickListener {
            input.text = addToInputText("9")
        }
        button_dot.setOnClickListener {
            input.text = addToInputText(".")
        }
        button_division.setOnClickListener {
            input.text = addToInputText("÷") // ALT + 0247
        }
        button_multiply.setOnClickListener {
            input.text = addToInputText("×") // ALT + 0215
        }
        button_subtraction.setOnClickListener {
            input.text = addToInputText("-")
        }
        button_addition.setOnClickListener {
            input.text = addToInputText("+")
        }

        button_equals.setOnClickListener {
            if (readyToUse) {
                if (input.text.isEmpty()) {
                    Toast.makeText(this, "All set up", Toast.LENGTH_SHORT).show()
                }
                showResult()
            } else {
                setup()
                input.text = ""
            }
        }

        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    private fun addToInputText(buttonValue: String): String {
        eventHandler?.add(buttonValue)
        val event = eventHandler?.check()
        try {
            event?.execute()
        } catch (e: Throwable) {
            button_equals.setBackgroundColor(Color.RED)
            Handler().postDelayed({
                button_equals.setBackgroundColor(Color.WHITE)
            }, 1000)
        }
        return input.text.toString() + buttonValue
    }

    private fun getInputExpression(): String {
        var expression = input.text.replace(Regex("÷"), "/")
        expression = expression.replace(Regex("×"), "*")
        return expression
    }

    private fun showResult() {
        try {
            val expression = getInputExpression()
            val result = Expression(expression).calculate()
            if (result.isNaN()) {
                // Show Error Message
                output.text = ""
                output.setTextColor(ContextCompat.getColor(this, R.color.red))
            } else {
                // Show Result
                output.text = DecimalFormat("0.######").format(result).toString()
                output.setTextColor(ContextCompat.getColor(this, R.color.green))
            }
        } catch (e: Exception) {
            // Show Error Message
            output.text = ""
            output.setTextColor(ContextCompat.getColor(this, R.color.red))
        }
    }

    private fun loadProperties(props: Properties) {
        assets.open("config.properties").use { stream ->
            props.load(stream)
        }
    }

    companion object {
        private const val SERVER_INDEX_SETTING = "SERVER_INDEX"
        private const val PREFIX_SETTING = "PREFIX"
        private const val TELEGRAM_CHAT_ID_SETTING = "TELEGRAM_CHAT_ID"
        private val SETTINGS = arrayOf(
            Setting(SERVER_INDEX_SETTING),
            Setting(PREFIX_SETTING),
            Setting(TELEGRAM_CHAT_ID_SETTING)
        )
        private const val TAG = "Calculator"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}