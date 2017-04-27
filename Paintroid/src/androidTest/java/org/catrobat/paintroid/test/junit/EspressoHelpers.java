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

package org.catrobat.paintroid.test.junit;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

public class EspressoHelpers {

    public static ViewAction clickXY(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                new CoordinatesProvider() {
                    @Override
                    public float[] calculateCoordinates(View view) {

                        final int[] screenPos = new int[2];
                        view.getLocationOnScreen(screenPos);

                        final float screenX = screenPos[0] + x;
                        final float screenY = screenPos[1] + y;
                        float[] coordinates = {screenX, screenY};

                        return coordinates;
                    }
                },
                Press.FINGER);
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

    public static void espressoWait(final long millis) {
        onView(isRoot()).perform(waitFor(millis));
    }

    public static ViewAssertion isNotVisible() {
        return new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noView) {
                if (view != null) {
                    boolean isRect = view.getGlobalVisibleRect(new Rect());
                    boolean isVisible = withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE).matches(view);
                    boolean retVal = !(isRect && isVisible);

                    assertThat("View is present in the hierarchy: " + HumanReadables.describe(view),
                            retVal, is(true));
                }
            }
        };
    }

    public static ViewAction selectViewPagerPage(final int pos) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(android.support.v4.view.ViewPager.class);
            }

            @Override
            public String getDescription() {
                return "select page in ViewPager";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((android.support.v4.view.ViewPager) view).setCurrentItem(pos);
            }
        };
    }

    public static Matcher<Object> equalsNumberDots(final int value) {
        return new BoundedMatcher<Object, LinearLayout>(LinearLayout.class) {
            private String layoutCount = null;

            @Override
            public void describeTo(Description description) {
                description.appendText("Number of dots does not match.\n");

                description.appendText("Expected: " + value);

                if (layoutCount != null) {
                    description.appendText("\nIs: " + layoutCount);
                }

            }

            @Override
            public boolean matchesSafely(LinearLayout layout) {
                layoutCount = String.valueOf(layout.getChildCount());
                return layout.getChildCount() == value;
            }
        };
    }

    public static Matcher<View> checkDotsColors(final int activeIndex, final int colorActive,
                                                final int colorInactive) {

        return new BoundedMatcher<View, LinearLayout>(LinearLayout.class) {
            private String errorTextView = null;
            private int currentIndex = -1;
            private int currentColor;
            private int expectedColor;

            @Override
            public boolean matchesSafely(LinearLayout layout) {
                for (currentIndex = 0; currentIndex < layout.getChildCount(); currentIndex++) {
                    TextView textView = (TextView) layout.getChildAt(currentIndex);

                    if (textView == null) {
                        errorTextView = "DotView is not TextView";
                        return false;
                    }

                    currentColor = textView.getCurrentTextColor();

                    if (currentIndex == activeIndex) {
                        if (currentColor != colorActive) {
                            expectedColor = colorActive;
                            return false;
                        }
                    } else {
                        if (currentColor != colorInactive) {
                            expectedColor = colorInactive;
                            return false;
                        }
                    }
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("\nAt Index: " + currentIndex);
                if (errorTextView != null) {
                    description.appendText("\nIs not a textview");
                    return;
                }

                description.appendText("Dot Color does not match ");
                description.appendText("\nExcepted: " + expectedColor);
                description.appendText("\nIs: " + currentColor);
            }
        };
    }

    public static Matcher<View> withDrawable(final int resourceId) {

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

                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
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

    public static class TapTargetVisibilityIdlingResource implements IdlingResource {

        private final String className;

        private boolean mIdle;
        private ResourceCallback mResourceCallback;

        public TapTargetVisibilityIdlingResource(final String className) {
            this.className = className;
            this.mIdle = false;
            this.mResourceCallback = null;
        }

        @Override
        public final String getName() {
            return TapTargetVisibilityIdlingResource.class.getSimpleName();
        }

        @Override
        public final boolean isIdleNow() {

            try {
                onView(allOf(withClassName(is(className)), isDisplayed()))
                .check(matches(isDisplayed()));

                mIdle = true;
            } catch (Exception e) {
                return mIdle;
            }


            if (mIdle) {
                if (mResourceCallback != null) {
                    Log.d("IDLE", "TT is visible");
                    mResourceCallback.onTransitionToIdle();
                }
            }

            return mIdle;
        }

        @Override
        public void registerIdleTransitionCallback(IdlingResource.ResourceCallback resourceCallback) {
            mResourceCallback = resourceCallback;
        }

    }

}
