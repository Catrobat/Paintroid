/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.iotasks

import android.app.DownloadManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import org.catrobat.paintroid.FileIO.enableAlpha
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.common.MAX_LAYERS
import org.catrobat.paintroid.common.SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Layer
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.math.BigInteger
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

//
//Source:	https://www.openraster.org/baseline/file-layout-spec.html
//
//Layout:
//example.ora  [considered as a folder-like object]
//        ├ mimetype
//        ├ stack.xml
//        ├ data/
//        │  ├ [image data files referenced by stack.xml, typically layer*.png]
//        │  └ [other data files, indexed elsewhere]
//        ├ Thumbnails/
//        │  └ thumbnail.png
//        └ mergedimage.png
class OpenRasterFileFormatConversion private constructor() {
    init {
        throw AssertionError()
    }

    companion object {
        private val TAG = OpenRasterFileFormatConversion::class.java.simpleName
        private const val COMPRESS_QUALITY = 100
        private const val THUMBNAIL_WIDTH = 256
        private const val THUMBNAIL_HEIGHT = 256
        private const val ORA_VERSION = "0.0.2"
        var mainActivity: MainActivity? = null
        fun setContext(toSet: MainActivity?) {
            mainActivity = toSet
        }

        @Throws(IOException::class)
        fun exportToOraFile(layers: List<LayerContracts.Layer>, fileName: String, bitmapAllLayers: Bitmap, resolver: ContentResolver): Uri? {
            val imageUri: Uri?
            val outputStream: OutputStream?
            val contentValues = ContentValues()
            var wholeSize = 0f
            val mimeType = "image/openraster"
            val mimeByteArray = mimeType.toByteArray()
            val xmlByteArray = getXmlStack(layers)
            for (current in layers) {
                val bos = ByteArrayOutputStream()
                current.bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bos)
                val byteArray = bos.toByteArray()
                wholeSize += byteArray.size.toFloat()
                val alphaByteArray = BigInteger.valueOf(current.opacityPercentage.toLong()).toByteArray()
                wholeSize += alphaByteArray.size.toFloat()
            }
            val bosMerged = ByteArrayOutputStream()
            bitmapAllLayers.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bosMerged)
            val bitmapByteArray = bosMerged.toByteArray()
            val bitmapThumb = Bitmap.createScaledBitmap(bitmapAllLayers, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, false)
            val bosThumb = ByteArrayOutputStream()
            bitmapThumb.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bosThumb)
            val bitmapThumbArray = bosThumb.toByteArray()
            var imageRoot: File? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //applefile has no file ending. which is important for api level 30.
                // we can't save an application file in media directory so we have to save it in downloads.
                contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/applefile")
                imageUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            } else {
                imageRoot = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS)
                if (!imageRoot.exists() && !imageRoot.mkdirs()) {
                    imageRoot.mkdirs()
                }
                val uri = MediaStore.Files.getContentUri("external")
                contentValues.put(MediaStore.Files.FileColumns.DATA, imageRoot.absolutePath + "/" + fileName)
                contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/zip")
                contentValues.put(MediaStore.Files.FileColumns.MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_NONE)
                val date = System.currentTimeMillis()
                contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, date / 1000)
                wholeSize += xmlByteArray!!.size.toFloat()
                wholeSize += mimeByteArray.size.toFloat()
                wholeSize += bitmapByteArray.size.toFloat()
                wholeSize += bitmapThumbArray.size.toFloat()
                contentValues.put(MediaStore.Images.Media.SIZE, wholeSize)
                val downloadManager = mainActivity!!.baseContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val id = downloadManager.addCompletedDownload(fileName, fileName, true, "application/zip", imageRoot.absolutePath + "/" + fileName, wholeSize.toLong(), true)
                imageUri = resolver.insert(uri, contentValues)
                val sharedPreferences = mainActivity!!.getSharedPreferences(SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME, 0)
                sharedPreferences.edit().putLong(imageRoot.absolutePath + "/" + fileName, id).apply()
            }
            outputStream = Objects.requireNonNull(imageUri).let { it?.let { it1 -> resolver.openOutputStream(it1) } }
            val streamZip = ZipOutputStream(outputStream)
            val mimetypeEntry = ZipEntry("mimetype")
            mimetypeEntry.method = ZipEntry.DEFLATED
            streamZip.putNextEntry(mimetypeEntry)
            streamZip.write(mimeByteArray, 0, mimeByteArray.size)
            streamZip.closeEntry()
            streamZip.putNextEntry(ZipEntry("stack.xml"))
            Objects.requireNonNull(xmlByteArray).let { it?.let { it1 -> streamZip.write(xmlByteArray, 0, it1.size) } }
            streamZip.closeEntry()
            var counter = 0
            for (current in layers) {
                val bos = ByteArrayOutputStream()
                current.bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bos)
                val byteArray = bos.toByteArray()
                streamZip.putNextEntry(ZipEntry("data/layer$counter.png"))
                streamZip.write(byteArray, 0, byteArray.size)
                streamZip.closeEntry()
                streamZip.putNextEntry(ZipEntry("alpha/$counter"))
                val alphaByteArray = BigInteger.valueOf(current.opacityPercentage.toLong()).toByteArray()
                streamZip.write(alphaByteArray, 0, alphaByteArray.size)
                streamZip.closeEntry()
                counter++
            }
            streamZip.putNextEntry(ZipEntry("Thumbnails/thumbnail.png"))
            streamZip.write(bitmapThumbArray, 0, bitmapThumbArray.size)
            streamZip.closeEntry()
            streamZip.putNextEntry(ZipEntry("mergedimage.png"))
            streamZip.write(bitmapByteArray, 0, bitmapByteArray.size)
            streamZip.closeEntry()
            streamZip.close()
            outputStream!!.close()
            return imageUri
        }

        private fun getXmlStack(bitmapList: List<LayerContracts.Layer>): ByteArray? {
            val docFactory = DocumentBuilderFactory.newInstance()
            return try {
                val docBuilder = docFactory.newDocumentBuilder()
                val doc = docBuilder.newDocument()
                val rootElement = doc.createElement("image")
                val attr1 = doc.createAttribute("version")
                val attr2 = doc.createAttribute("w")
                val attr3 = doc.createAttribute("h")
                attr1.value = ORA_VERSION
                attr2.value = bitmapList[0].bitmap.width.toString()
                attr3.value = bitmapList[0].bitmap.height.toString()
                rootElement.setAttributeNode(attr1)
                rootElement.setAttributeNode(attr2)
                rootElement.setAttributeNode(attr3)
                doc.appendChild(rootElement)
                val stack = doc.createElement("stack")
                rootElement.appendChild(stack)
                for (i in bitmapList.indices.reversed()) {
                    val layer = doc.createElement("layer")
                    layer.setAttribute("name", "layer$i")
                    layer.setAttribute("src", "data/layer$i.png")
                    stack.appendChild(layer)
                }
                val stream = ByteArrayOutputStream()
                val result = StreamResult(stream)
                val transformerFactory = TransformerFactory.newInstance()
                val transformer = transformerFactory.newTransformer()
                val source = DOMSource(doc)
                transformer.transform(source, result)
                stream.toByteArray()
            } catch (e: ParserConfigurationException) {
                Log.e(TAG, "Could not create document.")
                null
            } catch (e: TransformerException) {
                Log.e(TAG, "Could not transform Xml file.")
                null
            }
        }

        @Throws(IOException::class)
        fun saveOraFileToUri(layers: List<LayerContracts.Layer>, uri: Uri, fileName: String, bitmapAllLayers: Bitmap, resolver: ContentResolver): Uri? {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val projection = arrayOf(MediaStore.Images.Media._ID)
                val c = resolver.query(uri, projection, null, null, null)
                if (c!!.moveToFirst()) {
                    val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val deleteUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
                    resolver.delete(deleteUri, null, null)
                } else {
                    throw AssertionError("No file to delete was found!")
                }
                c.close()
            } else {
                val file = File(uri.path)
                val isDeleted = file.delete()
                val sharedPreferences = mainActivity!!.getSharedPreferences(SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                val id = sharedPreferences.getLong(uri.path, -1)
                if (id > -1) {
                    val downloadManager = mainActivity!!.baseContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.remove(id)
                }
                if (!isDeleted) {
                    throw AssertionError("No file to delete was found!")
                }
            }
            return exportToOraFile(layers, fileName, bitmapAllLayers, resolver)
        }

        @Throws(IOException::class)
        fun importOraFile(resolver: ContentResolver, uri: Uri?): BitmapReturnValue {
            val options = BitmapFactory.Options()
            val inputStream = resolver.openInputStream(uri!!)
            val zipInput = ZipInputStream(inputStream)
            val layers: MutableList<LayerContracts.Layer> = ArrayList()
            var current = zipInput.nextEntry
            while (current != null) {
                if (current.name.matches("data/(.*).png".toRegex())) {
                    val layerBitmap = enableAlpha(BitmapFactory.decodeStream(zipInput, null, options))
                            ?: throw IOException("Cannot decode stream to bitmap!")
                    val layer: LayerContracts.Layer = Layer(layerBitmap)
                    current = zipInput.nextEntry
                    if (current != null && current.name.matches("alpha/(.*)".toRegex())) {
                        val opacityPercentage = zipInput.read()
                        layer.opacityPercentage = opacityPercentage
                        current = zipInput.nextEntry
                    }
                    layers.add(layer)
                } else {
                    current = zipInput.nextEntry
                }
            }
            if (layers.isEmpty() || layers.size > MAX_LAYERS) {
                throw IOException("Bitmap list is wrong!")
            }
            return BitmapReturnValue(layers, null, false)
        }
    }
}
