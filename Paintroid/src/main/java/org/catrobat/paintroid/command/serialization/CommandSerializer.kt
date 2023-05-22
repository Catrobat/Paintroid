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
package org.catrobat.paintroid.command.serialization

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.KryoException
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.colorpicker.ColorHistory
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.ClippingCommand
import org.catrobat.paintroid.command.implementation.AddEmptyLayerCommand
import org.catrobat.paintroid.command.implementation.CompositeCommand
import org.catrobat.paintroid.command.implementation.CropCommand
import org.catrobat.paintroid.command.implementation.CutCommand
import org.catrobat.paintroid.command.implementation.FillCommand
import org.catrobat.paintroid.command.implementation.FlipCommand
import org.catrobat.paintroid.command.implementation.GeometricFillCommand
import org.catrobat.paintroid.command.implementation.LayerOpacityCommand
import org.catrobat.paintroid.command.implementation.LoadLayerListCommand
import org.catrobat.paintroid.command.implementation.LoadCommand
import org.catrobat.paintroid.command.implementation.MergeLayersCommand
import org.catrobat.paintroid.command.implementation.PathCommand
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.command.implementation.RemoveLayerCommand
import org.catrobat.paintroid.command.implementation.ReorderLayersCommand
import org.catrobat.paintroid.command.implementation.ResetCommand
import org.catrobat.paintroid.command.implementation.ResizeCommand
import org.catrobat.paintroid.command.implementation.RotateCommand
import org.catrobat.paintroid.command.implementation.SelectLayerCommand
import org.catrobat.paintroid.command.implementation.SetDimensionCommand
import org.catrobat.paintroid.command.implementation.SprayCommand
import org.catrobat.paintroid.command.implementation.ClipboardCommand
import org.catrobat.paintroid.command.implementation.TextToolCommand
import org.catrobat.paintroid.command.implementation.SmudgePathCommand
import org.catrobat.paintroid.common.Constants.DOWNLOADS_DIRECTORY
import org.catrobat.paintroid.common.SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME
import org.catrobat.paintroid.iotasks.OpenRasterFileFormatConversion
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.iotasks.WorkspaceReturnValue
import org.catrobat.paintroid.model.CommandManagerModel
import org.catrobat.paintroid.tools.drawable.HeartDrawable
import org.catrobat.paintroid.tools.drawable.OvalDrawable
import org.catrobat.paintroid.tools.drawable.RectangleDrawable
import org.catrobat.paintroid.tools.drawable.ShapeDrawable
import org.catrobat.paintroid.tools.drawable.StarDrawable
import org.catrobat.paintroid.ui.DrawingSurfaceThread
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream

open class CommandSerializer(private val activityContext: Context, private val commandManager: CommandManager, private val model: MainActivityContracts.Model) {

    companion object {
        const val CURRENT_IMAGE_VERSION = 2
        const val MAGIC_VALUE = "CATROBAT"
        private val TAG = DrawingSurfaceThread::class.java.simpleName
    }

    val kryo = Kryo()
    private val registerMap = LinkedHashMap<Class<*>, VersionSerializer<*>?>()

    init {
        setRegisterMapVersion(CURRENT_IMAGE_VERSION)
        registerClasses()
    }

