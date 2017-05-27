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
import android.widget.LinearLayout;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.espresso.util.base.TapTargetTestBase;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isNotVisible;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntroPossibilitiesTapTargetTest extends TapTargetTestBase {

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp(IntroSlide.Possibilities);
        setFirstTimeSequence(false);
        changePageFromLayoutResource(R.layout.islide_possibilities);
        EspressoUtils.waitMillis(100);
    }

    @Test
    public void testTopBarSequence() throws NoSuchFieldException, IllegalAccessException {
        setFirstTimeSequence(true);

        ViewInteraction tapTargetView = null;
        ViewInteraction fadeViewInteraction = onView(withId(R.id.intro_possibilities_textview));

        fadeViewInteraction.check(matches(isDisplayed()));
        EspressoUtils.waitMillis(animationDelay);
        for (int i = 0; i < getTopBarToolsView().getChildCount(); i++) {
            fadeViewInteraction.check(matches(not(isDisplayed())));
            tapTargetView = onView(allOf(withClassName(is(TT_CLASS_NAME)), isDisplayed()));
            tapTargetView.perform(click());
        }
        tapTargetView.check(isNotVisible());
        fadeViewInteraction.check(matches(isDisplayed()));
    }

    @Test
    public void testWithoutSequenceToolUndo() throws NoSuchFieldException, IllegalAccessException {
        onView(isRoot()).perform(touchCenterMiddle());
        clickToolAndCheckView(ToolType.UNDO);
    }
    @Test
    public void testWithoutSequenceToolRedo() throws NoSuchFieldException, IllegalAccessException {
        onView(isRoot()).perform(touchCenterMiddle());
        clickToolAndCheckView(ToolType.REDO);
    }
    @Test
    public void testWithoutSequenceToolLayer() throws NoSuchFieldException, IllegalAccessException {
        onView(isRoot()).perform(touchCenterMiddle());
        clickToolAndCheckView(ToolType.LAYER);
    }
    @Test
    public void testWithoutSequenceToolColorchooser() throws NoSuchFieldException, IllegalAccessException {
        onView(isRoot()).perform(touchCenterMiddle());
        clickToolAndCheckView(ToolType.COLORCHOOSER);
    }

    @Test
    public void testTapTargetSequenceAppearOnce() throws NoSuchFieldException, IllegalAccessException {
        setFirstTimeSequence(true);
        int pageIndex = getPageIndexFormLayout(R.layout.islide_possibilities);

        EspressoUtils.waitMillis(animationDelay);
        for (int i = 0; i < getTopBarToolsView().getChildCount(); i++) {
            onView(allOf(withClassName(is(TT_CLASS_NAME)), isDisplayed()))
                    .perform(click());
        }

        if (pageIndex < layouts.length) {
            onView(isRoot()).perform(swipeLeft());
            onView(isRoot()).perform(swipeRight());
        } else {
            onView(isRoot()).perform(swipeRight());
            onView(isRoot()).perform(swipeLeft());
        }

        onView(allOf(withClassName(is(TT_CLASS_NAME)))).check(isNotVisible());
        onView(withId(R.id.intro_possibilities_textview)).check(matches(isDisplayed()));
    }

}
