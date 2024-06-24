package ru.bruhabruh.camerablocker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CameraMonitorApp()
        }
    }

    @Composable
    fun CameraMonitorApp() {
        val context = LocalContext.current
        var hasPermissions by remember { mutableStateOf(false) }

        val multiplePermissionsLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            hasPermissions = permissions.values.all { it }
        }

        LaunchedEffect(Unit) {
            val permissionsToRequest = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.FOREGROUND_SERVICE_CAMERA,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SYSTEM_ALERT_WINDOW
            )
            val permissionsGranted = permissionsToRequest.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
            if (!permissionsGranted) {
                multiplePermissionsLauncher.launch(permissionsToRequest)
            } else {
                hasPermissions = true
                startCameraService(context)
            }
        }

        MaterialTheme {
            if (hasPermissions) {
                ButtonScreen()
            } else {
                NoPermissionsScreen()
            }
        }
    }

    private fun startCameraService(context: Context) {
        val serviceIntent = Intent(context, CameraMonitorService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    @Composable
    fun ButtonScreen() {
        val viewModel: CameraViewModel = viewModel(
            factory = CameraViewModelFactory(application)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color =
                    if (viewModel.isCameraDisabled)
                        MaterialTheme.colorScheme.inverseSurface
                    else
                        MaterialTheme.colorScheme.surface
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
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
                Text(
                    text = if (viewModel.isCameraDisabled) "Камера выключена" else "Камера включена",
                    color = if (viewModel.isCameraDisabled)
                        MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.85f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    fontSize = 16.sp,
                )
            }
        }
    }

    @Composable
    fun NoPermissionsScreen() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Нет всех необходимых разрешений", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        }
    }
}
