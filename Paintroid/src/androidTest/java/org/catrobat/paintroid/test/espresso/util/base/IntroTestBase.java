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

package org.catrobat.paintroid.test.junit.ui;

import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.Rule;

import java.util.Locale;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.Multilingual.setContextLocale;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.selectViewPagerPage;


public class IntroTestBase {
    protected Intent intent;
    protected WelcomeActivity activity;
    protected Context context;
    protected int[] layouts;
    protected int colorActive;
    protected int colorInactive;
    protected boolean rtl = false;

    @Rule
    public IntentsTestRule<WelcomeActivity> mActivityRule =
            new IntentsTestRule<>(WelcomeActivity.class, true, false);

    public void setUp() throws NoSuchFieldException, IllegalAccessException {

        intent = new Intent();
        context = getInstrumentation().getTargetContext();
        Session session = new Session(context);
        session.setFirstTimeLaunch(true);

        if(rtl){
            setContextLocale(context, "he");
        } else {
            setContextLocale(context, "");
        }

        mActivityRule.launchActivity(intent);
        activity = mActivityRule.getActivity();
        layouts = (int[]) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "layouts");
        colorActive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorActive");
        colorInactive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorInactive");
    }

    protected static void changePage(int page) {
        onView(withId(R.id.view_pager)).perform(selectViewPagerPage(page));
    }

    protected void changePageFromLayoutResource(int layoutResource) {
        onView(withId(R.id.view_pager)).
                perform(selectViewPagerPage(getPageIndexFormLayout(layoutResource)));
    }

    protected int getPageIndexFormLayout(final int layoutResource) throws IndexOutOfBoundsException {
        for (int i = 0; i < layouts.length; i++) {
            if(layouts[i] == layoutResource)
                return i;
        }

        throw new IndexOutOfBoundsException("No Index Found");
    }

    protected View getDescendantView(int ancestorResource, int targetResource) {
        return activity.findViewById(ancestorResource).findViewById(targetResource);
    }

    protected void checkSlideText(final int viewResource, final int stringResource) {
        onView(withId(viewResource)).check(matches(withText(stringResource)));
    }

}
