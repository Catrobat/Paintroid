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

package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.tools.options.FillToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FillToolTest {
	@Mock
	public FillToolOptionsView fillToolOptions;
	@Mock
	public ContextCallback contextCallback;
	@Mock
	public ToolOptionsVisibilityController toolOptionsViewController;
	@Mock
	public Workspace workspace;
	@Mock
	public ToolPaint toolPaint;
	@Mock
	public CommandManager commandManager;
	@Mock
	public CommandFactory commandFactory;
	@Mock
	public LayerContracts.Model layerModel;
	@InjectMocks
	public FillTool toolToTest;

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = toolToTest.getToolType();
		assertEquals(ToolType.FILL, toolType);
	}
}
