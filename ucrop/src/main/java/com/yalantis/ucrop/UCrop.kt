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
import android.app.Fragment
import android.content.Context
import android.net.Uri
import kotlin.jvm.JvmOverloads
import com.yalantis.ucrop.UCropFragment
import androidx.annotation.FloatRange
import android.os.Parcelable
import androidx.annotation.IntRange
import com.yalantis.ucrop.UCropFragmentCallback
import com.yalantis.ucrop.UCropFragment.UCropResult
import java.util.*

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 *
 *
 * Builder class to ease Intent setup.
 */
class UCrop private constructor(source: Uri, destination: Uri) {
    private val mCropIntent: Intent
    private var mCropOptionsBundle: Bundle

    /**
     * Set an aspect ratio for crop bounds.
     * User won't see the menu with other ratios options.
     *
     * @param x aspect ratio X
     * @param y aspect ratio Y
     */
    fun withAspectRatio(x: Float, y: Float): UCrop {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, x)
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y)
        return this
    }

    /**
     * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
     * User won't see the menu with other ratios options.
     */
    fun useSourceImageAspectRatio(): UCrop {
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0f)
        mCropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0f)
        return this
    }

    /**
     * Set maximum size for result cropped image. Maximum size cannot be less then {@value MIN_SIZE}
     *
     * @param width  max cropped image width
     * @param height max cropped image height
     */
    fun withMaxResultSize(
        @IntRange(from = MIN_SIZE.toLong()) width: Int,
        @IntRange(from = MIN_SIZE.toLong()) height: Int
    ): UCrop {
        var width = width
        var height = height
        if (width < MIN_SIZE) {
            width = MIN_SIZE
        }
        if (height < MIN_SIZE) {
            height = MIN_SIZE
        }
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_X, width)
        mCropOptionsBundle.putInt(EXTRA_MAX_SIZE_Y, height)
        return this
    }

    fun withOptions(options: Options): UCrop {
        mCropOptionsBundle.putAll(options.optionBundle)
        return this
    }
    /**
     * Send the crop Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    /**
     * Send the crop Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    @JvmOverloads
    fun start(activity: Activity, requestCode: Int = REQUEST_CROP) {
        activity.startActivityForResult(getIntent(activity), requestCode)
    }

    /**
     * Send the crop Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    fun start(context: Context, fragment: Fragment) {
        start(context, fragment, REQUEST_CROP)
    }

    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun start(context: Context, fragment: Fragment, requestCode: Int) {
        fragment.startActivityForResult(getIntent(context), requestCode)
    }
    /**
     * Send the crop Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    /**
     * Send the crop Intent from a support library Fragment
     *
     * @param fragment Fragment to receive result
     */
    @JvmOverloads
    fun start(
        context: Context,
        fragment: androidx.fragment.app.Fragment,
        requestCode: Int = REQUEST_CROP
    ) {
        fragment.startActivityForResult(getIntent(context), requestCode)
    }

    /**
     * Get Intent to start [UCropActivity]
     *
     * @return Intent for [UCropActivity]
     */
    fun getIntent(context: Context): Intent {
        mCropIntent.setClass(context, UCropActivity::class.java)
        mCropIntent.putExtras(mCropOptionsBundle)
        return mCropIntent
    }

    /**
     * Get Fragment [UCropFragment]
     *
     * @return Fragment of [UCropFragment]
     */
    val fragment: UCropFragment
        get() = UCropFragment.Companion.newInstance(mCropOptionsBundle)

    fun getFragment(bundle: Bundle): UCropFragment {
        mCropOptionsBundle = bundle
        return fragment
    }

    /**
     * Class that helps to setup advanced configs that are not commonly used.
     * Use it with method [.withOptions]
     */
    class Options {
        val optionBundle: Bundle

        /**
         * Set one of [android.graphics.Bitmap.CompressFormat] that will be used to save resulting Bitmap.
         */
        fun setCompressionFormat(format: CompressFormat) {
            optionBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, format.name)
        }

        /**
         * Set compression quality [0-100] that will be used to save resulting Bitmap.
         */
        fun setCompressionQuality(@IntRange(from = 0) compressQuality: Int) {
            optionBundle.putInt(EXTRA_COMPRESSION_QUALITY, compressQuality)
        }

        /**
         * Choose what set of gestures will be enabled on each tab - if any.
         */
        fun setAllowedGestures(
            @UCropActivity.GestureTypes tabScale: Int,
            @UCropActivity.GestureTypes tabRotate: Int,
            @UCropActivity.GestureTypes tabAspectRatio: Int
        ) {
            optionBundle.putIntArray(EXTRA_ALLOWED_GESTURES,
                intArrayOf(tabScale, tabRotate, tabAspectRatio))
        }

        /**
         * This method sets multiplier that is used to calculate max image scale from min image scale.
         *
         * @param maxScaleMultiplier - (minScale * maxScaleMultiplier) = maxScale
         */
        fun setMaxScaleMultiplier(
            @FloatRange(from = 1.0,
                fromInclusive = false) maxScaleMultiplier: Float
        ) {
            optionBundle.putFloat(EXTRA_MAX_SCALE_MULTIPLIER, maxScaleMultiplier)
        }

        /**
         * This method sets animation duration for image to wrap the crop bounds
         *
         * @param durationMillis - duration in milliseconds
         */
        fun setImageToCropBoundsAnimDuration(@IntRange(from = MIN_SIZE.toLong()) durationMillis: Int) {
            optionBundle.putInt(EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, durationMillis)
        }

        /**
         * Setter for max size for both width and height of bitmap that will be decoded from an input Uri and used in the view.
         *
         * @param maxBitmapSize - size in pixels
         */
        fun setMaxBitmapSize(@IntRange(from = MIN_SIZE.toLong()) maxBitmapSize: Int) {
            optionBundle.putInt(EXTRA_MAX_BITMAP_SIZE, maxBitmapSize)
        }

        /**
         * @param color - desired color of dimmed area around the crop bounds
         */
        fun setDimmedLayerColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_DIMMED_LAYER_COLOR, color)
        }

        /**
         * @param isCircle - set it to true if you want dimmed layer to have an circle inside
         */
        fun setCircleDimmedLayer(isCircle: Boolean) {
            optionBundle.putBoolean(EXTRA_CIRCLE_DIMMED_LAYER, isCircle)
        }

        /**
         * @param show - set to true if you want to see a crop frame rectangle on top of an image
         */
        fun setShowCropFrame(show: Boolean) {
            optionBundle.putBoolean(EXTRA_SHOW_CROP_FRAME, show)
        }

        /**
         * @param color - desired color of crop frame
         */
        fun setCropFrameColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_CROP_FRAME_COLOR, color)
        }

        /**
         * @param width - desired width of crop frame line in pixels
         */
        fun setCropFrameStrokeWidth(@IntRange(from = 0) width: Int) {
            optionBundle.putInt(EXTRA_CROP_FRAME_STROKE_WIDTH, width)
        }

        /**
         * @param show - set to true if you want to see a crop grid/guidelines on top of an image
         */
        fun setShowCropGrid(show: Boolean) {
            optionBundle.putBoolean(EXTRA_SHOW_CROP_GRID, show)
        }

        /**
         * @param count - crop grid rows count.
         */
        fun setCropGridRowCount(@IntRange(from = 0) count: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_ROW_COUNT, count)
        }

        /**
         * @param count - crop grid columns count.
         */
        fun setCropGridColumnCount(@IntRange(from = 0) count: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_COLUMN_COUNT, count)
        }

        /**
         * @param color - desired color of crop grid/guidelines
         */
        fun setCropGridColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_COLOR, color)
        }

        /**
         * @param color - desired color of crop grid/guidelines corner
         */
        fun setCropGridCornerColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_CORNER_COLOR, color)
        }

        /**
         * @param width - desired width of crop grid lines in pixels
         */
        fun setCropGridStrokeWidth(@IntRange(from = 0) width: Int) {
            optionBundle.putInt(EXTRA_CROP_GRID_STROKE_WIDTH, width)
        }

        /**
         * @param color - desired resolved color of the toolbar
         */
        fun setToolbarColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_TOOL_BAR_COLOR, color)
        }

        /**
         * @param color - desired resolved color of the statusbar
         */
        fun setStatusBarColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_STATUS_BAR_COLOR, color)
        }

        /**
         * @param color - desired resolved color of the active and selected widget and progress wheel middle line (default is white)
         */
        fun setActiveControlsWidgetColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE, color)
        }

        /**
         * @param color - desired resolved color of Toolbar text and buttons (default is darker orange)
         */
        fun setToolbarWidgetColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, color)
        }

        /**
         * @param text - desired text for Toolbar title
         */
        fun setToolbarTitle(text: String?) {
            optionBundle.putString(EXTRA_UCROP_TITLE_TEXT_TOOLBAR, text)
        }

        /**
         * @param drawable - desired drawable for the Toolbar left cancel icon
         */
        fun setToolbarCancelDrawable(@DrawableRes drawable: Int) {
            optionBundle.putInt(EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, drawable)
        }

        /**
         * @param drawable - desired drawable for the Toolbar right crop icon
         */
        fun setToolbarCropDrawable(@DrawableRes drawable: Int) {
            optionBundle.putInt(EXTRA_UCROP_WIDGET_CROP_DRAWABLE, drawable)
        }

        /**
         * @param color - desired resolved color of logo fill (default is darker grey)
         */
        fun setLogoColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_LOGO_COLOR, color)
        }

        /**
         * @param hide - set to true to hide the bottom controls (shown by default)
         */
        fun setHideBottomControls(hide: Boolean) {
            optionBundle.putBoolean(EXTRA_HIDE_BOTTOM_CONTROLS, hide)
        }

        /**
         * @param enabled - set to true to let user resize crop bounds (disabled by default)
         */
        fun setFreeStyleCropEnabled(enabled: Boolean) {
            optionBundle.putBoolean(EXTRA_FREE_STYLE_CROP, enabled)
        }

        /**
         * Pass an ordered list of desired aspect ratios that should be available for a user.
         *
         * @param selectedByDefault - index of aspect ratio option that is selected by default (starts with 0).
         * @param aspectRatio       - list of aspect ratio options that are available to user
         */
        fun setAspectRatioOptions(selectedByDefault: Int, vararg aspectRatio: AspectRatio?) {
            require(selectedByDefault < aspectRatio.size) {
                String.format(Locale.US,
                    "Index [selectedByDefault = %d] (0-based) cannot be higher or equal than aspect ratio options count [count = %d].",
                    selectedByDefault, aspectRatio.size)
            }
            optionBundle.putInt(EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, selectedByDefault)
            optionBundle.putParcelableArrayList(EXTRA_ASPECT_RATIO_OPTIONS, ArrayList<Parcelable>(
                Arrays.asList(*aspectRatio)))
        }

        /**
         * @param color - desired background color that should be applied to the root view
         */
        fun setRootViewBackgroundColor(@ColorInt color: Int) {
            optionBundle.putInt(EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, color)
        }

        /**
         * Set an aspect ratio for crop bounds.
         * User won't see the menu with other ratios options.
         *
         * @param x aspect ratio X
         * @param y aspect ratio Y
         */
        fun withAspectRatio(x: Float, y: Float) {
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_X, x)
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, y)
        }

        /**
         * Set an aspect ratio for crop bounds that is evaluated from source image width and height.
         * User won't see the menu with other ratios options.
         */
        fun useSourceImageAspectRatio() {
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_X, 0f)
            optionBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 0f)
        }

        /**
         * Set maximum size for result cropped image.
         *
         * @param width  max cropped image width
         * @param height max cropped image height
         */
        fun withMaxResultSize(
            @IntRange(from = MIN_SIZE.toLong()) width: Int,
            @IntRange(from = MIN_SIZE.toLong()) height: Int
        ) {
            optionBundle.putInt(EXTRA_MAX_SIZE_X, width)
            optionBundle.putInt(EXTRA_MAX_SIZE_Y, height)
        }

        companion object {
            val EXTRA_COMPRESSION_FORMAT_NAME = EXTRA_PREFIX + ".CompressionFormatName"
            val EXTRA_COMPRESSION_QUALITY = EXTRA_PREFIX + ".CompressionQuality"
            val EXTRA_ALLOWED_GESTURES = EXTRA_PREFIX + ".AllowedGestures"
            val EXTRA_MAX_BITMAP_SIZE = EXTRA_PREFIX + ".MaxBitmapSize"
            val EXTRA_MAX_SCALE_MULTIPLIER = EXTRA_PREFIX + ".MaxScaleMultiplier"
            val EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION =
                EXTRA_PREFIX + ".ImageToCropBoundsAnimDuration"
            val EXTRA_DIMMED_LAYER_COLOR = EXTRA_PREFIX + ".DimmedLayerColor"
            val EXTRA_CIRCLE_DIMMED_LAYER = EXTRA_PREFIX + ".CircleDimmedLayer"
            val EXTRA_SHOW_CROP_FRAME = EXTRA_PREFIX + ".ShowCropFrame"
            val EXTRA_CROP_FRAME_COLOR = EXTRA_PREFIX + ".CropFrameColor"
            val EXTRA_CROP_FRAME_STROKE_WIDTH = EXTRA_PREFIX + ".CropFrameStrokeWidth"
            val EXTRA_SHOW_CROP_GRID = EXTRA_PREFIX + ".ShowCropGrid"
            val EXTRA_CROP_GRID_ROW_COUNT = EXTRA_PREFIX + ".CropGridRowCount"
            val EXTRA_CROP_GRID_COLUMN_COUNT = EXTRA_PREFIX + ".CropGridColumnCount"
            val EXTRA_CROP_GRID_COLOR = EXTRA_PREFIX + ".CropGridColor"
            val EXTRA_CROP_GRID_CORNER_COLOR = EXTRA_PREFIX + ".CropGridCornerColor"
            val EXTRA_CROP_GRID_STROKE_WIDTH = EXTRA_PREFIX + ".CropGridStrokeWidth"
            @JvmField
            val EXTRA_TOOL_BAR_COLOR = EXTRA_PREFIX + ".ToolbarColor"
            @JvmField
            val EXTRA_STATUS_BAR_COLOR = EXTRA_PREFIX + ".StatusBarColor"
            val EXTRA_UCROP_COLOR_CONTROLS_WIDGET_ACTIVE =
                EXTRA_PREFIX + ".UcropColorControlsWidgetActive"
            @JvmField
            val EXTRA_UCROP_WIDGET_COLOR_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarWidgetColor"
            @JvmField
            val EXTRA_UCROP_TITLE_TEXT_TOOLBAR = EXTRA_PREFIX + ".UcropToolbarTitleText"
            @JvmField
            val EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCancelDrawable"
            @JvmField
            val EXTRA_UCROP_WIDGET_CROP_DRAWABLE = EXTRA_PREFIX + ".UcropToolbarCropDrawable"
            val EXTRA_UCROP_LOGO_COLOR = EXTRA_PREFIX + ".UcropLogoColor"
            val EXTRA_HIDE_BOTTOM_CONTROLS = EXTRA_PREFIX + ".HideBottomControls"
            val EXTRA_FREE_STYLE_CROP = EXTRA_PREFIX + ".FreeStyleCrop"
            val EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT =
                EXTRA_PREFIX + ".AspectRatioSelectedByDefault"
            val EXTRA_ASPECT_RATIO_OPTIONS = EXTRA_PREFIX + ".AspectRatioOptions"
            val EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR =
                EXTRA_PREFIX + ".UcropRootViewBackgroundColor"
        }

        init {
            optionBundle = Bundle()
        }
    }

    companion object {
        const val REQUEST_CROP = 69
        const val RESULT_ERROR = 96
        const val MIN_SIZE = 10
        private val EXTRA_PREFIX: String = "crop"
        val EXTRA_INPUT_URI = EXTRA_PREFIX + ".InputUri"
        val EXTRA_OUTPUT_URI = EXTRA_PREFIX + ".OutputUri"
        val EXTRA_OUTPUT_CROP_ASPECT_RATIO = EXTRA_PREFIX + ".CropAspectRatio"
        val EXTRA_OUTPUT_IMAGE_WIDTH = EXTRA_PREFIX + ".ImageWidth"
        val EXTRA_OUTPUT_IMAGE_HEIGHT = EXTRA_PREFIX + ".ImageHeight"
        val EXTRA_OUTPUT_OFFSET_X = EXTRA_PREFIX + ".OffsetX"
        val EXTRA_OUTPUT_OFFSET_Y = EXTRA_PREFIX + ".OffsetY"
        val EXTRA_ERROR = EXTRA_PREFIX + ".Error"
        val EXTRA_ASPECT_RATIO_X = EXTRA_PREFIX + ".AspectRatioX"
        val EXTRA_ASPECT_RATIO_Y = EXTRA_PREFIX + ".AspectRatioY"
        val EXTRA_MAX_SIZE_X = EXTRA_PREFIX + ".MaxSizeX"
        val EXTRA_MAX_SIZE_Y = EXTRA_PREFIX + ".MaxSizeY"

        /**
         * This method creates new Intent builder and sets both source and destination image URIs.
         *
         * @param source      Uri for image to crop
         * @param destination Uri for saving the cropped image
         */
        @JvmStatic
        fun of(source: Uri, destination: Uri): UCrop {
            return UCrop(source, destination)
        }

        /**
         * Retrieve cropped image Uri from the result Intent
         *
         * @param intent crop result intent
         */
        @JvmStatic
        fun getOutput(intent: Intent): Uri? {
            return intent.getParcelableExtra(EXTRA_OUTPUT_URI)
        }

        /**
         * Retrieve the width of the cropped image
         *
         * @param intent crop result intent
         */
        fun getOutputImageWidth(intent: Intent): Int {
            return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_WIDTH, -1)
        }

        /**
         * Retrieve the height of the cropped image
         *
         * @param intent crop result intent
         */
        fun getOutputImageHeight(intent: Intent): Int {
            return intent.getIntExtra(EXTRA_OUTPUT_IMAGE_HEIGHT, -1)
        }

        /**
         * Retrieve cropped image aspect ratio from the result Intent
         *
         * @param intent crop result intent
         * @return aspect ratio as a floating point value (x:y) - so it will be 1 for 1:1 or 4/3 for 4:3
         */
        fun getOutputCropAspectRatio(intent: Intent): Float {
            return intent.getFloatExtra(EXTRA_OUTPUT_CROP_ASPECT_RATIO, 0f)
        }

        /**
         * Method retrieves error from the result intent.
         *
         * @param result crop result Intent
         * @return Throwable that could happen while image processing
         */
        @JvmStatic
        fun getError(result: Intent): Throwable? {
            return result.getSerializableExtra(EXTRA_ERROR) as Throwable?
        }
    }

    init {
        mCropIntent = Intent()
        mCropOptionsBundle = Bundle()
        mCropOptionsBundle.putParcelable(EXTRA_INPUT_URI, source)
        mCropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, destination)
    }
}