package org.catrobat.paintroid.test.espresso.util;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.contrib.DrawerActions;
import android.view.View;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 *
 */
public final class EspressoUtils {

    public static void openNavigationDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
    }

    public static void closeNavigationDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
    }

    public static void selectTool(ToolType toolType) {
        ViewInteraction toolInteraction = onView(withId(toolType.getToolButtonID()))
            .perform(scrollTo());

        if(PaintroidApplication.currentTool.getToolType() != toolType) {
            toolInteraction.perform(click());
        }

        // Some test fail without wait
        waitMillis(1000);
    }

    public static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    public static void waitMillis(final long millis) {
        onView(isRoot()).perform(waitFor(millis));
    }

    public static void waitForIdleSync() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    }

    public static ViewAction touchAt(final int x, final int y) {
        return actionWithAssertions(
            new GeneralClickAction(Tap.SINGLE, new CoordinatesProvider() {
                @Override
                public float[] calculateCoordinates(View view) {
                    final int[] screenLocation = new int[2];
                    view.getLocationOnScreen(screenLocation);

                    final float touchX = screenLocation[0] + x;
                    final float touchY = screenLocation[1] + y;
                    float[] coordinates = {touchX, touchY};

                    return coordinates;
                }
            }, Press.FINGER)
        );
    }

    public static ViewAction touchAt(final float x, final float y) {
        return actionWithAssertions(
                new GeneralClickAction(Tap.SINGLE, new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {
                        final int[] screenLocation = new int[2];
                        view.getLocationOnScreen(screenLocation);

                        final float touchX = screenLocation[0] + x;
                        final float touchY = screenLocation[1] + y;
                        float[] coordinates = {touchX, touchY};

                        return coordinates;
                    }
                }, Press.FINGER)
        );
    }
}
