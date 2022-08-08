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

package org.catrobat.paintroid.test.junit.command

import android.content.Context
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory
import org.mockito.Mock
import org.junit.Before
import org.hamcrest.CoreMatchers
import org.catrobat.paintroid.command.implementation.CompositeCommand
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import org.catrobat.paintroid.command.implementation.AddEmptyLayerCommand
import org.catrobat.paintroid.command.implementation.SelectLayerCommand
import org.catrobat.paintroid.command.implementation.RemoveLayerCommand
import org.catrobat.paintroid.command.implementation.ReorderLayersCommand
import org.catrobat.paintroid.command.implementation.MergeLayersCommand
import org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection
import org.catrobat.paintroid.command.implementation.RotateCommand
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection
import org.catrobat.paintroid.command.implementation.FlipCommand
import org.catrobat.paintroid.command.implementation.CropCommand
import android.graphics.PointF
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.command.implementation.ResizeCommand
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.implementation.DefaultToolReference
import org.mockito.MockitoAnnotations
import org.catrobat.paintroid.command.implementation.ColorChangedCommand
import org.junit.Assert
import org.junit.Test

class DefaultCommandFactoryTest {
    private var commandFactory: DefaultCommandFactory? = null

    @Mock
    private val context: Context? = null

    @Before
    fun setUp() { commandFactory = DefaultCommandFactory() }

    @Test
    fun testCreateInitCommand() {
        val command = commandFactory?.createInitCommand(10, 20)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(CompositeCommand::class.java)))
    }

    @Test
    fun testCreateInitCommandWithBitmap() {
        val command = commandFactory?.createInitCommand(Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888))
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(CompositeCommand::class.java)))
    }

    @Test
    fun testCreateResetCommand() {
        val command = commandFactory?.createResetCommand()
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(CompositeCommand::class.java)))
    }

    @Test
    fun testCreateAddLayerCommand() {
        val command = commandFactory?.createAddEmptyLayerCommand()
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(AddEmptyLayerCommand::class.java)))
    }

    @Test
    fun testCreateSelectLayerCommand() {
        val command = commandFactory?.createSelectLayerCommand(0)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(SelectLayerCommand::class.java)))
    }

    @Test
    fun testCreateRemoveLayerCommand() {
        val command = commandFactory?.createRemoveLayerCommand(0)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(RemoveLayerCommand::class.java)))
    }

    @Test
    fun testCreateReorderLayersCommand() {
        val command = commandFactory?.createReorderLayersCommand(0, 1)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(ReorderLayersCommand::class.java)))
    }

    @Test
    fun testCreateMergeLayersCommand() {
        val command = commandFactory?.createMergeLayersCommand(0, 1)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(MergeLayersCommand::class.java)))
    }

    @Test
    fun testCreateRotateCommand() {
        val command = commandFactory?.createRotateCommand(RotateDirection.ROTATE_LEFT)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(RotateCommand::class.java)))
    }

    @Test
    fun testCreateFlipCommand() {
        val command = commandFactory?.createFlipCommand(FlipDirection.FLIP_HORIZONTAL)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(FlipCommand::class.java)))
    }

    @Test
    fun testCreateCropCommand() {
        val command = commandFactory?.createCropCommand(0, 0, 1, 1, 2)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(CropCommand::class.java)))
    }

    @Test
    fun testCreatePointCommand() {
        val command = commandFactory?.createPointCommand(Paint(), PointF(3f, 5f))
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(PointCommand::class.java)))
    }

    @Test
    fun testCreateResizeCommand() {
        val command = commandFactory?.createResizeCommand(10, 20)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(ResizeCommand::class.java)))
    }

    @Test
    fun testCreateColorChangedCommand() {
        val toolReference: ToolReference = DefaultToolReference()
        MockitoAnnotations.initMocks(this)
        val command = commandFactory?.createColorChangedCommand(toolReference, context!!, Color.WHITE)
        Assert.assertThat(command, CoreMatchers.`is`(CoreMatchers.instanceOf(ColorChangedCommand::class.java)))
    }
}
