/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.Integration.intro;

import android.app.Activity;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.intro.TapTargetBase;
import org.catrobat.paintroid.intro.TapTargetBottomBar;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.espresso.util.IntroUtils;
import org.catrobat.paintroid.test.espresso.util.base.IntroTestBase;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitMillis;
import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntroTapTargetIntegrationTest extends IntroTestBase {

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        startSequence = false;
        super.setUpAndLaunchActivity();
        waitMillis(100);
    }

    @Test
    public void numberTapTargetsBottomBar() throws NoSuchFieldException, IllegalAccessException {
        EspressoUtils.changeIntroPage(getPageIndexFormLayout(R.layout.islide_tools));
        LinearLayout targetItemView = IntroUtils.getBottomBarFromToolSlide(activity);
        TapTargetBottomBar tapTargetBottomBar = IntroUtils.getTapTargetBottomBar(activity);
        tapTargetBottomBar.initTargetView();
        HashMap<ToolType, TapTarget> tapTargetMap = IntroUtils.getMapFromTapTarget(tapTargetBottomBar);
        Assert.assertEquals("TapTarget doesn't have same size. Tool is missing",
                IntroUtils.numberOfVisibleChildren(targetItemView), tapTargetMap.size());

    }

    @Test
    public void numberTapTargetsTopBar() throws NoSuchFieldException, IllegalAccessException {
        EspressoUtils.changeIntroPage(getPageIndexFormLayout(R.layout.islide_possibilities));
        LinearLayout targetItemView = IntroUtils.getTopBarFromPossibilitiesSlide(activity);
        TapTargetTopBar tapTargetTopBar = IntroUtils.getTapTargetTopBar(activity);
        tapTargetTopBar.initTargetView();

        HashMap<ToolType, TapTarget> tapTargetMap = IntroUtils.getMapFromTapTarget(tapTargetTopBar);
        Assert.assertEquals("TapTarget doesn't have same size. Tool is missing",
                IntroUtils.numberOfVisibleChildren(targetItemView), tapTargetMap.size());

    }

    @Test
    public void testRadiusTopBar() throws NoSuchFieldException, IllegalAccessException {
        EspressoUtils.changeIntroPage(getPageIndexFormLayout(R.layout.islide_possibilities));
        EspressoUtils.waitMillis(200);
        TapTargetTopBar tapTargetTopBar = IntroUtils.getTapTargetTopBar(activity);
        int expectedRadius = IntroUtils.getExpectedRadiusForTapTarget(tapTargetTopBar);
        int actualRadius = (int) PrivateAccess.getMemberValue(TapTargetBase.class, tapTargetTopBar, "radius");

        assertEquals("Radius calculated Wrong",expectedRadius, actualRadius);
    }

    @Test
    public void testRadiusBottomBar() throws NoSuchFieldException, IllegalAccessException {
        EspressoUtils.changeIntroPage(getPageIndexFormLayout(R.layout.islide_tools));
        TapTargetBottomBar tapTargetBottomBar = IntroUtils.getTapTargetBottomBar(activity);
        int expectedRadius = IntroUtils.getExpectedRadiusForTapTarget(tapTargetBottomBar);
        int actualRadius = (int) PrivateAccess.getMemberValue(TapTargetBase.class, tapTargetBottomBar, "radius");

        assertEquals("Radius calculated Wrong",expectedRadius, actualRadius);
    }


}
