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
package org.catrobat.paintroid.command.serialization

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.KryoException
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.FileIO
import org.catrobat.paintroid.command.implementation.ClipboardCommand
import java.io.IOException

class ClipboardCommandSerializer(version: Int) : VersionSerializer<ClipboardCommand>(version) {

    companion object {
        private const val COMPRESSION_QUALITY = 100
    }

    override fun write(kryo: Kryo, output: Output, command: ClipboardCommand) {
        with(kryo) {
            with(output) {
                var bitmap = command.fileToStoredBitmap?.let { file ->
                    FileIO.getBitmapFromFile(file)
                }
                bitmap = bitmap ?: command.bitmap ?: throw KryoException()
                bitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, output)
                writeObject(output, command.coordinates)
                writeFloat(command.boxWidth)
                writeFloat(command.boxHeight)
                writeFloat(command.boxRotation)
            }
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out ClipboardCommand>): ClipboardCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out ClipboardCommand>): ClipboardCommand {
        return with(kryo) {
            with(input) {
                val bitmap = BitmapFactory.decodeStream(input)
                val coordinates = readObject(input, Point::class.java)
                val width = readFloat()
                val height = readFloat()
                val rotation = readFloat()
                if (bitmap == null) throw IOException("Bitmap is null! Can not create ClipboardCommand.")
                ClipboardCommand(bitmap, coordinates, width, height, rotation)
            }
        }
    }
}
