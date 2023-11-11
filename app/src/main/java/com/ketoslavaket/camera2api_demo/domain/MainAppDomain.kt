package com.ketoslavaket.camera2api_demo.domain


import com.ketoslavaket.camera2api_demo.data.PermissionsController
import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.data.CameraConfiguration
import com.ketoslavaket.camera2api_demo.data.CameraController
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log


class MainAppDomain (private var mainActivity: Activity, private val modelData: ModelData,
                     private val uiEventHandler: UiEventHandler) {


    // Settings


    companion object {
        const val LOG_TAG = "myLogs"
    }


    // Variables


    // Components
    private var cameraController: CameraController? = null
    private val permissionsController = PermissionsController(mainActivity)
    // Domains
    private val setupDomain = SetupDomain(
        mainActivity, modelData, uiEventHandler, permissionsController)
    private var cameraDomain: CameraDomain? = null
    // Camera configurations
    private var cameraConfigurations: List<CameraConfiguration> = arrayListOf()


    // Public


    fun start() {
        Log.i(LOG_TAG, "DomainController, Start")
        startSetupState()
    }


    // Provide callbacks
    fun onNewActivityState(newState: String) {
        cameraDomain?.onNewActivityState(newState)
    }
    fun requestPermissionsResultCallback(requestCode: Int, permissions: Array<String>,
                                         grantResults: IntArray) {
        // Process in permissionsController
        permissionsController.requestPermissionsResultCallback(requestCode,
            permissions, grantResults)
    }


    // Private


    // Model data shortcuts

    private fun openCameraPage() {
        modelData.openCameraPreview()
        modelData.openCameraControl()
    }


    // Start setup state after start
    private fun startSetupState() {
        setupDomain.startSetup { result ->
            if (result) {
                startCameraDomain()
            } else {
                Log.e(LOG_TAG, "WRITE: DomainController, startSetupState(), false")
            }
        }
    }


    // Start main state after setup
    private fun startCameraDomain() {
        // Open Ui pages
        openCameraPage()

        // Start camera service
        waitAndSetupCameraController {

            // Create capture domain
            cameraDomain = CameraDomain(
                mainActivity,
                modelData,
                uiEventHandler,
                cameraController!!,
                cameraConfigurations[0]
            )

            // Start capture session
            cameraDomain?.startCameraDomain(cameraConfigurations)
        }
    }


    // Setup camera controller

    private val handler = Handler(Looper.getMainLooper())
    private fun waitAndSetupCameraController(setupCallback: (status: Boolean) -> Unit) {
        handler.postDelayed({ setupCameraController(setupCallback) }, 250)
    }
    private fun setupCameraController(setupCallback: (status: Boolean) -> Unit) {
        // Create camera controller and setup cameras
        cameraController = CameraController(mainActivity)
        cameraController!!.setupCameraController { newCameraCharacteristicsList ->
            cameraConfigurations = newCameraCharacteristicsList
            setupCallback(true)
        }
    }
}