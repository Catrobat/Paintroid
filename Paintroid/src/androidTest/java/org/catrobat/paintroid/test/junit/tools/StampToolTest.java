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
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewConfiguration;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StampToolTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	private StampTool tool;

	@UiThreadTest
	@Before
	public void setUp() {
		MainActivity activity = activityTestRule.getActivity();
		ToolPaint toolPaint = activity.toolPaint;
		CommandManager commandManager = activity.commandManager;
		Workspace workspace = activity.workspace;
		tool = new StampTool(activity, toolPaint, workspace, commandManager);
	}

	@Test
	public void testLongClickResetsToolPosition() throws Throwable {

		final float initialX = tool.toolPosition.x;
		final float initialY = tool.toolPosition.y;

		activityTestRule.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tool.handleDown(new PointF(initialX, initialY));
				tool.handleMove(new PointF(initialX + 2, initialY + 3));
			}
		});

		assertEquals(initialX + 2, tool.toolPosition.x, Float.MIN_VALUE);
		assertEquals(initialY + 3, tool.toolPosition.y, Float.MIN_VALUE);

		Thread.sleep(ViewConfiguration.getLongPressTimeout() + 50);

		assertEquals(initialX, tool.toolPosition.x, Float.MIN_VALUE);
		assertEquals(initialY, tool.toolPosition.y, Float.MIN_VALUE);
	}
}
