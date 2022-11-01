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

package org.catrobat.paintroid.test.command.implementation

import android.graphics.Canvas
import android.graphics.Paint
import org.junit.runner.RunWith
import org.mockito.Mock
import android.graphics.PointF
import org.mockito.InjectMocks
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.mockito.Mockito
import org.catrobat.paintroid.model.LayerModel
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PointCommandTest {
    @Mock
    private val paint: Paint? = null

    @Mock
    private val point: PointF? = null

    @InjectMocks
    private val command: PointCommand? = null

    @Test
    fun testSetUp() { Mockito.verifyZeroInteractions(paint, point) }

    @Test
    fun testDrawOnePoint() {
        val canvas = Mockito.mock(Canvas::class.java)
        val model: LayerContracts.Model = LayerModel()

        point?.x = 3f
        point?.y = 7f
        command?.run(canvas, model)

        Mockito.verify(canvas).drawPoint(3f, 7f, paint!!)
        Mockito.verifyZeroInteractions(paint)
        Mockito.verifyZeroInteractions(point)
    }
}
