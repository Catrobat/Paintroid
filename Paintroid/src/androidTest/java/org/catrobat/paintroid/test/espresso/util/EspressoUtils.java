package org.catrobat.paintroid.test.espresso.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.w3c.dom.Text;

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

    public static Matcher<View> hasChildPosition(final int position) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is child #" + position);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent viewParent = view.getParent();

                if(!(viewParent instanceof ViewGroup)) {
                    return false;
                }

                ViewGroup viewGroup = (ViewGroup) viewParent;
                return (viewGroup.indexOfChild(view) == position);
            }
        };
    }

    public static Matcher<View> hasTablePosition(final int rowIndex, final int columnIndex) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is child in cell @(" + rowIndex + "|" + columnIndex + ")");
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent tableRow = view.getParent();
                if(!(tableRow instanceof ViewGroup)) {
                    return false;
                }
                if(((ViewGroup) tableRow).indexOfChild(view) != columnIndex) {
                    return false;
                }

                ViewParent tableLayout = tableRow.getParent();
                if(!(tableLayout instanceof ViewGroup)) {
                    return false;
                }

                return (((ViewGroup) tableLayout).indexOfChild((TableRow)tableRow) == rowIndex);
            }
        };
    }

    public static Matcher<View> withBackgroundColor(final Matcher<Integer> colorMatcher) {

        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                ColorDrawable colorDrawable = ((ColorDrawable) view.getBackground());

                if(colorDrawable == null) {
                    return false;
                }

                int bgColor = colorDrawable.getColor();

                return colorMatcher.matches(bgColor);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: ");
                colorMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<View> withBackgroundColor(final int color) {

        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                ColorDrawable colorDrawable = ((ColorDrawable) view.getBackground());

                if(colorDrawable == null) {
                    return false;
                }

                int bgColor = colorDrawable.getColor();

                return (bgColor  == color);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with background color: " + color);
            }
        };
    }

    public static Matcher<View> withTextColor(final Matcher<Integer> colorMatcher) {

        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if(!(view instanceof TextView)) {
                    return false;
                }

                TextView textView = ((TextView) view);

                int textColor = textView.getCurrentTextColor();

                return colorMatcher.matches(textColor);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
                colorMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<View> withTextColor(final int color) {

        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if(!(view instanceof TextView)) {
                    return false;
                }

                TextView textView = ((TextView) view);

                int textColor = textView.getCurrentTextColor();

                return (textColor == color);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: " + color);
            }
        };
    }

    public static Matcher<View> withBackground(final int resourceId) {

        return new TypeSafeMatcher<View>() {
            String resourceName;

            @Override
            protected boolean matchesSafely(View target) {
                if (!(target instanceof ImageView)) {
                    return false;
                }
                ImageView imageView = (ImageView) target;
                Resources resources = target.getContext().getResources();
                Drawable expectedDrawable = resources.getDrawable(resourceId);
                resourceName = resources.getResourceEntryName(resourceId);

                if (expectedDrawable == null) {
                    return false;
                }

                if(imageView.getBackground() == null) {
                    return false;
                }

                Bitmap bitmap = ((BitmapDrawable) imageView.getBackground()).getBitmap();
                Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
                return bitmap.sameAs(otherBitmap);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id: ");
                description.appendValue(resourceId);
                if (resourceName != null) {
                    description.appendText("[");
                    description.appendText(resourceName);
                    description.appendText("]");
                }
            }
        };
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
