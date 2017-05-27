/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.ui;

import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.espresso.util.base.TapTargetTestBase;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isNotVisible;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntroToolsTapTargetTest extends TapTargetTestBase {

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp(IntroSlide.Tools);
        changePageFromLayoutResource(R.layout.islide_tools);
        EspressoUtils.waitMillis(100);
    }

    @Test
    public void testToolBrush() {
        clickToolAndCheckView(ToolType.BRUSH);
    }

    @Test
    public void testToolShape() {
        clickToolAndCheckView(ToolType.SHAPE);
    }

    @Test
    public void testToolTransform() {
        clickToolAndCheckView(ToolType.TRANSFORM);
    }

    @Test
    public void testToolLine() {
        clickToolAndCheckView(ToolType.LINE);
    }

    @Test
    public void testToolCursor() {
        clickToolAndCheckView(ToolType.CURSOR);
    }

    @Test
    public void testToolFill() {
        clickToolAndCheckView(ToolType.FILL);
    }

    @Test
    public void testToolPipette() {
        clickToolAndCheckView(ToolType.PIPETTE);
    }

    @Test
    public void testToolStamp() {
        clickToolAndCheckView(ToolType.STAMP);
    }

    @Test
    public void testToolImport() {
        clickToolAndCheckView(ToolType.IMPORTPNG);
    }


    @Test
    public void testToolErase() {
        clickToolAndCheckView(ToolType.ERASER);
    }

    @Test
    public void testToolText() {
        clickToolAndCheckView(ToolType.TEXT);
    }

}
