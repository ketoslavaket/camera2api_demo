package com.ketoslavaket.camera2api_demo.data


import com.ketoslavaket.camera2api_demo.domain.StandardImage

import android.app.Activity
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.media.Image
import android.util.Size
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer


class DataFilesController (private val mainActivity: Activity) {


    // Public


    // Save image
    fun saveImage(image: StandardImage) {
        saveImageAsByteArray(image.getImageAsByteArray())
    }
    fun saveImageAsByteArray(image: ByteArray) {

        // Define folder name
        val folderName = "camera2api_demo"

        // Create a File object for the directory
        val directory = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM), folderName)

        // Check if the directory exists, and create it if it doesn't
        if (!directory.exists()) {
            directory.mkdirs()
        }

        // Create file as object
        val file = File(directory, "C2AD_IMG_${System.currentTimeMillis()}.jpg")

        // Open data stream
        val fileOutputStream = FileOutputStream(file)

        // Write data and close stream
        try {
            fileOutputStream.write(image)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileOutputStream.close()
        }

        // Info
        Toast.makeText(mainActivity, "Photo saved", Toast.LENGTH_SHORT).show()
    }
}