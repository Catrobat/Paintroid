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
import android.graphics.PointF
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.command.implementation.SmudgePathCommand

class SmudgePathCommandSerializer(version: Int) : VersionSerializer<SmudgePathCommand>(version) {

    companion object {
        private const val COMPRESSION_QUALITY = 100
    }

    override fun write(kryo: Kryo, output: Output, command: SmudgePathCommand) {
        with(kryo) {
            with(output) {
                command.originalBitmap.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY, output)
                writeInt(command.pointPath.size)
                command.pointPath.forEach {
                    writeObject(output, it)
                }
                writeFloat(command.maxPressure)
                writeFloat(command.maxSize)
                writeFloat(command.minSize)
            }
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out SmudgePathCommand>): SmudgePathCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out SmudgePathCommand>): SmudgePathCommand {
        return with(kryo) {
            with(input) {
                val originalBitmap = BitmapFactory.decodeStream(input)
                val pointPath = mutableListOf<PointF>()
                val size = readInt()
                repeat(size) {
                    pointPath.add(readObject(input, PointF::class.java))
                }
                val maxPressure = readFloat()
                val maxSize = readFloat()
                val minSize = readFloat()
                SmudgePathCommand(originalBitmap, pointPath, maxPressure, maxSize, minSize)
            }
        }
    }
}
