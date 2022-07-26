package com.yalantis.ucrop.sample

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.yalantis.ucrop.sample.R
import android.content.DialogInterface
import com.yalantis.ucrop.sample.BaseActivity
import android.os.Bundle
import com.yalantis.ucrop.view.UCropView
import com.yalantis.ucrop.sample.ResultActivity
import android.widget.Toast
import android.graphics.BitmapFactory
import android.content.pm.PackageManager
import kotlin.Throws
import android.content.Intent
import androidx.core.content.FileProvider
import android.content.pm.ResolveInfo
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.os.Build
import android.app.PendingIntent
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*

/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
class ResultActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val uri = intent.data
        if (uri != null) {
            try {
                val uCropView = findViewById<UCropView>(R.id.ucrop)
                uCropView.cropImageView.setImageUri(uri, null)
                uCropView.overlayView.setShowCropFrame(false)
                uCropView.overlayView.setShowCropGrid(false)
                uCropView.overlayView.setDimmedColor(Color.TRANSPARENT)
            } catch (e: Exception) {
                Log.e(TAG, "setImageUri", e)
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
        }
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(File(intent.data!!.path).absolutePath, options)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title =
                getString(R.string.format_crop_result_d_d, options.outWidth, options.outHeight)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_result, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_download) {
            saveCroppedImage()
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            BaseActivity.Companion.REQUEST_STORAGE_WRITE_ACCESS_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveCroppedImage()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveCroppedImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getString(R.string.permission_write_storage_rationale),
                BaseActivity.Companion.REQUEST_STORAGE_WRITE_ACCESS_PERMISSION)
        } else {
            val imageUri = intent.data
            if (imageUri != null && imageUri.scheme == "file") {
                try {
                    copyFileToDownloads(intent.data)
                } catch (e: Exception) {
                    Toast.makeText(this@ResultActivity, e.message, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, imageUri.toString(), e)
                }
            } else {
                Toast.makeText(this@ResultActivity,
                    getString(R.string.toast_unexpected_error),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(Exception::class)
    private fun copyFileToDownloads(croppedFileUri: Uri?) {
        val downloadsDirectoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val filename = String.format("%d_%s",
            Calendar.getInstance().timeInMillis,
            croppedFileUri!!.lastPathSegment)
        val saveFile = File(downloadsDirectoryPath, filename)
        val inStream = FileInputStream(File(
            croppedFileUri.path))
        val outStream = FileOutputStream(saveFile)
        val inChannel = inStream.channel
        val outChannel = outStream.channel
        inChannel.transferTo(0, inChannel.size(), outChannel)
        inStream.close()
        outStream.close()
        showNotification(saveFile)
        Toast.makeText(this, R.string.notification_image_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showNotification(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val fileUri = FileProvider.getUriForFile(
            this,
            getString(R.string.file_provider_authorities),
            file)
        intent.setDataAndType(fileUri, "image/*")
        val resInfoList = packageManager.queryIntentActivities(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY)
        for (info in resInfoList) {
            grantUriPermission(
                info.activityInfo.packageName,
                fileUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val notificationBuilder: NotificationCompat.Builder
        val notificationManager = this
            .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager?.createNotificationChannel(createChannel())
            NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }
        notificationBuilder
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.notification_image_saved_click_to_preview))
            .setTicker(getString(R.string.notification_image_saved))
            .setSmallIcon(R.drawable.ic_done)
            .setOngoing(false)
            .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
            .setAutoCancel(true)
        notificationManager?.notify(DOWNLOAD_NOTIFICATION_ID_DONE, notificationBuilder.build())
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createChannel(): NotificationChannel {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name), importance)
        channel.description = getString(R.string.channel_description)
        channel.enableLights(true)
        channel.lightColor = Color.YELLOW
        return channel
    }

    companion object {
        private const val TAG = "ResultActivity"
        private const val CHANNEL_ID = "3000"
        private const val DOWNLOAD_NOTIFICATION_ID_DONE = 911
        fun startWithUri(context: Context, uri: Uri) {
            val intent = Intent(context, ResultActivity::class.java)
            intent.data = uri
            context.startActivity(intent)
        }
    }
}