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

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.tools.FontType

data class SerializableTypeface(val font: FontType, val bold: Boolean, val underline: Boolean, val italic: Boolean, val textSize: Float, val textSkewX: Float) {

    class TypefaceSerializer(version: Int) : VersionSerializer<SerializableTypeface>(version) {
        override fun write(kryo: Kryo, output: Output, typeface: SerializableTypeface) {
            with(output) {
                writeString(typeface.font.name)
                writeBoolean(typeface.bold)
                writeBoolean(typeface.underline)
                writeBoolean(typeface.italic)
                writeFloat(typeface.textSize)
                writeFloat(typeface.textSkewX)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out SerializableTypeface>): SerializableTypeface =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out SerializableTypeface>): SerializableTypeface {
            return with(input) {
                SerializableTypeface(FontType.valueOf(readString()), readBoolean(), readBoolean(), readBoolean(), readFloat(), readFloat())
            }
        }
    }
}
