/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid

import android.app.Activity
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import org.catrobat.paintroid.common.Constants.MEDIA_DIRECTORY
import org.catrobat.paintroid.common.IS_JPG
import org.catrobat.paintroid.common.IS_NO_FILE
import org.catrobat.paintroid.common.IS_ORA
import org.catrobat.paintroid.common.IS_PNG
import org.catrobat.paintroid.common.MAX_LAYERS
import org.catrobat.paintroid.iotasks.BitmapReturnValue
import org.catrobat.paintroid.presenter.MainActivityPresenter
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.NullPointerException
import java.util.Locale
import java.util.UUID
import java.util.Objects.requireNonNull
import kotlin.Throws
import kotlin.math.min
import kotlin.math.sqrt
import id.zelory.compressor.Compressor

private const val CONSTANT_POINT9 = .9f
private const val CONSTANT_5000 = 5000L
private const val CONSTANT_4 = 4L
private const val CONSTANT_100 = 100
private const val ANGLE_90 = 90f
private const val ANGLE_180 = 180f
private const val ANGLE_270 = 270f
private const val BUFFER_SIZE = 4096

object FileIO {

    @JvmField
    var filename = "image"

    @JvmField
    var ending = ".png"

    var compressQuality = CONSTANT_100

    @JvmField
    var compressFormat = CompressFormat.PNG

    var catroidFlag = false

    @JvmField
    var isCatrobatImage = false

    @JvmField
    var wasImageLoaded = false

    @JvmField
    var currentFileNameJpg: String? = null

    @JvmField
    var currentFileNamePng: String? = null

    var currentFileNameOra: String? = null

    @JvmField
    var uriFileJpg: Uri? = null

    @JvmField
    var uriFilePng: Uri? = null

    var uriFileOra: Uri? = null

    val defaultFileName: String
        get() = filename + ending

    private val cacheChildFolder = "images"

    @Throws(IOException::class)
    private fun saveBitmapToStream(outputStream: OutputStream?, bitmap: Bitmap?) {
        var currentBitmap = bitmap
        require(currentBitmap != null && !currentBitmap.isRecycled) { "Bitmap is invalid" }
        if (compressFormat == CompressFormat.JPEG) {
            val newBitmap =
                Bitmap.createBitmap(currentBitmap.width, currentBitmap.height, currentBitmap.config)
            val canvas = Canvas(newBitmap)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(currentBitmap, 0f, 0f, null)
            currentBitmap = newBitmap
        }
        if (currentBitmap != null && !currentBitmap.compress(
                compressFormat,
                compressQuality,
                outputStream
            )
        ) {
            throw IOException("Can not write png to stream.")
        }
    }

    @Throws(IOException::class)
    fun saveBitmapToUri(uri: Uri, bitmap: Bitmap?, context: Context): Uri {
        val uid = UUID.randomUUID()
        val cachedImageUri = saveBitmapToCache(bitmap, context as MainActivity, uid.toString())
        val cachedFile = File(MainActivityPresenter.getPathFromUri(context, cachedImageUri))

        try {
            if (!compress(context, cachedFile, uri)) {
                throw IOException("Can not open URI.")
            }
        } finally {
            if (cachedFile.exists()) {
                cachedFile.delete()
            }
        }
        return uri
    }

