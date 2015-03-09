/**
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

package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.helper.floodfill.FloodFillRange;
import org.catrobat.paintroid.tools.helper.floodfill.FloodFillRangeQueue;
import org.catrobat.paintroid.tools.helper.floodfill.QueueLinearFloodFiller;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Point;

public class FillToolTests extends BaseToolTest {

	protected PrivateAccess mPrivateAccess = new PrivateAccess();

	public FillToolTests() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new FillTool(getActivity(), ToolType.FILL);
		super.setUp();
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();
		assertEquals(ToolType.FILL, toolType);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonOne() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
		assertEquals("Transparent should be displayed", R.drawable.icon_menu_no_icon, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonTwo() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
		assertEquals("Color picker should be displayed", R.drawable.icon_menu_color_palette, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Fill tool icon should be displayed", R.drawable.icon_menu_bucket, resource);
	}

	@Test
	public void testFloodFillRangeInitIsCorrect() {
		int startX = 3;
		int endX = 4;
		int y = 5;

		FloodFillRange range = new FloodFillRange(startX, endX, y);

		assertEquals(startX, range.startX);
		assertEquals(endX, range.endX);
		assertEquals(y, range.y);
	}

	public void testFloodFillRangeQueueWorksCorrect() {
		FloodFillRange range1 = new FloodFillRange(3, 4, 5);
		FloodFillRange range2 = new FloodFillRange(6, 8, 5);

		FloodFillRangeQueue rangeQueue = new FloodFillRangeQueue(1);
		try {
			// test empty queue
			FloodFillRange[] array = (FloodFillRange[]) PrivateAccess.getMemberValue(FloodFillRangeQueue.class,
					rangeQueue, "mArray");
			Integer head = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mHead");
			Integer count = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mCount");

			assertEquals("Array should be empty", null, array[0]);
			assertEquals("Array size should be 1", 1, array.length);
			assertEquals("Head should be 0", 0, head.intValue());
			assertEquals("Head should be 0", 0, count.intValue());

			// add one element
			rangeQueue.addToEndOfQueue(range1);
			array = (FloodFillRange[]) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mArray");
			head = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mHead");
			count = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mCount");

			assertEquals("Array should contain 1 element", range1, array[0]);
			assertEquals("Array should contain 1 element", range1, rangeQueue.getFirst());
			assertEquals("Array size should be 1", 1, array.length);
			assertEquals("Head should be 0", 0, head.intValue());
			assertEquals("Count should be 1", 1, count.intValue());

			// add another element
			rangeQueue.addToEndOfQueue(range2);
			array = (FloodFillRange[]) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mArray");
			head = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mHead");
			count = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mCount");

			assertEquals("Array should contain range1", range1, array[0]);
			assertEquals("Array should contain range2 at the back", range2, array[1]);
			assertEquals("Array should contain 1 element", range1, rangeQueue.getFirst());
			assertEquals("Array size should be 2", 2, array.length);
			assertEquals("Head should be 0", 0, head.intValue());
			assertEquals("Count should be 2", 2, count.intValue());

			// delete one element
			FloodFillRange firstReturn = rangeQueue.removeAndReturnFirstElement();
			assertEquals("First element should be range1", range1, firstReturn);

			array = (FloodFillRange[]) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mArray");
			assertEquals("Array should still contain range2 at the back", range2, array[1]);
			assertEquals("First element of array should be null", null, array[0]);
			assertEquals("Array should now return range2 as first", range2, rangeQueue.getFirst());

			// delete second element
			FloodFillRange secondReturn = rangeQueue.removeAndReturnFirstElement();
			assertEquals("First element should be range1", range2, secondReturn);

			array = (FloodFillRange[]) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mArray");
			head = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mHead");
			count = (Integer) PrivateAccess.getMemberValue(FloodFillRangeQueue.class, rangeQueue, "mCount");
			assertEquals("First element of array should be null", null, array[0]);
			assertEquals("Second element of array should be null", null, array[1]);
			assertEquals("Array should now return null as first", null, rangeQueue.getFirst());

			assertEquals("Array size should be 2", 2, array.length);
			assertEquals("Head should be 2", 2, head.intValue());
			assertEquals("Count should be 0", 0, count.intValue());

			// delete another non existing element
			FloodFillRange thirdReturn = rangeQueue.removeAndReturnFirstElement();
			assertEquals("Should return null due empty queue", null, thirdReturn);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public void testQueueLinearFloodFiller() {
		int width = 3;
		int height = 3;
		Point clickedPoint = new Point(1, 1);
		int targetColor = 16777215;
		int replacementColor = 16757115;
		float selectionThreshold = 1.0f;

		int[] pixels = new int[width * height];
		for (int i = 0; i < (width * height); i++) {
			pixels[i] = targetColor;
		}

		QueueLinearFloodFiller.floodFill(pixels, width, height, clickedPoint, targetColor, replacementColor,
				selectionThreshold);

		for (int i = 0; i < (width * height); i++) {
			assertEquals("Color should have been replaced", pixels[i], replacementColor);
		}

	}
}
