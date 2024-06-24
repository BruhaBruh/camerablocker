package ru.bruhabruh.camerablocker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.bruhabruh.camerablocker.ui.theme.CameraBlockerTheme
import androidx.lifecycle.viewmodel.compose.viewModel

@Suppress("DEPRECATION")
@SuppressLint("ObsoleteSdkInt")
class MainActivity : ComponentActivity() {
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 2
        private const val BOOT_PERMISSION_REQUEST_CODE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, CameraMonitorService::class.java)
        startService(serviceIntent)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("CameraPermission", "Requesting permission")
            requestCameraPermission()
        } else {
            Log.d("CameraPermission", "Permission is already granted")
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECEIVE_BOOT_COMPLETED
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("BootPermission", "Requesting permission")
            requestBootPermission()
        } else {
            Log.d("BootPermission", "Permission is already granted")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && !Settings.canDrawOverlays(
                this
            )
        ) {
            Log.d("OverlayPermission", "Requesting permission")
            requestOverlayPermission()
        } else {
            Log.d("OverlayPermission", "Permission is already granted")
        }

        setContent {
            CameraBlockerTheme {
                val viewModel: IsCameraDisabledViewModel = viewModel(
                    factory = IsCameraDisabledViewModelFactory(application)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color =
                            if (viewModel.isCameraDisabled)
                                MaterialTheme.colorScheme.inverseSurface
                            else
                                MaterialTheme.colorScheme.surface
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { viewModel.toggle() },
                        modifier = Modifier.height(64.dp)
                    ) {
                        Text(
                            text = if (viewModel.isCameraDisabled) "Включить камеру" else "Выключить камеру",
                            fontSize = 24.sp,
                        )
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("CameraPermission", "Permission is granted")
                } else {
                    Log.d("CameraPermission", "Permission is denied, requesting again")
                    requestCameraPermission()
                }
            }
            BOOT_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("BootPermission", "Permission is granted")
                } else {
                    Log.d("BootPermission", "Permission is denied, requesting again")
                    requestBootPermission()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            OVERLAY_PERMISSION_REQUEST_CODE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && Settings.canDrawOverlays(this)) {
                    Log.d("OverlayPermission", "Permission is granted")
                } else {
                    Log.d("OverlayPermission", "Permission is denied, requesting again")
                    requestOverlayPermission()
                }
            }
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestBootPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.RECEIVE_BOOT_COMPLETED),
            BOOT_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }
}