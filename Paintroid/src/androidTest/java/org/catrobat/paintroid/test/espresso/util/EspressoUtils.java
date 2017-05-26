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

package org.catrobat.paintroid.test.espresso.util;

import android.graphics.PointF;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.contrib.DrawerActions;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 *
 */
public final class EspressoUtils {

    /**
     * Field name for surface width of {@link Perspective} class. Use {@link #getSurfaceHeight()} to
     * get the value
     */
    public static final String FIELD_NAME_SURFACE_WIDTH  = "mSurfaceWidth";

    /**
     * Field name for surface height of {@link Perspective} class. Use {@link #getSurfaceHeight()} to
     * get the value
     */
    public static final String FIELD_NAME_SURFACE_HEIGHT = "mSurfaceHeight";

    public static void openNavigationDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    }

    public static void closeNavigationDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
    }

    public static float getActionbarHeight() {
        return Utils.getActionbarHeight();
    }

    public static float getStatusbarHeight() {
        return Utils.getStatusbarHeight();
    }

    public static PointF getSurfacePointFromScreenPoint(PointF screenPoint) {
        return Utils.getSurfacePointFromScreenPoint(screenPoint);
    }

    public static PointF getCanvasPointFromScreenPoint(PointF screenPoint) {
        return Utils.getCanvasPointFromScreenPoint(screenPoint);
    }

    public static PointF getCanvasPointFromSurfacePoint(PointF surfacePoint) {
        return PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(surfacePoint);
    }

    public static void selectTool(ToolType toolType) {
        ViewInteraction toolInteraction = onView(withId(toolType.getToolButtonID()))
            .perform(scrollTo());

        if(PaintroidApplication.currentTool.getToolType() != toolType) {
            toolInteraction.perform(click());
        }

        // Some test fail without wait
        waitMillis(500);
    }

    public static void longClickOnTool(ToolType toolType) {
        ViewInteraction toolInteraction = onView(withId(toolType.getToolButtonID()))
                .perform(scrollTo());

        toolInteraction.perform(longClick());

        // Some test fail without wait
        waitMillis(500);
    }

    public static float getSurfaceWidth() throws NoSuchFieldException, IllegalAccessException {
        return (float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, FIELD_NAME_SURFACE_WIDTH);
    }

    public static float getSurfaceHeight() throws NoSuchFieldException, IllegalAccessException {
        return (float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective, FIELD_NAME_SURFACE_HEIGHT);
    }

    public static void openToolOptionsForCurrentTool() {
        clickSelectedToolButton();
    }

    public static void clickSelectedToolButton() {
        onView(withId(PaintroidApplication.currentTool.getToolType().getToolButtonID())).perform(click());
    }

    public static void waitMillis(final long millis) {
        onView(isRoot()).perform(UiInteractions.waitFor(millis));
    }

    public static void waitForIdleSync() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

}
