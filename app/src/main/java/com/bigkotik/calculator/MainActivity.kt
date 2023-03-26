package com.bigkotik.calculator

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bigkotik.calculator.camera.CameraState
import com.bigkotik.calculator.events.queuehandler.*
import com.bigkotik.calculator.voice.VoiceState
import kotlinx.android.synthetic.main.activity_main.*
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private var readyToUse = false
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

        var arr: Array<String> =
            sharedPref.getString(PREFIX_SETTING, "")?.split("")?.toTypedArray() ?: return
        arr = arr.copyOfRange(1, arr.size - 1)
        eventHandler = QueueEventHandler(
            arrayOf(
                StartRecordingEvent(arrayOf(*arr, "1"), voiceState),
                StopRecordingEvent(arrayOf(*arr, "2"), voiceState),
                StartCameraEvent(arrayOf(*arr, "("), cameraState),
                StopCameraEvent(arrayOf(*arr, ")"), cameraState),
                TakePictureEvent(arrayOf(*arr, "0"), cameraState),
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

    private fun addToInputText(buttonValue: String): String {
        eventHandler?.add(buttonValue)
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

    companion object {
        private const val PREFIX_SETTING = "PREFIX"
        private val SETTINGS = arrayOf(
            Setting("SERVER_INDEX"),
            Setting(PREFIX_SETTING),
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