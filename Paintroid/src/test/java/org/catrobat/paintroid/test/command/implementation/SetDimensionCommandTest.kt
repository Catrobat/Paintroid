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
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.command.implementation.SetDimensionCommand
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test

class SetDimensionCommandTest {
    @Test
    fun testRun() {
        val layerModel = LayerModel()
        val command = SetDimensionCommand(3, 4)
        command.run(Canvas(), layerModel)

        Assert.assertThat(layerModel.width, CoreMatchers.`is`(3))
        Assert.assertThat(layerModel.height, CoreMatchers.`is`(4))
    }
}
