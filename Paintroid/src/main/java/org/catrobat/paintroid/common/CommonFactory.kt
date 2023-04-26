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
package org.catrobat.paintroid.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import org.catrobat.paintroid.command.serialization.SerializablePath

open class CommonFactory {
    open fun createCanvas() = Canvas()

    open fun createBitmap(width: Int, height: Int, config: Bitmap.Config): Bitmap =
        Bitmap.createBitmap(width, height, config)

    fun createPaint(paint: Paint?) = Paint(paint)

    fun createPointF(point: PointF) = PointF(point.x, point.y)

    fun createPoint(x: Int, y: Int) = Point(x, y)

    open fun createSerializablePath(path: SerializablePath): SerializablePath =
        SerializablePath(path)

    fun createRectF(rect: RectF?) = RectF(rect)
}
