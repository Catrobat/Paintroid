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

package org.catrobat.paintroid.test.junit.tools

import org.junit.runner.RunWith
import org.mockito.Mock
import org.catrobat.paintroid.tools.options.FillToolOptionsView
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.contract.LayerContracts
import org.mockito.InjectMocks
import org.catrobat.paintroid.tools.implementation.FillTool
import org.catrobat.paintroid.tools.ToolType
import org.junit.Assert
import org.junit.Test
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FillToolTest {
    @Mock
    var fillToolOptions: FillToolOptionsView? = null

    @Mock
    var contextCallback: ContextCallback? = null

    @Mock
    var toolOptionsViewController: ToolOptionsViewController? = null

    @Mock
    var workspace: Workspace? = null

    @Mock
    var toolPaint: ToolPaint? = null

    @Mock
    var commandManager: CommandManager? = null

    @Mock
    var commandFactory: CommandFactory? = null

    @Mock
    var layerModel: LayerContracts.Model? = null

    @InjectMocks
    var toolToTest: FillTool? = null

    @Test
    fun testShouldReturnCorrectToolType() {
        val toolType = toolToTest!!.toolType
        Assert.assertEquals(ToolType.FILL, toolType)
    }
}
