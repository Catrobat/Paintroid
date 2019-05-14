/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.drawable.DrawableShape;
import org.catrobat.paintroid.tools.drawable.DrawableStyle;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.catrobat.paintroid.test.espresso.util.OffsetLocationProvider.withOffset;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;

@RunWith(AndroidJUnit4.class)
public class ShapeToolIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);
	private ToolReference toolReference;
	private Workspace workspace;

	@Before
	public void setUp() {
		MainActivity activity = launchActivityRule.getActivity();
		toolReference = activity.toolReference;
		workspace = activity.workspace;

		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
	}

	@Test
	public void testEllipseIsDrawnOnBitmap() {
		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.OVAL);

		BaseToolWithRectangleShape ellipseTool = (BaseToolWithRectangleShape) toolReference.get();
		float rectHeight = ellipseTool.boxHeight;

		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.BLACK, withOffset(BitmapLocationProvider.MIDDLE, (int) (rectHeight / 2.5f), 0))
				.checkPixelColor(Color.TRANSPARENT, withOffset(BitmapLocationProvider.MIDDLE, (int) (rectHeight / 2.5f), (int) (rectHeight / 2.5f)));
	}

	@Test
	public void testUndoRedo() {
		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testFilledRectChangesColor() {
		onToolBarView()
				.performCloseToolOptionsView();

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_brown1);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.pocketpaint_color_picker_brown1, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testDrawWithHeartShape() {
		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.HEART);

		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testShapeWithOutlineAlsoWorksWithTransparentColor() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);
		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.RECTANGLE);
		onShapeToolOptionsView()
				.performSelectShapeDrawType(DrawableStyle.FILL);
		onToolProperties()
				.setColor(Color.BLACK);
		drawShape();
		onToolBarView()
				.performClickSelectedToolButton();

		onShapeToolOptionsView()
				.performSelectShape(DrawableShape.OVAL);
		onShapeToolOptionsView()
				.performSelectShapeDrawType(DrawableStyle.STROKE);
		onToolProperties()
				.setColor(Color.TRANSPARENT);
		drawShape();
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, DrawingSurfaceLocationProvider.TOOL_POSITION);
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, DrawingSurfaceLocationProvider.TOP_MIDDLE);
	}

	public void drawShape() {
		onToolBarView()
				.performCloseToolOptionsView();
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));
	}
}
