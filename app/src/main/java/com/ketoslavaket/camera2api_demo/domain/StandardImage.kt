package com.ketoslavaket.camera2api_demo.domain

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Size
import java.io.ByteArrayOutputStream


class StandardImage (
    private val imageBitmap: Bitmap,
    private val rotationRelativeToNormal: Float
) {


    // Public


    fun getImageAsBitmap(): Bitmap { return imageBitmap }
    fun getImageResolution(): Size { return Size(imageBitmap.width, imageBitmap.height) }
    fun getRotationRelativeToNormal(): Float { return rotationRelativeToNormal }


    fun getImageAsByteArray(): ByteArray {

        // Translate settings
        val format = Bitmap.CompressFormat.JPEG
        val quality = 100

        // translate Bitmap to ByteArray and return
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(format, quality, outputStream)
        return outputStream.toByteArray()
    }


    fun cropImageByNewAspectRatio(newAspectRatio: Float): StandardImage {

        // get current aspect ratio
        val originalAspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()

        // new image resolution
        val newWidth: Int
        val newHeight: Int

        if (originalAspectRatio > newAspectRatio) {
            newWidth = (imageBitmap.height * newAspectRatio).toInt()
            newHeight = imageBitmap.height
        } else {
            newWidth = imageBitmap.width
            newHeight = (imageBitmap.width / newAspectRatio).toInt()
        }

        // calc crop start point
        val startX = (imageBitmap.width - newWidth) / 2
        val startY = (imageBitmap.height - newHeight) / 2

        // create new cropped image
        val croppedBitmap = Bitmap.createBitmap(
            imageBitmap, startX, startY, newWidth, newHeight)

        return StandardImage(croppedBitmap, rotationRelativeToNormal)
    }


    fun getRotatedToNormalImage(): StandardImage {
        return StandardImage(
            rotateBitmap(imageBitmap, rotationRelativeToNormal), 0F)
    }


    // Private


    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, true)
    }
}