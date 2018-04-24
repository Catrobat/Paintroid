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

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.TapTargetBase;
import org.catrobat.paintroid.intro.TapTargetBottomBar;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.intro.helper.WelcomeActivityHelper;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.HashMap;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getDescendantView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.shouldStartSequence;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitMillis;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isNotVisible;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

public final class IntroUtils {
	private static final int ANIMATION_DELAY = 750;
	private static final String TT_CLASS_NAME = "com.getkeepsafe.taptargetview.TapTargetView";

	private IntroUtils() {
	}

	public static int numberOfVisibleChildren(LinearLayout layout) {
		int count = 0;
		for (int i = 0; i < layout.getChildCount(); i++) {
			if (layout.getChildAt(i).getVisibility() == View.VISIBLE) {
				count++;
			}
		}
		return count;
	}

	public static void introClickToolAndCheckView(ToolType toolType, IntroSlide introSlide) {
		ViewInteraction tapTargetViewInteraction;
		ViewInteraction buttonViewInteraction;
		ViewInteraction fadeViewInteraction;

		buttonViewInteraction = onView(allOf(withId(toolType.getToolButtonID()),
				isDescendantOfA(ViewMatchers.withId(introSlide.getToolBarResourceId()))));

		waitMillis(ANIMATION_DELAY);

		fadeViewInteraction = onView(ViewMatchers.withId(introSlide.getFadeViewResourceId()))
				.check(matches(isDisplayed()));

		if (introSlide == IntroSlide.Tools) {
			buttonViewInteraction.perform(scrollTo());

			onView(withText(R.string.intro_tool_more_information))
					.check(matches(isDisplayed()));
		}

		buttonViewInteraction
				.check(matches(isClickable()))
				.perform(click());

		if (introSlide == IntroSlide.Tools) {
			onView(withText(R.string.intro_tool_more_information))
					.check(matches(not(isDisplayed())));
		}

		onView(withTapTargetTitle(toolType.getNameResource()))
				.check(matches(isDisplayed()));
		onView(withTapTargetDescription(toolType.getHelpTextResource()))
				.check(matches(isDisplayed()));

		tapTargetViewInteraction = onView(allOf(withClassName(Matchers.is(TT_CLASS_NAME))));
		tapTargetViewInteraction.check(matches(isDisplayed()));
		fadeViewInteraction.check(matches(not(isDisplayed())));
		tapTargetViewInteraction.perform(click()).check(isNotVisible());
		fadeViewInteraction.check(matches(isDisplayed()));
	}

	private static Matcher<View> withTapTargetTitle(final int resourceId) {
		return new WithTapTargetTextMatcher(resourceId, TapTargetTextType.TITLE);
	}

	private static Matcher<View> withTapTargetDescription(final int resourceId) {
		return new WithTapTargetTextMatcher(resourceId, TapTargetTextType.DESCRIPTION);
	}

	public static int getExpectedRadiusForTapTarget() {
		final Context context = InstrumentationRegistry.getTargetContext();
		final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int radiusOffset = TapTargetBase.RADIUS_OFFSET;
		float dimension = context.getResources().getDimension(R.dimen.top_bar_height);
		return WelcomeActivityHelper.calculateTapTargetRadius(dimension, metrics, radiusOffset);
	}

	public static LinearLayout getBottomBarFromToolSlide(Activity activity) {
		return (LinearLayout) getDescendantView(R.id.intro_tools_bottom_bar, R.id.tools_layout, activity);
	}

	public static LinearLayout getTopBarFromPossibilitiesSlide(Activity activity) {
		return (LinearLayout) getDescendantView(R.id.intro_possibilites_topbar, R.id.top_bar_buttons, activity);
	}

	public static TapTargetBottomBar getTapTargetBottomBar(Activity activity) {
		LinearLayout targetItemView = getBottomBarFromToolSlide(activity);
		final View fadeView = activity.findViewById(R.id.intro_tools_textview);
		return new TapTargetBottomBar(targetItemView, fadeView, (WelcomeActivity) activity, R.id.intro_tools_bottom_bar);
	}

	public static TapTargetTopBar getTapTargetTopBar(Activity activity) {
		final View fadeView = activity.findViewById(R.id.intro_possibilities_textview);
		LinearLayout targetItemView = getTopBarFromPossibilitiesSlide(activity);
		TapTargetTopBar tapTargetTopBar = new TapTargetTopBar(targetItemView, fadeView, (WelcomeActivity) activity, R.id.intro_possibilities_bottom_bar);
		shouldStartSequence(false);
		return tapTargetTopBar;
	}

	public static HashMap<ToolType, TapTarget> getMapFromTapTarget(TapTargetBase tapTarget) {
		return tapTarget.tapTargetMap;
	}

	public static int getPageIndexFromLayout(final int[] layouts, final int layoutResource) throws IndexOutOfBoundsException {
		for (int i = 0; i < layouts.length; i++) {
			if (layouts[i] == layoutResource) {
				return i;
			}
		}

		throw new IndexOutOfBoundsException("No Index Found");
	}

	private enum TapTargetTextType {
		TITLE,
		DESCRIPTION
	}

	public enum IntroSlide {
		Tools(R.id.intro_tools_textview, R.id.intro_tools_bottom_bar),
		Possibilities(R.id.intro_possibilities_textview, R.id.intro_possibilites_topbar);

		int fadeViewResourceId;
		int toolBarResourceId;

		IntroSlide(int fadeViewResourceId, int toolBarResourceId) {
			this.fadeViewResourceId = fadeViewResourceId;
			this.toolBarResourceId = toolBarResourceId;
		}

		public int getFadeViewResourceId() {
			return fadeViewResourceId;
		}

		public int getToolBarResourceId() {
			return toolBarResourceId;
		}
	}

	static class WithTapTargetTextMatcher extends BoundedMatcher<View, TapTargetView> {

		private final int resourceId;
		private final TapTargetTextType type;
		private String text;

		WithTapTargetTextMatcher(int resourceId, TapTargetTextType type) {
			super(TapTargetView.class);
			this.resourceId = resourceId;
			this.type = type;
		}

		@Override
		protected boolean matchesSafely(TapTargetView item) {
			if (text == null) {
				text = item.getResources().getString(resourceId);
			}
			CharSequence actualText = null;
			try {
				switch (type) {
					case TITLE:
						actualText = (CharSequence) PrivateAccess.getMemberValue(TapTargetView.class, item, "title");
						break;
					case DESCRIPTION:
						actualText = (CharSequence) PrivateAccess.getMemberValue(TapTargetView.class, item, "description");
						break;
				}
				return actualText != null && text.equals(actualText.toString());
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public void describeTo(Description description) {
			if (text == null) {
				description.appendText("with string from resource id: ").appendValue(resourceId);
			} else {
				description.appendText("with string value:").appendText(text);
			}
		}
	}
}
