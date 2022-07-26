package com.yalantis.ucrop.model

import android.graphics.Bitmap
import com.yalantis.ucrop.model.ImageState
import com.yalantis.ucrop.model.CropParameters
import com.yalantis.ucrop.callback.BitmapCropCallback
import android.os.AsyncTask
import kotlin.Throws
import android.graphics.RectF
import android.graphics.Bitmap.CompressFormat
import com.yalantis.ucrop.model.ExifInfo
import android.graphics.BitmapFactory
import com.yalantis.ucrop.task.BitmapCropTask
import com.yalantis.ucrop.util.ImageHeaderParser
import com.yalantis.ucrop.callback.BitmapLoadCallback
import com.yalantis.ucrop.task.BitmapLoadTask.BitmapWorkerResult
import com.yalantis.ucrop.util.BitmapLoadUtils
import com.yalantis.ucrop.task.BitmapLoadTask
import okhttp3.OkHttpClient
import com.yalantis.ucrop.UCropHttpClientStore
import okio.BufferedSource
import okio.Okio
import android.os.Build
import android.annotation.TargetApi
import android.opengl.EGL14
import android.opengl.GLES20
import javax.microedition.khronos.egl.EGL10
import android.opengl.GLES10
import android.annotation.SuppressLint
import android.provider.DocumentsContract
import android.text.TextUtils
import android.content.ContentUris
import android.provider.MediaStore
import android.view.WindowManager
import android.view.Display
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import com.yalantis.ucrop.util.RotationGestureDetector.OnRotationGestureListener
import android.view.MotionEvent
import android.graphics.PorterDuff
import android.os.Parcelable
import android.os.Parcel
import com.yalantis.ucrop.model.AspectRatio

/**
 * Created by Oleksii Shliama [https://github.com/shliama] on 6/24/16.
 */
class AspectRatio : Parcelable {
    val aspectRatioTitle: String?
    val aspectRatioX: Float
    val aspectRatioY: Float

    constructor(aspectRatioTitle: String?, aspectRatioX: Float, aspectRatioY: Float) {
        this.aspectRatioTitle = aspectRatioTitle
        this.aspectRatioX = aspectRatioX
        this.aspectRatioY = aspectRatioY
    }

    protected constructor(`in`: Parcel) {
        aspectRatioTitle = `in`.readString()
        aspectRatioX = `in`.readFloat()
        aspectRatioY = `in`.readFloat()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(aspectRatioTitle)
        dest.writeFloat(aspectRatioX)
        dest.writeFloat(aspectRatioY)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AspectRatio?> = object : Parcelable.Creator<AspectRatio?> {
            override fun createFromParcel(`in`: Parcel): AspectRatio? {
                return AspectRatio(`in`)
            }

            override fun newArray(size: Int): Array<AspectRatio?> {
                return arrayOfNulls(size)
            }
        }
    }
}