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

import android.graphics.Bitmap
import android.graphics.Canvas
import org.catrobat.paintroid.command.implementation.ResetCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.LayerModel
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ResetCommandTest {
    @Mock
    var canvas: Canvas? = null

    @InjectMocks
    var command: ResetCommand? = null
    private var layerModel: LayerContracts.Model? = null

    @Before
    fun setUp() { layerModel = LayerModel() }

    @Test
    fun testRunClearsLayers() {
        layerModel?.addLayerAt(0, Layer(mock(Bitmap::class.java)))
        layerModel?.addLayerAt(1, Layer(mock(Bitmap::class.java)))
        canvas?.let { layerModel?.let { it1 -> command?.run(it, it1) } }
        Assert.assertThat(layerModel?.layerCount, CoreMatchers.`is`(0))
    }
}
