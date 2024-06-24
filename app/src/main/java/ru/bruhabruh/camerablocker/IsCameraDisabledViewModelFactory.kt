package ru.bruhabruh.camerablocker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class IsCameraDisabledViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IsCameraDisabledViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IsCameraDisabledViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}