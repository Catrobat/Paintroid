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

package org.catrobat.paintroid.test.junit.stubs

import android.graphics.Path
import org.catrobat.paintroid.command.serialization.SerializablePath
import org.mockito.Mockito

class PathStub : SerializablePath() {
    private val stub: SerializablePath = Mockito.mock(SerializablePath::class.java)

    fun getStub(): Path = stub
    override fun reset() { stub.reset() }

    override fun rewind() { stub.rewind() }

    override fun moveTo(x: Float, y: Float) { stub.moveTo(x, y) }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) { stub.quadTo(x1, y1, x2, y2) }

    override fun lineTo(x: Float, y: Float) { stub.lineTo(x, y) }
}
