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

package org.catrobat.paintroid.test.espresso.intro.base;

import android.content.Context;
import android.content.Intent;

import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.espresso.util.IntroUtils;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.Rule;

import static android.support.test.InstrumentationRegistry.getInstrumentation;


public class IntroTestBase {
    protected Intent intent;
    protected WelcomeActivity activity;
    protected Context context;
    protected static int[] layouts;
    protected int colorActive;
    protected int colorInactive;
    protected boolean rtl = false;
    protected boolean startSequence = true;
    protected IntroUtils.IntroSlide introSlide;

    @Rule
    public WelcomeActivityIntentsTestRule<WelcomeActivity> mActivityRule = new WelcomeActivityIntentsTestRule<>(WelcomeActivity.class, true, false);

    @Rule
    public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

    public void setUpAndLaunchActivity() throws NoSuchFieldException, IllegalAccessException {
        intent = new Intent();
        context = getInstrumentation().getTargetContext();
        EspressoUtils.shouldStartSequence(startSequence);
        EspressoUtils.setRtl(rtl);

        mActivityRule.launchActivity(intent);
        activity = mActivityRule.getActivity();
        layouts = (int[]) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "layouts");
        colorActive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorActive");
        colorInactive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorInactive");
    }


    protected static int getPageIndexFormLayout(final int layoutResource) throws IndexOutOfBoundsException {
        for (int i = 0; i < layouts.length; i++) {
            if (layouts[i] == layoutResource)
                return i;
        }

        throw new IndexOutOfBoundsException("No Index Found");
    }
}
