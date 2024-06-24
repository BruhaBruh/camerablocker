package ru.bruhabruh.camerablocker

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class IsCameraDisabledViewModel(application: Application) : ViewModel() {
    private val sharedPreferences = application.getSharedPreferences("state", Context.MODE_PRIVATE)
    private var _isCameraDisabled by mutableStateOf(sharedPreferences.getBoolean("is_camera_disabled", false))
    val isCameraDisabled: Boolean get() = _isCameraDisabled

    fun setCameraDisabled(disabled: Boolean) {
        _isCameraDisabled = disabled
        sharedPreferences.edit().putBoolean("is_camera_disabled", disabled).apply()
    }

    fun toggle() {
        setCameraDisabled(!isCameraDisabled)
    }
}