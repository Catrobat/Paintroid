/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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

package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.MoveZoomTool;
import org.catrobat.paintroid.ui.Perspective;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.Before;
import org.junit.Test;

import android.graphics.PointF;

public class MoveZoomTest extends BaseToolTest {

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new MoveZoomTool(getActivity(), ToolType.MOVE);
		super.setUp();
	}

	public void testMove() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		float screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

		float offset = 50.0f;

		PointF fromPoint = new PointF(screenWidth / 2, screenHeight / 2);
		PointF toPoint = new PointF(fromPoint.x + offset, fromPoint.y + offset);

		float translationXBefore = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, "mSurfaceTranslationX");
		float translationYBefore = (Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, "mSurfaceTranslationY");

		mToolToTest.handleDown(fromPoint);
		mToolToTest.handleMove(toPoint);

		float translationXAfter = Math.round((Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, "mSurfaceTranslationX"));
		float translationYAfter = Math.round((Float) PrivateAccess.getMemberValue(Perspective.class,
				PaintroidApplication.perspective, "mSurfaceTranslationY"));

		assertEquals("translation of X should be the offset", translationXAfter - offset, translationXBefore);
		assertEquals("translation of Y should be the offset", translationYAfter - offset, translationYBefore);
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Move tool icon should be displayed", R.drawable.icon_menu_move, resource);
	}
}
