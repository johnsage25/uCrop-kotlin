package com.yalantis.ucrop.callback

import android.graphics.Bitmap
import com.yalantis.ucrop.model.ExifInfo
import android.graphics.RectF

/**
 * Created by Oleksii Shliama.
 */
interface OverlayViewChangeListener {
    fun onCropRectUpdated(cropRect: RectF?)
}