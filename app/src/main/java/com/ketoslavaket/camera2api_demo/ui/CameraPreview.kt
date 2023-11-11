package com.ketoslavaket.camera2api_demo.ui


import com.ketoslavaket.camera2api_demo.domain.StandardImage
import com.ketoslavaket.camera2api_demo.presentation.ModelData
import com.ketoslavaket.camera2api_demo.presentation.UiEventHandler

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap


class CameraPreview (
    private var mainActivity: Activity,
    private var modelData: ModelData,
    private var uiEventHandler: UiEventHandler,
    private val uiScale: Float,
    private val screenResolution: Pair<Int, Int>,
    private var generalComponents: GeneralComponents
) {

    @Composable
    fun CameraPreviewPage() {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                val previewImage = modelData.cameraPreviewImage.collectAsState().value
                if (previewImage != null) {
                    StandardImageViewer(previewImage)
                }
            }
        }
    }


    @Composable
    fun StandardImageViewer(image: StandardImage) {

        val imageBitmap = image.getRotatedToNormalImage().getImageAsBitmap().asImageBitmap()

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}