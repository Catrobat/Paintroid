/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util;

import android.graphics.PointF;
import android.graphics.Rect;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.MotionEvents;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.ScrollToAction;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.action.Tapper;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.v4.view.ViewPager;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;

import org.hamcrest.Matcher;

import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;

import static org.catrobat.paintroid.test.espresso.util.CustomSwiper.ACCURATE;
import static org.hamcrest.Matchers.is;

public final class UiInteractions {

	private UiInteractions() {
	}

	public static ViewAction unconstrainedScrollTo() {
		return actionWithAssertions(new UnconstrainedScrollToAction());
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

	public static ViewAssertion assertListViewCount(final int expectedCount) {
		return new ViewAssertion() {
			@Override
			public void check(View view, NoMatchingViewException noViewFoundException) {
				if (noViewFoundException != null) {
					throw noViewFoundException;
				}

				ListAdapter adapter = ((ListView) view).getAdapter();
				assertThat(adapter.getCount(), is(expectedCount));
			}
		};
	}

	public static ViewAction setProgress(final int progress) {
		return new ViewAction() {

			@Override
			public Matcher<View> getConstraints() {
				return isAssignableFrom(SeekBar.class);
			}

			@Override
			public String getDescription() {
				return "Set a progress";
			}

			@Override
			public void perform(UiController uiController, View view) {
				if (!(view instanceof SeekBar)) {
					return;
				}

				((SeekBar) view).setProgress(progress);
			}
		};
	}

	public static ViewAction clickOutside(final Direction direction) {
		return actionWithAssertions(
				new GeneralClickAction(Tap.SINGLE, new CoordinatesProvider() {
					@Override
					public float[] calculateCoordinates(View view) {
						Rect r = new Rect();
						view.getGlobalVisibleRect(r);
						switch (direction) {
							case ABOVE:
								return new float[]{r.centerX(), r.top - 50};
							case BELOW:
								return new float[]{r.centerX(), r.bottom + 50};
							case LEFT:
								return new float[]{r.left - 50, r.centerY()};
							case RIGHT:
								return new float[]{r.right + 50, r.centerY()};
						}
						return null;
					}
				}, Press.FINGER, 0, 1)
		);
	}

	public static ViewAction touchAt(final CoordinatesProvider provider) {
		return actionWithAssertions(
				new GeneralClickAction(Tap.SINGLE, provider, Press.FINGER, 0, 0));
	}

	public static ViewAction touchAt(final PointF coordinates) {
		return touchAt(coordinates, Tap.SINGLE);
	}

	public static ViewAction touchAt(final int x, final int y) {
		return touchAt((float) x, (float) y);
	}

	public static ViewAction touchAt(final float x, final float y) {
		return touchAt(x, y, Tap.SINGLE);
	}

	public static ViewAction touchLongAt(final PointF coordinates) {
		return touchAt(coordinates, Tap.LONG);
	}

	public static ViewAction touchLongAt(final float x, final float y) {
		return touchAt(x, y, Tap.LONG);
	}

	public static ViewAction touchAt(final PointF coordinates, final Tapper tapStyle) {
		return touchAt(coordinates.x, coordinates.y, tapStyle);
	}

	public static ViewAction touchAt(final float x, final float y, final Tapper tapStyle) {
		return actionWithAssertions(
				new GeneralClickAction(tapStyle, PositionCoordinatesProvider.at(x, y), Press.FINGER, 0, 0)
		);
	}

	public static ViewAction touchCenterLeft() {
		return new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_LEFT, Press.FINGER, 0, 0);
	}

	public static ViewAction touchCenterMiddle() {
		return new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER, Press.FINGER, 0, 0);
	}

	public static ViewAction touchCenterRight() {
		return new GeneralClickAction(Tap.SINGLE, GeneralLocation.CENTER_RIGHT, Press.FINGER, 0, 0);
	}

	public static ViewAction swipe(PointF start, PointF end) {
		return swipe((int) start.x, (int) start.y, (int) end.x, (int) end.y);
	}

	public static ViewAction swipe(float startX, float startY, float endX, float endY) {
		return swipe((int) startX, (int) startY, (int) endX, (int) endY);
	}

	public static ViewAction swipe(int startX, int startY, int endX, int endY) {
		return swipe(PositionCoordinatesProvider.at(startX, startY), PositionCoordinatesProvider.at(endX, endY));
	}

	public static ViewAction swipe(CoordinatesProvider startCoordinatesProvider, CoordinatesProvider endCoordinatesProvider) {
		return new GeneralSwipeAction(Swipe.FAST, startCoordinatesProvider, endCoordinatesProvider, Press.FINGER);
	}

	public static ViewAction swipeAccurate(CoordinatesProvider startCoordinatesProvider, CoordinatesProvider endCoordinatesProvider) {
		return new GeneralSwipeAction(ACCURATE, startCoordinatesProvider, endCoordinatesProvider, Press.FINGER);
	}

	public static ViewAction selectViewPagerPage(final int pos) {
		return new ViewAction() {
			@Override
			public Matcher<View> getConstraints() {
				return isAssignableFrom(ViewPager.class);
			}

			@Override
			public String getDescription() {
				return "select page in ViewPager";
			}

			@Override
			public void perform(UiController uiController, View view) {
				((ViewPager) view).setCurrentItem(pos);
			}
		};
	}

	public enum Direction {
		ABOVE,
		BELOW,
		LEFT,
		RIGHT
	}

	private static class UnconstrainedScrollToAction implements ViewAction {
		private ViewAction action = new ScrollToAction();

		@Override
		public Matcher<View> getConstraints() {
			return withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE);
		}

		@Override
		public String getDescription() {
			return action.getDescription();
		}

		@Override
		public void perform(UiController uiController, View view) {
			action.perform(uiController, view);
		}
	}

	public static class DefinedLongTap implements Tapper {

		private int longPressTimeout;

		DefinedLongTap(int longPressTimeout) {
			this.longPressTimeout = longPressTimeout;
		}

		public static Tapper withPressTimeout(final int longPressTimeout) {
			return new DefinedLongTap(longPressTimeout);
		}

		@Override
		public Status sendTap(
				UiController uiController, float[] coordinates, float[] precision, int inputDevice,
				int buttonState) {
			MotionEvent downEvent = MotionEvents.sendDown(uiController, coordinates, precision,
					inputDevice, buttonState).down;
			try {
				// Duration before a press turns into a long press.
				// Factor 1.5 is needed, otherwise a long press is not safely detected.
				// See android.test.TouchUtils longClickView
				long longPressTimeout = (long) (this.longPressTimeout * 1.5f);
				uiController.loopMainThreadForAtLeast(longPressTimeout);

				if (!MotionEvents.sendUp(uiController, downEvent)) {
					MotionEvents.sendCancel(uiController, downEvent);
					return Status.FAILURE;
				}
			} finally {
				downEvent.recycle();
			}
			return Status.SUCCESS;
		}

		/**
		 * @deprecated use other sendTap instead
		 */
		@Deprecated
		@Override
		public Status sendTap(UiController uiController, float[] coordinates, float[] precision) {
			return sendTap(uiController, coordinates, precision, InputDevice.SOURCE_UNKNOWN,
					MotionEvent.BUTTON_PRIMARY);
		}
	}
}
