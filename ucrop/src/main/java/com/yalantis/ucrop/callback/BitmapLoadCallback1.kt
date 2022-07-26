package com.yalantis.ucrop.callback

import android.graphics.Bitmap
import com.yalantis.ucrop.model.ExifInfo
import android.graphics.RectF
import java.lang.Exception

interface BitmapLoadCallback {
    fun onBitmapLoaded(
        bitmap: Bitmap,
        exifInfo: ExifInfo,
        imageInputPath: String,
        imageOutputPath: String?
    )

    fun onFailure(bitmapWorkerException: Exception)
}