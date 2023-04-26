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

import android.content.Context
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.implementation.TextToolCommand
import org.catrobat.paintroid.tools.FontType

class TextToolCommandSerializer(version: Int, private val activityContext: Context) : VersionSerializer<TextToolCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: TextToolCommand) {
        with(kryo) {
            with(output) {
                writeObject(output, command.multilineText)
                writeObject(output, command.textPaint)
                writeFloat(command.boxOffset)
                writeFloat(command.boxWidth)
                writeFloat(command.boxHeight)
                writeObject(output, command.toolPosition)
                writeFloat(command.rotationAngle)
                writeObject(output, command.typeFaceInfo)
            }
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out TextToolCommand>): TextToolCommand =
        super.handleVersions(this, kryo, input, type)

    @Suppress("Detekt.TooGenericExceptionCaught")
    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out TextToolCommand>): TextToolCommand {
        return with(input) {
            val text = kryo.readObject(input, Array<String>::class.java)
            val paint = kryo.readObject(input, Paint::class.java)
            val offset = readFloat()
            val width = readFloat()
            val height = readFloat()
            val position = kryo.readObject(input, PointF::class.java)
            val rotation = readFloat()
            val typeFaceInfo = kryo.readObject(input, SerializableTypeface::class.java)

            paint.apply {
                isFakeBoldText = typeFaceInfo.bold
                isUnderlineText = typeFaceInfo.underline
                textSize = typeFaceInfo.textSize
                textSkewX = typeFaceInfo.textSkewX
                val style = if (typeFaceInfo.italic) Typeface.ITALIC else Typeface.NORMAL
                typeface = try {
                    when (typeFaceInfo.font) {
                        FontType.SANS_SERIF -> Typeface.create(Typeface.SANS_SERIF, style)
                        FontType.SERIF -> Typeface.create(Typeface.SERIF, style)
                        FontType.MONOSPACE -> Typeface.create(Typeface.MONOSPACE, style)
                        FontType.STC -> ResourcesCompat.getFont(activityContext, R.font.stc_regular)
                        FontType.DUBAI -> ResourcesCompat.getFont(activityContext, R.font.dubai)
                    }
                } catch (e: Exception) {
                    Log.e("LoadImageAsync", "Typeface not supported on this mobile phone")
                    Typeface.create(Typeface.SANS_SERIF, style)
                }
            }
            TextToolCommand(text, paint, offset, width, height, position, rotation, typeFaceInfo)
        }
    }
}