    private fun setRegisterMapVersion(version: Int) {
        // Only add new classes at the end
        // because Kryo assigns an ID to each class
        with(registerMap) {
            put(Command::class.java, null)
            put(CompositeCommand::class.java, CompositeCommandSerializer(version))
            put(FloatArray::class.java, DataStructuresSerializer.FloatArraySerializer(version))
            put(PointF::class.java, DataStructuresSerializer.PointFSerializer(version))
            put(Point::class.java, DataStructuresSerializer.PointSerializer(version))
            put(CommandManagerModel::class.java, CommandManagerModelSerializer(version))
            put(SetDimensionCommand::class.java, SetDimensionCommandSerializer(version))
            put(SprayCommand::class.java, SprayCommandSerializer(version))
            put(Paint::class.java, PaintSerializer(version, activityContext))
            put(AddEmptyLayerCommand::class.java, AddLayerCommandSerializer(version))
            put(SelectLayerCommand::class.java, SelectLayerCommandSerializer(version))
            put(LoadCommand::class.java, LoadCommandSerializer(version))
            put(TextToolCommand::class.java, TextToolCommandSerializer(version, activityContext))
            put(Array<String>::class.java, DataStructuresSerializer.StringArraySerializer(version))
            put(FillCommand::class.java, FillCommandSerializer(version))
            put(FlipCommand::class.java, FlipCommandSerializer(version))
            put(CropCommand::class.java, CropCommandSerializer(version))
            put(CutCommand::class.java, CutCommandSerializer(version))
            put(ResizeCommand::class.java, ResizeCommandSerializer(version))
            put(RotateCommand::class.java, RotateCommandSerializer(version))
            put(ResetCommand::class.java, ResetCommandSerializer(version))
            put(ReorderLayersCommand::class.java, ReorderLayersCommandSerializer(version))
            put(RemoveLayerCommand::class.java, RemoveLayerCommandSerializer(version))
            put(MergeLayersCommand::class.java, MergeLayersCommandSerializer(version))
            put(PathCommand::class.java, PathCommandSerializer(version))
            put(SerializablePath::class.java, SerializablePath.PathSerializer(version))
            put(SerializablePath.Move::class.java, SerializablePath.PathActionMoveSerializer(version))
            put(SerializablePath.Line::class.java, SerializablePath.PathActionLineSerializer(version))
            put(SerializablePath.Quad::class.java, SerializablePath.PathActionQuadSerializer(version))
            put(SerializablePath.Rewind::class.java, SerializablePath.PathActionRewindSerializer(version))
            put(LoadLayerListCommand::class.java, LoadLayerListCommandSerializer(version))
            put(GeometricFillCommand::class.java, GeometricFillCommandSerializer(version))
            put(HeartDrawable::class.java, GeometricFillCommandSerializer.HeartDrawableSerializer(version))
            put(OvalDrawable::class.java, GeometricFillCommandSerializer.OvalDrawableSerializer(version))
            put(RectangleDrawable::class.java, GeometricFillCommandSerializer.RectangleDrawableSerializer(version))
            put(StarDrawable::class.java, GeometricFillCommandSerializer.StarDrawableSerializer(version))
            put(ShapeDrawable::class.java, null)
            put(RectF::class.java, DataStructuresSerializer.RectFSerializer(version))
            put(ClipboardCommand::class.java, ClipboardCommandSerializer(version))
            put(SerializableTypeface::class.java, SerializableTypeface.TypefaceSerializer(version))
            put(PointCommand::class.java, PointCommandSerializer(version))
            put(SerializablePath.Cube::class.java, SerializablePath.PathActionCubeSerializer(version))
            put(Bitmap::class.java, BitmapSerializer(version))
            put(SmudgePathCommand::class.java, SmudgePathCommandSerializer(version))
            put(ColorHistory::class.java, ColorHistorySerializer(version))
            put(ClippingCommand::class.java, ClippingCommandSerializer(version))
            put(LayerOpacityCommand::class.java, LayerOpacityCommandSerializer(version))
        }
    }

    private fun registerClasses() {
        registerMap.forEach { (classRegister, serializer) ->
            val registration = kryo.register(classRegister)
            serializer?.let {
                registration.serializer = serializer
            }
        }
    }

