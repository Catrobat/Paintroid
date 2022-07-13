package org.catrobat.paintroid.test.espresso.util.wrappers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.MainActivityHelper.getMainActivityFromView;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;

public class ZoomWindowInteraction extends CustomViewInteraction {

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
                        activity.findViewById(R.id.pocketpaint_zoom_window_inner).getLayoutParams();

                int rulesFromLayout = layoutParams.getRule(verb);

                Log.d("TAG", String.valueOf(rulesFromLayout));
                for(int i : layoutParams.getRules())
                    Log.d("TAG", String.valueOf(i));

                return rulesFromLayout == -1;
            }

            @Override
            public void describeTo(Description description) {
                String alignedAccordingTo =
                        (verb == RelativeLayout.ALIGN_PARENT_LEFT) ? "left" : "right";
                description.appendText("The zoom window is aligned to the " + alignedAccordingTo);
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
                        activity.findViewById(R.id.pocketpaint_zoom_window_inner).getLayoutParams();

                int[] rulesFromLayout = layoutParams.getRules();

                return rulesFromLayout[index] == -1;
            }

            @Override
            public void describeTo(Description description) {
                String alignedAccordingTo = index == 11 ? "right" : "left";
                description.appendText("The zoom window is aligned to " + alignedAccordingTo);
            }
        }));
        return this;
    }
}
