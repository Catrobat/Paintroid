/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.AddLayerCommand
import org.catrobat.paintroid.command.implementation.CompositeCommand
import org.catrobat.paintroid.command.implementation.CropCommand
import org.catrobat.paintroid.command.implementation.CutCommand
import org.catrobat.paintroid.command.implementation.FillCommand
import org.catrobat.paintroid.command.implementation.FlipCommand
import org.catrobat.paintroid.command.implementation.GeometricFillCommand
import org.catrobat.paintroid.command.implementation.LoadBitmapListCommand
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
import org.catrobat.paintroid.command.implementation.StampCommand
import org.catrobat.paintroid.command.implementation.TextToolCommand
import org.catrobat.paintroid.common.Constants
import org.catrobat.paintroid.model.CommandManagerModel
import org.catrobat.paintroid.tools.drawable.HeartDrawable
import org.catrobat.paintroid.tools.drawable.OvalDrawable
import org.catrobat.paintroid.tools.drawable.RectangleDrawable
import org.catrobat.paintroid.tools.drawable.ShapeDrawable
import org.catrobat.paintroid.tools.drawable.StarDrawable
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import kotlin.collections.LinkedHashMap

class CommandSerializationUtilities(private val activityContext: Context, private val commandManager: CommandManager) {

    companion object {
        const val CURRENT_IMAGE_VERSION = 1
        const val MAGIC_VALUE = "CATROBAT"
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
            put(AddLayerCommand::class.java, AddLayerCommandSerializer(version))
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
            put(LoadBitmapListCommand::class.java, LoadBitmapListCommandSerializer(version))
            put(GeometricFillCommand::class.java, GeometricFillCommandSerializer(version))
            put(HeartDrawable::class.java, GeometricFillCommandSerializer.HeartDrawableSerializer(version))
            put(OvalDrawable::class.java, GeometricFillCommandSerializer.OvalDrawableSerializer(version))
            put(RectangleDrawable::class.java, GeometricFillCommandSerializer.RectangleDrawableSerializer(version))
            put(StarDrawable::class.java, GeometricFillCommandSerializer.StarDrawableSerializer(version))
            put(ShapeDrawable::class.java, null)
            put(RectF::class.java, DataStructuresSerializer.RectFSerializer(version))
            put(StampCommand::class.java, StampCommandSerializer(version))
            put(SerializableTypeface::class.java, SerializableTypeface.TypefaceSerializer(version))
            put(PointCommand::class.java, PointCommandSerializer(version))
            put(SerializablePath.Cube::class.java, SerializablePath.PathActionCubeSerializer(version))
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
            if (!(Constants.MEDIA_DIRECTORY.exists() || Constants.MEDIA_DIRECTORY.mkdirs())) {
                return null
            }
            val imageFile = File(Constants.MEDIA_DIRECTORY, fileName)
            FileOutputStream(imageFile).use { fileStream ->
                writeToStream(fileStream)
                returnUri = Uri.fromFile(imageFile)
            }
        }

        return returnUri
    }

    private fun writeToStream(stream: OutputStream) {
        Output(stream).use { output ->
            output.writeString(MAGIC_VALUE)
            output.writeInt(CURRENT_IMAGE_VERSION)
            kryo.writeObject(output, commandManager.commandManagerModel)
        }
    }

    fun readFromFile(uri: Uri): CommandManagerModel {
        var commandModel: CommandManagerModel

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
            }
        }

        commandModel.commands.reverse()
        return commandModel
    }

    class NotCatrobatImageException(message: String) : Exception(message)
}
