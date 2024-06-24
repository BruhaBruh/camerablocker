package ru.bruhabruh.camerablocker

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.os.IBinder
import android.util.Log

class CameraMonitorService : Service() {
    private lateinit var cameraManager: CameraManager
    private val isCameraDisabled: Boolean
        get() = getSharedPreferences("state", Context.MODE_PRIVATE).getBoolean("is_camera_disabled", false)

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        bindCameraCallback()

        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        bindCameraCallback()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun bindCameraCallback() {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        val cameraAvailabilityCallback = object : CameraManager.AvailabilityCallback() {
            override fun onCameraAvailable(cameraId: String) {
                super.onCameraAvailable(cameraId)
                Log.d("CameraState", "Camera $cameraId is available")
            }

            override fun onCameraUnavailable(cameraId: String) {
                super.onCameraUnavailable(cameraId)
                Log.d("CameraState", "Camera $cameraId is unavailable")

                if (isCameraDisabled) {
                    val intent = Intent(this@CameraMonitorService, OverlayActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }

        cameraManager.registerAvailabilityCallback(cameraAvailabilityCallback, null)
    }
}