package com.yalantis.ucrop.callback

import android.graphics.Bitmap
import com.yalantis.ucrop.model.ExifInfo
import android.graphics.RectF

/**
 * Interface for crop bound change notifying.
 */
interface CropBoundsChangeListener {
    fun onCropAspectRatioChanged(cropRatio: Float)
}