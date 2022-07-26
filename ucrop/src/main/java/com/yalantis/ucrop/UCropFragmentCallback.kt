package com.yalantis.ucrop

import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.IntDef
import com.yalantis.ucrop.UCropActivity
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.yalantis.ucrop.view.UCropView
import com.yalantis.ucrop.view.GestureCropImageView
import com.yalantis.ucrop.view.OverlayView
import android.view.ViewGroup
import android.widget.TextView
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatDelegate
import android.os.Bundle
import com.yalantis.ucrop.R
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Animatable
import androidx.core.content.ContextCompat
import com.yalantis.ucrop.UCrop
import android.text.TextUtils
import com.yalantis.ucrop.view.CropImageView
import com.yalantis.ucrop.model.AspectRatio
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.yalantis.ucrop.view.TransformImageView.TransformImageListener
import android.view.animation.AccelerateInterpolator
import com.yalantis.ucrop.util.SelectedStateListDrawable
import android.annotation.TargetApi
import android.os.Build
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.FrameLayout
import com.yalantis.ucrop.view.widget.AspectRatioTextView
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView
import com.yalantis.ucrop.view.widget.HorizontalProgressWheelView.ScrollingListener
import androidx.annotation.IdRes
import com.yalantis.ucrop.callback.BitmapCropCallback
import android.app.Activity
import kotlin.jvm.JvmOverloads
import com.yalantis.ucrop.UCropFragment
import androidx.annotation.FloatRange
import android.os.Parcelable
import com.yalantis.ucrop.UCropFragmentCallback
import com.yalantis.ucrop.UCropFragment.UCropResult

interface UCropFragmentCallback {
    /**
     * Return loader status
     * @param showLoader
     */
    fun loadingProgress(showLoader: Boolean)

    /**
     * Return cropping result or error
     * @param result
     */
    fun onCropFinish(result: UCropResult?)
}