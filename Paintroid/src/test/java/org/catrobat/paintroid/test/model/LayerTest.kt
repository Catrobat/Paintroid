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

package org.catrobat.paintroid.test.model

import android.graphics.Bitmap
import org.catrobat.paintroid.model.Layer
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LayerTest {
    @Mock
    private val firstBitmap: Bitmap? = null

    @Mock
    private val secondBitmap: Bitmap? = null

    @Test
    fun testGetBitmap() {
        val firstLayer = Layer(firstBitmap!!)
        val secondLayer = Layer(secondBitmap!!)

        Assert.assertEquals(firstBitmap, firstLayer.bitmap)
        Assert.assertEquals(secondBitmap, secondLayer.bitmap)
        Assert.assertTrue(firstLayer.isVisible)
        Assert.assertTrue(secondLayer.isVisible)
        Mockito.verify(secondBitmap)?.width
        Mockito.verify(secondBitmap)?.height
        Mockito.verify(firstBitmap)?.width
        Mockito.verify(firstBitmap)?.height
    }

    @Test
    fun testSetBitmap() {
        val layer = Layer(firstBitmap!!)
        layer.bitmap = secondBitmap!!

        Assert.assertEquals(secondBitmap, layer.bitmap)
        Assert.assertTrue(layer.isVisible)
        Mockito.verify(firstBitmap)?.width
        Mockito.verify(firstBitmap)?.height
    }
}
