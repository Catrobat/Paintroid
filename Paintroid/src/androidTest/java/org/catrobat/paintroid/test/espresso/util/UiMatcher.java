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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.VectorDrawable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.util.HumanReadables;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;

public final class UiMatcher {

	private UiMatcher() {
	}

	public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
		return new TypeSafeMatcher<View>() {
			int currentIndex = 0;

			@Override
			public void describeTo(Description description) {
				description.appendText("with index: ");
				description.appendValue(index);
				matcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				return matcher.matches(view) && currentIndex++ == index;
			}
		};
	}

	public static Matcher<View> hasTypeFace(final Typeface typeface) {
		return new TypeSafeMatcher<View>() {

			@Override
			protected boolean matchesSafely(final View view) {
				return view instanceof TextView && ((TextView) view).getTypeface() == typeface;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("the selected TextView doesn't have the TypeFace:" + typeface);
			}
		};
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

				if (!(viewParent instanceof ViewGroup)) {
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
				if (!(tableRow instanceof ViewGroup)) {
					return false;
				}
				if (((ViewGroup) tableRow).indexOfChild(view) != columnIndex) {
					return false;
				}

				ViewParent tableLayout = tableRow.getParent();
				if (!(tableLayout instanceof ViewGroup)) {
					return false;
				}

				return (((ViewGroup) tableLayout).indexOfChild((TableRow) tableRow) == rowIndex);
			}
		};
	}

	public static Matcher<View> withBackgroundColor(final Matcher<Integer> colorMatcher) {

		return new TypeSafeMatcher<View>() {
			@Override
			protected boolean matchesSafely(View view) {
				ColorDrawable colorDrawable = ((ColorDrawable) view.getBackground());

				if (colorDrawable == null) {
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
				Drawable background = view.getBackground();

				if (background == null) {
					return false;
				}

				if (background instanceof ColorDrawable) {
					return color == ((ColorDrawable) background).getColor();
				} else if (background instanceof LayerDrawable) {
					LayerDrawable layerDrawable = (LayerDrawable) background;
					Drawable drawable = layerDrawable.getDrawable(0);
					return (drawable instanceof ColorDrawable
							&& color == ((ColorDrawable) drawable).getColor());
				}
				return false;
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
				if (!(view instanceof TextView)) {
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
				if (!(view instanceof TextView)) {
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
				if (!(view instanceof SeekBar)) {
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
				Resources resources = target.getContext().getResources();
				resourceName = resources.getResourceEntryName(resourceId);

				if (!(target instanceof ImageView)) {
					return false;
				}

				Drawable expectedDrawable = resources.getDrawable(resourceId);
				Drawable targetDrawable = target.getBackground();

				if (expectedDrawable == null || targetDrawable == null) {
					return false;
				}

				Bitmap expectedBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();

				if (targetDrawable instanceof BitmapDrawable) {
					Bitmap bitmap = ((BitmapDrawable) targetDrawable).getBitmap();
					return bitmap.sameAs(expectedBitmap);
				} else if (targetDrawable instanceof StateListDrawable) {
					Bitmap bitmap = ((BitmapDrawable) targetDrawable.getCurrent()).getBitmap();
					return bitmap.sameAs(expectedBitmap);
				}
				return false;
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
				Resources resources = target.getContext().getResources();
				resourceName = resources.getResourceEntryName(resourceId);

				if (!(target instanceof ImageView)) {
					return false;
				}

				Drawable expectedDrawable = resources.getDrawable(resourceId);
				ImageView targetImageView = (ImageView) target;
				Drawable targetDrawable = targetImageView.getDrawable();

				if (expectedDrawable == null || targetDrawable == null) {
					return false;
				}

				Bitmap expectedBitmap;

				if (targetDrawable instanceof BitmapDrawable) {
					Bitmap targetBitmap = ((BitmapDrawable) targetDrawable).getBitmap();
					expectedBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
					return targetBitmap.sameAs(expectedBitmap);
				} else if (targetDrawable instanceof VectorDrawable || targetDrawable instanceof VectorDrawableCompat) {
					Bitmap targetBitmap = vectorToBitmap((VectorDrawable) expectedDrawable);
					expectedBitmap = vectorToBitmap((VectorDrawable) expectedDrawable);
					return targetBitmap.sameAs(expectedBitmap);
				} else if (targetDrawable instanceof StateListDrawable) {
					Bitmap targetBitmap = vectorToBitmap((VectorDrawable) expectedDrawable);
					expectedBitmap = vectorToBitmap((VectorDrawable) expectedDrawable);
					return targetBitmap.sameAs(expectedBitmap);
				}
				return false;
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

			private Bitmap vectorToBitmap(VectorDrawable vectorDrawable) {
				return Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
						vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			}
		};
	}

	/**
	 * Matches {@link Root}s that are toasts (i.e. is not a window of the currently resumed activity).
	 *
	 * @see androidx.test.espresso.matcher.RootMatchers#isDialog()
	 */
	public static Matcher<Root> isToast() {
		return new TypeSafeMatcher<Root>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("is toast");
			}

			@Override
			public boolean matchesSafely(Root root) {
				int type = root.getWindowLayoutParams().get().type;
				return (type == WindowManager.LayoutParams.TYPE_TOAST);
			}
		};
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

	public static Matcher<View> isOnLeftSide() {
		return new TypeSafeMatcher<View>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("View is not on the Left Side");
			}

			@Override
			public boolean matchesSafely(View view) {
				int displayMiddle = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
				int viewStartX = (int) view.getX();
				int viewEndX = viewStartX + view.getWidth();

				return (viewStartX < displayMiddle) && (viewEndX < displayMiddle);
			}
		};
	}

	public static Matcher<View> isOnRightSide() {
		return new TypeSafeMatcher<View>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("View is not on the Right Side");
			}

			@Override
			public boolean matchesSafely(View view) {
				int displayMiddle = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
				int viewStartX = (int) view.getX();
				int viewEndX = viewStartX + view.getWidth();

				return (viewStartX > displayMiddle) && (viewEndX > displayMiddle);
			}
		};
	}

	public static Matcher<View> withAdaptedData(final int resourceId) {
		return new TypeSafeMatcher<View>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("with class name: ");
			}

			@Override
			public boolean matchesSafely(View view) {
				String resourceName;

				if (!(view instanceof AdapterView)) {
					return false;
				}

				Resources resources = view.getContext().getResources();
				resourceName = resources.getString(resourceId);

				@SuppressWarnings("rawtypes")
				Adapter adapter = ((AdapterView) view).getAdapter();
				for (int i = 0; i < adapter.getCount(); i++) {
					if (resourceName.equals(((MenuItem) adapter.getItem(i)).getTitle().toString())) {
						return true;
					}
				}

				return false;
			}
		};
	}
}
