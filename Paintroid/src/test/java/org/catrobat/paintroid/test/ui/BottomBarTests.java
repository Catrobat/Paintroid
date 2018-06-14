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

package org.catrobat.paintroid.test.ui;

import android.content.res.Configuration;
import android.view.View;
import android.widget.LinearLayout;

import junit.framework.Assert;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.listener.BottomBarScrollListener;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.BottomBar;
import org.catrobat.paintroid.ui.BottomBarHorizontalScrollView;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BottomBarTests {
	@Mock
	private BottomBar.BottomBarCallback callback;

	@Mock
	private View bottomBarLayout;

	@Mock
	private LinearLayout toolsLayout;

	@Mock
	private BottomBarHorizontalScrollView scrollView;

	@Mock
	private View toolView;

	@Mock
	private Tool currentTool;

	@Before
	public void setUp() {
		when(currentTool.getToolType()).thenReturn(ToolType.BRUSH);
		when(toolsLayout.findViewById(ToolType.BRUSH.getToolButtonID())).thenReturn(toolView);
		PaintroidApplication.currentTool = currentTool;
	}

	@Test
	public void testInit() {
		when(toolsLayout.getChildCount()).thenReturn(1);
		when(toolsLayout.getChildAt(0)).thenReturn(toolView);

		BottomBar bottomBar = new BottomBar(callback, 1, Configuration.ORIENTATION_PORTRAIT, bottomBarLayout, toolsLayout, scrollView);
		verify(toolView).setOnLongClickListener(bottomBar);
		verify(toolView).setOnClickListener(bottomBar);
		verify(scrollView).setScrollStateListener(any(BottomBarScrollListener.class));
	}

	@Test
	public void testOrientationLandscape() {
		BottomBar bottomBar = new BottomBar(callback, 1, Configuration.ORIENTATION_LANDSCAPE, bottomBarLayout, toolsLayout, scrollView);
		Assert.assertNotNull(bottomBar);
		verifyNoMoreInteractions(scrollView);
	}

	@Test
	public void testClickSameTool() {
		BottomBar bottomBar = new BottomBar(callback, 1, Configuration.ORIENTATION_PORTRAIT, bottomBarLayout, toolsLayout, scrollView);
		when(toolView.getId()).thenReturn(ToolType.BRUSH.getToolButtonID());

		bottomBar.onClick(toolView);

		verify(currentTool).toggleShowToolOptions();
		verifyNoMoreInteractions(callback);
	}

	@Test
	public void testClickOtherTool() {
		BottomBar bottomBar = new BottomBar(callback, 1, Configuration.ORIENTATION_PORTRAIT, bottomBarLayout, toolsLayout, scrollView);
		when(toolView.getId()).thenReturn(ToolType.ERASER.getToolButtonID());
		when(callback.isKeyboardShown()).thenReturn(false);

		bottomBar.onClick(toolView);

		verify(currentTool, never()).toggleShowToolOptions();
		verify(callback).isKeyboardShown();
		verify(callback).switchTool(ToolType.ERASER);
		verifyNoMoreInteractions(callback);
	}

	@Test
	public void testClickOtherToolHidesKeyboard() {
		BottomBar bottomBar = new BottomBar(callback, 1, Configuration.ORIENTATION_PORTRAIT, bottomBarLayout, toolsLayout, scrollView);
		when(toolView.getId()).thenReturn(ToolType.ERASER.getToolButtonID());
		when(callback.isKeyboardShown()).thenReturn(true);

		bottomBar.onClick(toolView);

		verify(currentTool, never()).toggleShowToolOptions();
		verify(callback).isKeyboardShown();
		verify(callback).hideKeyboard();
		verifyNoMoreInteractions(callback);
	}

	@Test
	public void testClickInvalidView() {
		BottomBar bottomBar = new BottomBar(callback, 1, Configuration.ORIENTATION_PORTRAIT, bottomBarLayout, toolsLayout, scrollView);
		when(toolView.getId()).thenReturn(-1);

		bottomBar.onClick(toolView);

		verify(currentTool, never()).toggleShowToolOptions();
		verifyNoMoreInteractions(callback);
	}

	@Test
	public void testLongClickInvalidView() {
		BottomBar bottomBar = new BottomBar(callback, 1, Configuration.ORIENTATION_PORTRAIT, bottomBarLayout, toolsLayout, scrollView);
		when(toolView.getId()).thenReturn(-1);

		bottomBar.onLongClick(toolView);

		verify(currentTool, never()).toggleShowToolOptions();
		verifyNoMoreInteractions(callback);
	}
}
