package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.view.View;
import android.widget.RelativeLayout;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import static org.catrobat.paintroid.test.espresso.util.MainActivityHelper.getMainActivityFromView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public final class ZoomWindowInteraction extends CustomViewInteraction {
	private ZoomWindowInteraction() {
		super(onView(withId(R.id.pocketpaint_zoom_window)));
	}

	public static ZoomWindowInteraction onZoomWindow() {
		return new ZoomWindowInteraction();
	}

	public ZoomWindowInteraction checkAlignment(final int verb) {
		check(matches(new TypeSafeMatcher<View>() {
			@Override
			protected boolean matchesSafely(View view) {
				MainActivity activity = getMainActivityFromView(view);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
						activity.findViewById(R.id.pocketpaint_zoom_window).getLayoutParams();

				int rulesFromLayout = layoutParams.getRule(verb);

				return rulesFromLayout == -1;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("The window's alignment");
			}
		}));
		return this;
	}

	public ZoomWindowInteraction checkAlignmentBelowM(final int index) {
		check(matches(new TypeSafeMatcher<View>() {
			@Override
			protected boolean matchesSafely(View view) {
				MainActivity activity = getMainActivityFromView(view);
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
						activity.findViewById(R.id.pocketpaint_zoom_window).getLayoutParams();

				int[] rulesFromLayout = layoutParams.getRules();

				return rulesFromLayout[index] == -1;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("The window's alignment");
			}
		}));
		return this;
	}
}
