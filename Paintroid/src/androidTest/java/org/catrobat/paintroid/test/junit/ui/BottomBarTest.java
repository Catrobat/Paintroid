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

package org.catrobat.paintroid.test.junit.ui;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.clickXY;
import static org.catrobat.paintroid.test.junit.stubs.EspressoHelpers.doesNotVisible;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class BottomBarTest {


    private ToolDialogReference[] toolsDialogues;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setup() {
        toolsDialogues = new ToolDialogReference[]{
                new ToolDialogReference(ToolType.BRUSH.getToolButtonID(), R.id.layout_stroke_dialog, 1),
                new ToolDialogReference(ToolType.LINE.getToolButtonID(), R.id.layout_stroke_dialog, 2),
                new ToolDialogReference(ToolType.ERASER.getToolButtonID(), R.id.layout_stroke_dialog, 2),
                new ToolDialogReference(ToolType.FLIP.getToolButtonID(), R.id.flip_horizontal_btn, 1),
                new ToolDialogReference(ToolType.FILL.getToolButtonID(), R.id.layout_fill_dialog, 2),
                new ToolDialogReference(ToolType.ROTATE.getToolButtonID(), R.id.layout_rotation_tool_buttons, 1),
                new ToolDialogReference(ToolType.TEXT.getToolButtonID(), R.id.layout_text_tool_dialog, 1)
        };
    }

    @Test
    public void closeDialogWithBackButton() {
        for (ToolDialogReference t : toolsDialogues) {
            openDialog(t);
            onView(withId(t.dialogId)).check(matches(isDisplayed()));
            pressBack();
            onView(withId(t.dialogId)).check(doesNotVisible());
        }
    }

    @Test
    public void closeDialogWithClickOnCanvas() {
        for (ToolDialogReference t : toolsDialogues) {
            View canvas = mActivityRule.getActivity().findViewById(R.id.drawingSurfaceView);
            int y = 0;
            if (canvas != null) {
                y = canvas.getHeight() / 8;
            }

            openDialog(t);
            onView(withId(t.dialogId)).check(matches((isDisplayed())));
            onView(withId(R.id.drawingSurfaceView)).perform(clickXY(0,y));
            onView(withId(t.dialogId)).check(doesNotVisible());
        }
    }

    @Test
    public void closeDialogWithClickOnIcon() {
        for (ToolDialogReference t : toolsDialogues) {
            openDialog(t);
            onView(withId(t.dialogId)).check(matches((isDisplayed())));
            clickButton(t);
            onView(withId(t.dialogId)).check(doesNotVisible());
        }
    }


    private void openDialog(ToolDialogReference t) {
        onView(withId(t.buttonId)).perform(scrollTo());
        for (int i = 0; i < t.clickCount; i++) {
            clickButton(t);
        }
    }

    private void clickButton(ToolDialogReference t) {
        onView(withId(t.buttonId)).perform(click());
    }

    private static class ToolDialogReference {
        final int buttonId;
        final int dialogId;
        final int clickCount;

        ToolDialogReference(int buttonId, int dialogId, int clickCount) {
            this.buttonId = buttonId;
            this.dialogId = dialogId;
            this.clickCount = clickCount;
        }
    }

}

