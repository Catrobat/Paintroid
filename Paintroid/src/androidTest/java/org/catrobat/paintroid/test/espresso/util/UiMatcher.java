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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 */

public final class UiMatcher {

    private UiMatcher() {

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

    public static Matcher<View> withProgress(final int progress) {

        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if(!(view instanceof SeekBar)) {
                    return false;
                }

                SeekBar seekbarView = ((SeekBar) view);

                int seekbarProgress = seekbarView.getProgress();

                return seekbarProgress == progress;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with progress: " + progress);
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

    public static Matcher<View> withChildren(final Matcher<Integer> numberOfChildrenMatcher) {

        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View target) {
                if (!(target instanceof ViewGroup)) {
                    return false;
                }

                return numberOfChildrenMatcher.matches(((ViewGroup) target).getChildCount());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with children # is ");
                numberOfChildrenMatcher.describeTo(description);
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
}