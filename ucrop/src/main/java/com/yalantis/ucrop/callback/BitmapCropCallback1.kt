package com.yalantis.ucrop.callback

import android.graphics.Bitmap
import com.yalantis.ucrop.model.ExifInfo
import android.graphics.RectF
import android.net.Uri

interface BitmapCropCallback {
    fun onBitmapCropped(
        resultUri: Uri,
        offsetX: Int,
        offsetY: Int,
        imageWidth: Int,
        imageHeight: Int
    )

    fun onCropFailure(t: Throwable)
}