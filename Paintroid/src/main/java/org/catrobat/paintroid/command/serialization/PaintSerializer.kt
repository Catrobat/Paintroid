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
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint

class PaintSerializer(version: Int, private val activityContext: Context) : VersionSerializer<Paint>(version) {
    override fun write(kryo: Kryo, output: Output, paint: Paint) {
        with(output) {
            writeInt(paint.color)
            writeFloat(paint.strokeWidth)
            writeInt(paint.strokeCap.ordinal)
            writeBoolean(paint.isAntiAlias)
            writeInt(paint.style.ordinal)
            writeInt(paint.strokeJoin.ordinal)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out Paint>): Paint =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Paint>): Paint {
        val toolPaint = DefaultToolPaint(activityContext).apply {
            with(input) {
                color = readInt()
                strokeWidth = readFloat()
                strokeCap = Paint.Cap.values()[readInt()]
            }
        }
        return toolPaint.paint.apply {
            with(input) {
                isAntiAlias = readBoolean()
                style = Paint.Style.values()[readInt()]
                strokeJoin = Paint.Join.values()[readInt()]
            }
        }
    }
}
