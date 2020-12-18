package org.catrobat.paintroid.colorpicker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun storeBitmapTemporally(bitmap: Bitmap, context: Context, imageName: String) =
        withContext(Dispatchers.IO) {
            var outputStream: FileOutputStream? = null
            try {
                outputStream = context.openFileOutput(imageName, Context.MODE_PRIVATE)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                outputStream?.close()
            }
        }

@Suppress("BlockingMethodInNonBlockingContext")
suspend fun loadBitmapByName(context: Context, imageName: String): Bitmap? {
    var bitmap: Bitmap? = null
    withContext(Dispatchers.IO) {
        var fileInputStream: FileInputStream? = null
        try {
            fileInputStream = context.openFileInput(imageName)
            bitmap = BitmapFactory.decodeStream(fileInputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileInputStream?.close()
        }
    }
    return bitmap
}


fun deleteBitmapFile(context: Context, imageName: String) = context.deleteFile(imageName)
