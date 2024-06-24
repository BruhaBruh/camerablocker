package ru.bruhabruh.camerablocker

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CameraViewModel(private val application: Application) : ViewModel() {
    private val sharedPreferences = application.getSharedPreferences("state", Context.MODE_PRIVATE)
    private var _isCameraDisabled by mutableStateOf(sharedPreferences.getBoolean("is_camera_disabled", false))
    val isCameraDisabled: Boolean get() = _isCameraDisabled

    fun setCameraDisabled(disabled: Boolean) {
        _isCameraDisabled = disabled
        sharedPreferences.edit().putBoolean("is_camera_disabled", disabled).apply()

        val intent = Intent(application, CameraMonitorService::class.java)
        if (!disabled) {
            intent.action = CameraMonitorService.ACTION_STOP_SERVICE
        }
        application.startService(intent)
    }

    fun toggle() {
        setCameraDisabled(!isCameraDisabled)
    }
}