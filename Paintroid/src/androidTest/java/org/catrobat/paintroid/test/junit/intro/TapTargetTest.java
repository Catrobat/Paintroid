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

package org.catrobat.paintroid.test.junit.intro;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.getkeepsafe.taptargetview.TapTarget;

import junit.framework.Assert;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.intro.TapTargetBase;
import org.catrobat.paintroid.intro.TapTargetBottomBar;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.test.junit.EspressoHelpers;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class TapTargetTest extends TapTargetBaseTest {



    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp();
    }

    @Test
    public void numberTapTargetsBottomBar() throws NoSuchFieldException, IllegalAccessException {
        changePageFromLayoutResource(R.layout.islide_tools);
        TapTargetBottomBar tapTargetBottomBar = getTapTargetBottomBar();
        tapTargetBottomBar.initTargetView();

        HashMap<ToolType, TapTarget> tapTargetMap = getMapFromTapTarget(tapTargetBottomBar);

        Assert.assertEquals("TapTraget doesn't have same size. Tool is missing",
                numberOfVisibleChildern(targetItemView), tapTargetMap.size());

    }

    @Test
    public void numberTapTargetsTopBar() throws NoSuchFieldException, IllegalAccessException {
        changePageFromLayoutResource(R.layout.islide_possibilities);
        TapTargetTopBar tapTargetTopBar = getTapTargetTopBar();
        tapTargetTopBar.initTargetView();

        HashMap<ToolType, TapTarget> tapTargetMap = getMapFromTapTarget(tapTargetTopBar);

        Assert.assertEquals("TapTraget doesn't have same size. Tool is missing",
                numberOfVisibleChildern(targetItemView), tapTargetMap.size());

    }

    @Test
    public void testRadiusTopBar() throws NoSuchFieldException, IllegalAccessException {
        changePageFromLayoutResource(R.layout.islide_possibilities);
        EspressoHelpers.espressoWait(admirationDelay);
        TapTargetTopBar tapTargetTopBar = getTapTargetTopBar();

        int expectedRadius = getExpectedRadius(tapTargetTopBar);
        int actualRadius = (int) PrivateAccess.getMemberValue(TapTargetBase.class, tapTargetTopBar, "radius");

        assertEquals("Radius calculated Wrong",expectedRadius, actualRadius);
    }

    @Test
    public void testRadiusBottomBar() throws NoSuchFieldException, IllegalAccessException {
        changePageFromLayoutResource(R.layout.islide_tools);
        TapTargetBottomBar tapTargetBottomBar = getTapTargetBottomBar();

        int expectedRadius = getExpectedRadius(tapTargetBottomBar);
        int actualRadius = (int) PrivateAccess.getMemberValue(TapTargetBase.class, tapTargetBottomBar, "radius");

        assertEquals("Radius calculated Wrong",expectedRadius, actualRadius);

    }


}