    fun compress(mainActivity: MainActivity, fileToCompress: File, destination: Uri): Boolean {
        val compressor = Compressor(mainActivity)
        compressor.setQuality(compressQuality)
        compressor.setCompressFormat(compressFormat)
        val tempFileName = "tmp"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            var compressed: File? = null
            try {
                val cachePath = File(mainActivity.cacheDir, cacheChildFolder)
                cachePath.mkdirs()
                compressor.setDestinationDirectoryPath(cachePath.path)
                compressed = compressor.compressToFile(fileToCompress, tempFileName + ending)
                val os = mainActivity.contentResolver.openOutputStream(destination)
                copyStreams(FileInputStream(compressed), os!!)
                true
            } catch (e: IOException) {
                Log.e("Compression", "Can not compress image file.", e)
                false
            } finally {
                if (compressed != null && compressed.exists()) {
                    compressed.delete()
                }
            }
        } else {
            try {
                compressor.setDestinationDirectoryPath(requireNonNull(File(destination.path!!).parentFile).getPath())
                compressor.compressToFile(fileToCompress, destination.lastPathSegment)
                true
            } catch (e: IOException) {
                Log.e("Compression", "Can not compress image file", e)
                false
            }
        }
    }

    fun saveBitmapToFile(fileName: String, bitmap: Bitmap?, resolver: ContentResolver?, context: Context?): Uri? {
        val imageUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/*")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            if (!(MEDIA_DIRECTORY.exists() || MEDIA_DIRECTORY.mkdirs())) {
                throw IOException("Can not create media directory.")
            }
            Uri.fromFile(File(MEDIA_DIRECTORY, fileName))
        }

        val cachedImageUri = saveBitmapToCache(bitmap, context as MainActivity, UUID.randomUUID().toString())
        val cachedFile = File(MainActivityPresenter.getPathFromUri(context, cachedImageUri))

        try {
            if (imageUri == null || !compress(context, cachedFile, imageUri)) {
                throw IOException("Can not compress image file.")
            }
            return imageUri
        } finally {
            if (cachedFile.exists()) {
                cachedFile.delete()
            }
        }
    }

    fun saveBitmapToCache(bitmap: Bitmap?, mainActivity: MainActivity, fileName: String): Uri? {
        var uri: Uri? = null
        try {
            val cachePath = File(mainActivity.cacheDir, cacheChildFolder)
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/$fileName$ending")
            saveBitmapToStream(stream, bitmap)
            stream.close()
            val imagePath = File(mainActivity.cacheDir, cacheChildFolder)
            val newFile = File(imagePath, fileName + ending)
            val fileProviderString =
                mainActivity.applicationContext.packageName + ".fileprovider"
            uri = FileProvider.getUriForFile(
                mainActivity.applicationContext,
                fileProviderString,
                newFile
            )
        } catch (e: IOException) {
            Log.e("Can not write", "Can not write png to stream.", e)
        }
        return uri
    }

    @Throws(NullPointerException::class)
    @JvmStatic
    fun createNewEmptyPictureFile(filename: String?, activity: Activity): File {
        var fileName = filename ?: defaultFileName
        if (!fileName.toLowerCase(Locale.US).endsWith(ending.toLowerCase(Locale.US))) {
            fileName += ending
        }
        val externalFilesDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (externalFilesDir == null || !externalFilesDir.exists() && !externalFilesDir.mkdir()) {
            throw NullPointerException("Can not create media directory.")
        }
        return File(externalFilesDir, fileName)
    }

    @Throws(IOException::class)
    fun decodeBitmapFromUri(
        resolver: ContentResolver,
        uri: Uri,
        options: BitmapFactory.Options,
        context: Context?
    ): Bitmap? {
        val inputStream =
            resolver.openInputStream(uri) ?: throw IOException("Can't open input stream")
        return inputStream.use {
            val bitmap = BitmapFactory.decodeStream(it, null, options)
            if (options.inJustDecodeBounds) {
                return bitmap
            }
            val angle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getBitmapOrientationFromInputStream(resolver, uri)
            } else {
                getBitmapOrientationFromUri(uri, context)
            }
            getOrientedBitmap(bitmap, angle)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Throws(IOException::class)
    private fun getBitmapOrientationFromInputStream(
        resolver: ContentResolver,
        uri: Uri
    ): Float {
        val inputStream = resolver.openInputStream(uri) ?: return 0f
        return inputStream.use {
            val exifInterface = ExifInterface(it)
            getBitmapOrientation(exifInterface)
        }
    }

    @Throws(IOException::class)
    private fun getBitmapOrientationFromUri(uri: Uri, context: Context?): Float {
        val exifInterface = ExifInterface(MainActivityPresenter.getPathFromUri(context, uri))
        return getBitmapOrientation(exifInterface)
    }

    @JvmStatic
    fun getOrientedBitmap(bitmap: Bitmap?, angle: Float): Bitmap? {
        bitmap ?: return null
        val matrix = Matrix()
        matrix.postRotate(angle)
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    @JvmStatic
    fun getBitmapOrientation(exifInterface: ExifInterface?): Float {
        exifInterface ?: return 0f
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        var angle = 0f
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> angle = ANGLE_90
            ExifInterface.ORIENTATION_ROTATE_180 -> angle = ANGLE_180
            ExifInterface.ORIENTATION_ROTATE_270 -> angle = ANGLE_270
        }
        return angle
    }

    fun parseFileName(uri: Uri, resolver: ContentResolver) {
        var fileName = "image"
        val cursor = resolver.query(
            uri,
            arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME),
            null, null, null
        )
        cursor?.use {
            if (cursor.moveToFirst()) {
                fileName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME))
            }
        }
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
            ending = ".jpg"
            compressFormat = CompressFormat.JPEG
            filename = fileName.substring(0, fileName.length - ending.length)
        } else if (fileName.endsWith(".png")) {
            ending = ".png"
            compressFormat = CompressFormat.PNG
            filename = fileName.substring(0, fileName.length - ending.length)
        }
    }

    @JvmStatic
    fun saveFileFromUri(uri: Uri, destFile: File, context: Context) {
        try {
            context.contentResolver.openInputStream(uri)?.use { fileInputStream ->
                FileOutputStream(destFile).use { fileOutputStream ->
                    copyStreams(fileInputStream, fileOutputStream)
                }
            }
        } catch (e: IOException) {
            Log.e("FileIO", "Can not copy streams.", e)
        }
    }

    @Throws(IOException::class)
    private fun copyStreams(from: InputStream, to: OutputStream): Long {
        val buffer = ByteArray(BUFFER_SIZE)
        var total: Long = 0
        while (true) {
            val read = from.read(buffer)
            if (read == -1) {
                break
            }
            to.write(buffer, 0, read)
            total += read.toLong()
        }
        return total
    }

    fun checkIfDifferentFile(filename: String): Int = when {
        currentFileNameJpg == filename -> IS_JPG
        currentFileNamePng == filename -> IS_PNG
        currentFileNameOra == filename -> IS_ORA
        else -> IS_NO_FILE
    }

    private fun calculateSampleSize(width: Int, height: Int, maxWidth: Int, maxHeight: Int): Int {
        var w = width
        var h = height
        var sampleSize = 1
        while (w > maxWidth || h > maxHeight) {
            w /= 2
            h /= 2
            sampleSize *= 2
        }
        return sampleSize
    }

    @Throws(IOException::class)
    @JvmStatic
    fun getBitmapFromUri(resolver: ContentResolver, bitmapUri: Uri, context: Context): Bitmap? {
        val options = BitmapFactory.Options()
        options.inMutable = true
        return enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options, context))
    }

    private fun getMemoryInfo(context: Context?): ActivityManager.MemoryInfo {
        val memoryInfo = ActivityManager.MemoryInfo()
        val activityManager =
            context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        activityManager?.getMemoryInfo(memoryInfo)
        return memoryInfo
    }

    @Throws(IOException::class)
    private fun hasEnoughMemory(
        resolver: ContentResolver,
        bitmapUri: Uri,
        context: Context?
    ): Boolean {
        var scaling = false
        val memoryInfo = getMemoryInfo(context)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        decodeBitmapFromUri(resolver, bitmapUri, options, context)
        if (options.outHeight < 0 || options.outWidth < 0) {
            throw IOException("Can't load bitmap from uri")
        }
        val availableMemory = min(
            ((memoryInfo.availMem - memoryInfo.threshold) * CONSTANT_POINT9).toLong(),
            CONSTANT_5000 * CONSTANT_5000 * CONSTANT_4
        )
        val requiredMemory = options.outWidth * options.outHeight * CONSTANT_4
        if (requiredMemory > availableMemory) {
            scaling = true
        }
        return scaling
    }

    @Throws(IOException::class)
    private fun getScaleFactor(resolver: ContentResolver, bitmapUri: Uri, context: Context?): Int {
        getMemoryInfo(context)
        val options = BitmapFactory.Options()
        decodeBitmapFromUri(resolver, bitmapUri, options, context)
        if (options.outHeight <= 0 || options.outWidth <= 0) {
            throw IOException("Can't load bitmap from uri")
        }
        val info = Runtime.getRuntime()
        val availableMemory =
            (info.maxMemory() - info.totalMemory() + info.freeMemory()) * CONSTANT_POINT9
        val heightToWidthFactor = options.outWidth / options.outHeight * 1f
        val availablePixels =
            availableMemory / MAX_LAYERS.toFloat() * CONSTANT_POINT9 / CONSTANT_4 // 4 byte per pixel, 10% safety buffer on memory
        val availableHeight = sqrt(availablePixels / heightToWidthFactor)
        val availableWidth = availablePixels / availableHeight
        return calculateSampleSize(
            options.outWidth,
            options.outHeight,
            availableWidth.toInt(),
            availableHeight.toInt()
        )
    }

    @Throws(IOException::class)
    fun getBitmapReturnValueFromUri(
        resolver: ContentResolver,
        bitmapUri: Uri,
        context: Context?
    ): BitmapReturnValue {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inJustDecodeBounds = false
        }
        val scaling = hasEnoughMemory(resolver, bitmapUri, context)
        return BitmapReturnValue(
            null,
            enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options, context)),
            scaling
        )
    }

    @Throws(IOException::class)
    fun getScaledBitmapFromUri(
        resolver: ContentResolver,
        bitmapUri: Uri,
        context: Context?
    ): BitmapReturnValue {
        val options = BitmapFactory.Options().apply {
            inMutable = true
            inJustDecodeBounds = false
            inSampleSize = getScaleFactor(resolver, bitmapUri, context)
        }
        return BitmapReturnValue(
            null,
            enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options, context)),
            false
        )
    }

    fun getBitmapFromFile(bitmapFile: File?): Bitmap? {
        bitmapFile ?: return null
        val options = BitmapFactory.Options()
        options.inMutable = true
        return enableAlpha(BitmapFactory.decodeFile(bitmapFile.absolutePath, options))
    }

    @JvmStatic
    fun enableAlpha(bitmap: Bitmap?): Bitmap? {
        bitmap?.setHasAlpha(true)
        return bitmap
    }
}
