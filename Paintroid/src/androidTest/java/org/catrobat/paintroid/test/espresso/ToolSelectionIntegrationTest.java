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

package org.catrobat.paintroid.test.espresso;

import android.graphics.Bitmap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import junit.framework.AssertionFailedError;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getStatusbarHeight;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitForToast;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.clickOutside;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ToolSelectionIntegrationTest {
	private static final int START = R.id.tools_brush;
	private static final int MIDDLE = R.id.tools_pipette;
	private static final int END = R.id.tools_text;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;
	private HorizontalScrollView scrollView;
	private LinearLayout toolsLayout;

	@Before
	public void setUp() {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		toolsLayout = (LinearLayout) launchActivityRule.getActivity().findViewById(R.id.tools_layout);
		scrollView = (HorizontalScrollView) launchActivityRule.getActivity().findViewById(R.id.bottom_bar_scroll_view);

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {
		IndeterminateProgressDialog.getInstance().dismiss();
		ColorPickerDialog.getInstance().dismiss();

		activityHelper = null;
	}

	protected Tool getCurrentTool() {
		return PaintroidApplication.currentTool;
	}

	protected boolean toolOptionsAreShown() {
		return getCurrentTool().getToolOptionsAreShown();
	}

	protected int getNumberOfNotVisibleTools() {
		LinearLayout toolsLayout = (LinearLayout) launchActivityRule.getActivity().findViewById(R.id.tools_layout);
		int toolCount = toolsLayout.getChildCount();
		int numberOfNotVisibleTools = 0;
		for (int i = 0; i < toolCount; i++) {
			View toolButton = toolsLayout.getChildAt(i);
			if (!toolButton.isShown()) {
				numberOfNotVisibleTools++;
			}
		}
		return numberOfNotVisibleTools;
	}

	public ToolType getToolTypeByButtonId(int id) {
		ToolType retToolType = null;

		for (ToolType toolType : ToolType.values()) {
			if (toolType.getToolButtonID() == id) {
				retToolType = toolType;
				break;
			}
		}

		assertNotNull(retToolType);

		return retToolType;
	}

	@Test
	public void testToolSelectionDrawingSurfaceDeactivatedWhenToolOptionsAreShown() throws NoSuchFieldException, IllegalAccessException {
		Bitmap currentDrawingSurfaceBitmap = getWorkingBitmap();

		int pixelBefore = currentDrawingSurfaceBitmap.getPixel(
				currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2
		);

		float posX = activityHelper.getDisplayWidth() / 2.0f;
		float posY = launchActivityRule.getActivity().findViewById(R.id.main_tool_options).getY() + getStatusbarHeight() - 10;

		UiInteractions.touchAt(posX, posY);

		int pixelAfter = currentDrawingSurfaceBitmap.getPixel(
				currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2
		);

		assertEquals("Drawing Surface should have been deactivated", pixelBefore, pixelAfter);
		assertFalse("Tool options should not be displayed", toolOptionsAreShown());
	}

	@Test
	public void testToolSelectionToolOptionsDisappearWhenClickedOutside() {
		onToolBarView()
				.performOpenToolOptions();

		onView(withId(R.id.layout_tool_options))
				.check(matches(isDisplayed()))
				.perform(clickOutside(UiInteractions.Direction.ABOVE))
				.check(matches(not(isDisplayed())));
	}

	// TODO: Fails now an then, tool view not visible
	@Test
	public void testToolSelectionToolButtonCheckPosition() {
		int toolCount = toolsLayout.getChildCount() - getNumberOfNotVisibleTools();
		View toolButton = toolsLayout.getChildAt(toolCount / 2);

		ToolType toolInMiddle = getToolTypeByButtonId(toolButton.getId());

		if (toolsLayout.getWidth() > scrollView.getWidth() + toolsLayout.getChildAt(0).getWidth()) {
			selectTool(toolInMiddle);

			int[] screenLocation = new int[2];
			toolButton.getLocationOnScreen(screenLocation);

			assertEquals("Tool button should be centered", activityHelper.getDisplayWidth() / 2, screenLocation[0] + toolButton.getWidth() / 2);
		}

		int scrollRight = 1;
		int scrollLeft = -1;

		View leftMostButton = toolsLayout.getChildAt(0);
		ToolType leftMostTool = getToolTypeByButtonId(leftMostButton.getId());

		selectTool(leftMostTool);

		assertFalse("Tool button should be most left", scrollView.canScrollHorizontally(scrollLeft));

		View rightMostButton = toolsLayout.getChildAt(toolCount - 1);
		ToolType rightMostTool = getToolTypeByButtonId(rightMostButton.getId());

		selectTool(rightMostTool);

		assertFalse("Tool button should be most right", scrollView.canScrollHorizontally(scrollRight));
	}

	// TODO: how to implement?
	@Test
	@Ignore
	public void testToolSelectionStartAnimation() {
		int scrollX = scrollView.getScrollX();
		assertTrue("Scroll position should be > 0 at start", scrollX > 0);

		for (int i = 0; i < 5; i++) {
			assertTrue(scrollView.getScrollX() <= scrollX);
			scrollX = scrollView.getScrollX();
		}

		assertEquals("Animation should be finished after a second", 0, scrollX);
	}

	@Test
	public void testToolSelectionToast() {
		ToolType toolType = ToolType.CURSOR;
		onToolBarView()
				.performSelectTool(toolType);

		waitForToast(withText(toolType.getNameResource()), 1000);
	}

	@Test
	public void testToolSelectionNextArrowDisplayed() {
		try {
			onView(withId(R.id.bottom_next))
					.check(matches(isCompletelyDisplayed()));
		} catch (AssertionFailedError e) {
			onView(withId(START))
					.check(matches(isCompletelyDisplayed()));
			onView(withId(END))
					.check(matches(isCompletelyDisplayed()));
		}
	}

	@Test
	public void testToolSelectionPreviousArrowNotDisplayed() {
		onView(withId(R.id.bottom_previous))
				.check(matches(not(isDisplayed())));
	}

	@Test
	public void testToolSelectionPreviousArrowDisplayedOnEnd() {
		onView(withId(END))
				.perform(scrollTo());
		try {
			onView(withId(R.id.bottom_previous))
					.check(matches(isCompletelyDisplayed()));
			onView(withId(R.id.bottom_next))
					.check(matches(not(isDisplayed())));
		} catch (AssertionFailedError e) {
			onView(withId(START))
					.check(matches(isCompletelyDisplayed()));
			onView(withId(END))
					.check(matches(isCompletelyDisplayed()));
		}
	}

	@Test
	public void testToolSelectionNextArrowNotDisplayedOnEnd() {
		onView(withId(START))
				.perform(scrollTo());
		try {
			onView(withId(R.id.bottom_previous))
					.check(matches(not(isDisplayed())));
			onView(withId(R.id.bottom_next))
					.check(matches(isCompletelyDisplayed()));
		} catch (AssertionFailedError e) {
			onView(withId(START))
					.check(matches(isCompletelyDisplayed()));
			onView(withId(END))
					.check(matches(isCompletelyDisplayed()));
		}
	}

	@Test
	public void previousAndNextDisplayedOnScrollToMiddle() {
		onView(withId(MIDDLE))
				.perform(scrollTo());
		try {
			onView(withId(R.id.bottom_previous))
					.check(matches(isCompletelyDisplayed()));
			onView(withId(R.id.bottom_next))
					.check(matches(isCompletelyDisplayed()));
		} catch (AssertionFailedError e) {

			onView(withId(START))
					.check(matches(isCompletelyDisplayed()));
			onView(withId(END))
					.check(matches(isCompletelyDisplayed()));
		}
	}
}
