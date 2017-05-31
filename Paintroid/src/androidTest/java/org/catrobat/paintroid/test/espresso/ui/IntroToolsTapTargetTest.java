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

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.espresso.util.IntroUtils;
import org.catrobat.paintroid.test.espresso.util.base.IntroTestBase;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntroToolsTapTargetTest extends IntroTestBase {

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        introSlide = IntroUtils.IntroSlide.Tools;
        super.setUpAndLaunchActivity();
        EspressoUtils.changeIntroPage(getPageIndexFormLayout(R.layout.islide_tools));
        EspressoUtils.waitMillis(100);
    }

    @Test
    public void testToolBrush() {
        IntroUtils.introClickToolAndCheckView(ToolType.BRUSH, introSlide);
    }

    @Test
    public void testToolShape() {
        IntroUtils.introClickToolAndCheckView(ToolType.SHAPE, introSlide);
    }

    @Test
    public void testToolTransform() {
        IntroUtils.introClickToolAndCheckView(ToolType.TRANSFORM, introSlide);
    }

    @Test
    public void testToolLine() {
        IntroUtils.introClickToolAndCheckView(ToolType.LINE, introSlide);
    }

    @Test
    public void testToolCursor() {
        IntroUtils.introClickToolAndCheckView(ToolType.CURSOR, introSlide);
    }

    @Test
    public void testToolFill() {
        IntroUtils.introClickToolAndCheckView(ToolType.FILL, introSlide);
    }

    @Test
    public void testToolPipette() {
        IntroUtils.introClickToolAndCheckView(ToolType.PIPETTE, introSlide);
    }

    @Test
    public void testToolStamp() {
        IntroUtils.introClickToolAndCheckView(ToolType.STAMP, introSlide);
    }

    @Test
    public void testToolImport() {
        IntroUtils.introClickToolAndCheckView(ToolType.IMPORTPNG, introSlide);
    }


    @Test
    public void testToolErase() {
        IntroUtils.introClickToolAndCheckView(ToolType.ERASER, introSlide);
    }

    @Test
    public void testToolText() {
        IntroUtils.introClickToolAndCheckView(ToolType.TEXT, introSlide);
    }

}
