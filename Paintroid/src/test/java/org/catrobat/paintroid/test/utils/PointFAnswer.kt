/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.paintroid.test.utils

import android.graphics.PointF
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.mockito.stubbing.Stubber

class PointFAnswer(private val pointX: Float, private val pointY: Float) :
    Answer<Any?> {
    override fun answer(invocation: InvocationOnMock): Any? {
        val point = invocation.getArgument<PointF>(0)
        point.x = pointX
        point.y = pointY
        return null
    }

    companion object {
        fun setPointFTo(x: Float, y: Float): Stubber {
            return Mockito.doAnswer(PointFAnswer(x, y))
        }
    }
}