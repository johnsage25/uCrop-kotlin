package com.yalantis.ucrop.view

import android.content.Context
import kotlin.jvm.JvmOverloads
import android.widget.FrameLayout
import com.yalantis.ucrop.callback.OverlayViewChangeListener
import android.graphics.RectF
import android.view.LayoutInflater
import com.yalantis.ucrop.R
import android.util.AttributeSet
import com.yalantis.ucrop.callback.CropBoundsChangeListener

class UCropView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    var cropImageView: GestureCropImageView
        private set
    val overlayView: OverlayView
    private fun setListenersToViews() {
        cropImageView.cropBoundsChangeListener =
            object : CropBoundsChangeListener {
                override fun onCropAspectRatioChanged(cropRatio: Float) {
                    overlayView.setTargetAspectRatio(cropRatio)
                }
            }
        overlayView.overlayViewChangeListener =
            object : OverlayViewChangeListener {
                override fun onCropRectUpdated(cropRect: RectF?) {
                    if (cropRect != null) {
                        cropImageView.setCropRect(cropRect)
                    }
                }
            }
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    /**
     * Method for reset state for UCropImageView such as rotation, scale, translation.
     * Be careful: this method recreate UCropImageView instance and reattach it to layout.
     */
    fun resetCropImageView() {
        removeView(cropImageView)
        cropImageView = GestureCropImageView(context)
        setListenersToViews()
        cropImageView.setCropRect(overlayView.cropViewRect)
        addView(cropImageView, 0)

    }

    init {
        LayoutInflater.from(context).inflate(R.layout.ucrop_view, this, true)
        cropImageView = findViewById(R.id.image_view_crop)
        overlayView = findViewById(R.id.view_overlay)
        val a = context.obtainStyledAttributes(attrs, R.styleable.ucrop_UCropView)
        overlayView.processStyledAttributes(a)
        cropImageView.processStyledAttributes(a)
        a.recycle()
        setListenersToViews()
    }
}