    fun writeToFile(fileName: String): Uri? {
        var returnUri: Uri? = null
        val contentResolver = activityContext.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { stream ->
                    writeToStream(stream)
                    returnUri = uri
                }
            }
        } else {
            if (!(DOWNLOADS_DIRECTORY.exists() || DOWNLOADS_DIRECTORY.mkdirs())) {
                return null
            }
            val imageFile = File(DOWNLOADS_DIRECTORY, fileName)
            FileOutputStream(imageFile).use { fileStream ->
                writeToStream(fileStream)
                returnUri = Uri.fromFile(imageFile)
            }

            val downloadManager = OpenRasterFileFormatConversion.mainActivity.baseContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val id = downloadManager.addCompletedDownload(fileName, fileName, true, "application/zip", imageFile.absolutePath, imageFile.length(), true)
            val sharedPreferences = OpenRasterFileFormatConversion.mainActivity.getSharedPreferences(SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME, 0)
            sharedPreferences.edit().putLong(imageFile.absolutePath, id).apply()
        }

        return returnUri
    }

    fun overWriteFile(fileName: String, uri: Uri, resolver: ContentResolver): Uri? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val c = resolver.query(uri, projection, null, null, null)
            if (c != null) {
                if (c.moveToFirst()) {
                    val id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val deleteUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id)
                    resolver.delete(deleteUri, null, null)
                } else {
                    throw AssertionError("No file to delete was found!")
                }
                c.close()
            }
        } else {
            val file = File(uri.path.toString())
            val isDeleted = file.delete()
            val sharedPreferences = OpenRasterFileFormatConversion.mainActivity.getSharedPreferences(SPECIFIC_FILETYPE_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            val id = sharedPreferences.getLong(uri.path, -1)
            if (id > -1) {
                val downloadManager = OpenRasterFileFormatConversion.mainActivity.baseContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.remove(id)
            }
            if (!isDeleted) {
                throw AssertionError("No file to delete was found!")
            }
        }
        return writeToFile(fileName)
    }

    fun writeToInternalMemory(stream: FileOutputStream) {
        stream.use { fileStream ->
            writeToStream(fileStream)
        }
    }

    fun readFromInternalMemory(stream: FileInputStream): WorkspaceReturnValue {
        var commandModel: CommandManagerModel? = null
        var colorHistory: ColorHistory? = null

        try {
            Input(stream).use { input ->
                if (!input.readString().equals(MAGIC_VALUE)) {
                    throw NotCatrobatImageException("Magic Value doesn't exist.")
                }
                val imageVersion = input.readInt()
                if (CURRENT_IMAGE_VERSION != imageVersion) {
                    setRegisterMapVersion(imageVersion)
                    registerClasses()
                }
                commandModel = kryo.readObject(input, CommandManagerModel::class.java)
                colorHistory = kryo.readObject(input, ColorHistory::class.java)
            }
        } catch (ex: KryoException) {
            Log.d(TAG, "KryoException while reading autosave: " + ex.message)
        }

        commandModel?.commands?.reverse()

        return WorkspaceReturnValue(commandModel, colorHistory)
    }

    private fun writeToStream(stream: OutputStream) {
        Output(stream).use { output ->
            output.writeString(MAGIC_VALUE)
            output.writeInt(CURRENT_IMAGE_VERSION)
            kryo.writeObject(output, commandManager.getCommandManagerModelForCatrobatImage())
            if (model.colorHistory.colors.isNotEmpty()) {
                kryo.writeObject(output, model.colorHistory)
            }
        }
    }

    fun readFromFile(uri: Uri): CatrobatFileContent {
        var commandModel: CommandManagerModel
        var colorHistory: ColorHistory? = null

        activityContext.contentResolver.openInputStream(uri).use { contentResolverStream ->
            Input(contentResolverStream).use { input ->
                if (!input.readString().equals(MAGIC_VALUE)) {
                    throw NotCatrobatImageException("Magic Value doesn't exist.")
                }
                val imageVersion = input.readInt()
                if (CURRENT_IMAGE_VERSION != imageVersion) {
                    setRegisterMapVersion(imageVersion)
                    registerClasses()
                }
                commandModel = kryo.readObject(input, CommandManagerModel::class.java)
                if (input.canReadInt()) {
                    colorHistory = kryo.readObject(input, ColorHistory::class.java)
                }
            }
        }

        commandModel.commands.reverse()
        return CatrobatFileContent(commandModel, colorHistory)
    }

    class NotCatrobatImageException(message: String) : Exception(message)
}
