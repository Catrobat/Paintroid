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

import android.graphics.PointF;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.LineTool;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class LineToolTest {

	private LineTool tool;
	@Mock
	private CommandManager commandManager;
	@Mock
	private ToolPaint toolPaint;
	@Mock
	private BrushToolOptionsView brushToolOptions;
	@Mock
	private ToolOptionsVisibilityController toolOptionsController;
	@Mock
	private Workspace workspace;
	@Mock
	private ContextCallback contextCallback;

	@Before
	public void setUp() {
		tool = new LineTool(brushToolOptions, contextCallback, toolOptionsController, toolPaint, workspace, commandManager);
	}

	@Test
	public void testInternalStateGetsResetWithPathOuterWorkspace() {
		tool.handleDown(new PointF(-1, -1));
		tool.handleUp(new PointF(-2, -2));

		assertEquals(tool.getCurrentCoordinate(), null);
		assertEquals(tool.getInitialEventCoordinate(), null);
	}

	@Test
	public void testInternalStateGetsResetWithPathInWorkspace() {
		tool.handleDown(new PointF(1, 1));
		tool.handleUp(new PointF(2, 2));

		assertEquals(tool.getCurrentCoordinate(), null);
		assertEquals(tool.getInitialEventCoordinate(), null);
	}
}
