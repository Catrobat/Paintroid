package org.catrobat.paintroid.test.espresso.intro;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.ViewPager;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.IntroPageViewAdapter;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class IntroIntegrationTest {

	@Rule
	public ActivityTestRule<WelcomeActivity> activityTestRule = new ActivityTestRule<>(WelcomeActivity.class);

	@Test
	public void testIntroWelcomePage() {
		onView(withText(R.string.welcome_to_pocket_paint)).check(matches(isDisplayed()));
		onView(withText(R.string.intro_welcome_text)).check(matches(isDisplayed()));
		onView(withText(R.string.next)).check(matches(isDisplayed()));
		onView(withText(R.string.skip)).check(matches(isDisplayed()));
	}

	@Test
	public void testOnSkipPressedActivityFinished() {
		onView(withId(R.id.pocketpaint_btn_skip)).perform(click());
		assertTrue(activityTestRule.getActivity().isFinishing());
	}

	@Test
	public void testOnLetsGoPressedActivityFinished() {
		ViewPager viewPager = activityTestRule.getActivity().viewPager;
		IntroPageViewAdapter adapter = (IntroPageViewAdapter) viewPager.getAdapter();

		for (int i = 0; i < adapter.layouts.length - 1; i++) {
			onView(withId(R.id.pocketpaint_btn_next)).perform(click());
		}

		onView(withId(R.id.pocketpaint_btn_next)).perform(click());
		assertTrue(activityTestRule.getActivity().isFinishing());
	}

	@Test
	public void testIntroToolsPageShowDescriptionOnPress() {
		ViewPager viewPager = activityTestRule.getActivity().viewPager;
		IntroPageViewAdapter adapter = (IntroPageViewAdapter) viewPager.getAdapter();

		for (int layout: adapter.layouts) {
			if (layout == R.layout.pocketpaint_slide_intro_tools_selection) {
				break;
			}
			onView(withId(R.id.pocketpaint_btn_next)).perform(click());
		}

		onView(withId(R.id.pocketpaint_textview_intro_tools_header))
				.check(matches(isDisplayed()));

		for (ToolType toolType: ToolType.values()) {
			if (!toolType.equals(ToolType.UNDO) && !toolType.equals(ToolType.REDO) && !toolType.equals(ToolType.LAYER) && !toolType.equals(ToolType.COLORCHOOSER)) {
				onView(withId(toolType.getToolButtonID())).perform(click());
				onView(withText(toolType.getHelpTextResource())).check(matches(isDisplayed()));
			}
		}
	}

	@Test
	public void testIntroViewPagerSwipeChangePage() {
		onView(withId(R.id.pocketpaint_intro_welcome_head))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_view_pager)).perform(swipeLeft());

		onView(withId(R.id.pocketpaint_intro_possibilities_head))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_view_pager)).perform(swipeLeft());

		onView(withId(R.id.pocketpaint_textview_intro_tools_header))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_view_pager)).perform(swipeLeft());

		onView(withId(R.id.pocketpaint_intro_landscape_head))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_view_pager)).perform(swipeLeft());

		onView(withId(R.id.pocketpaint_intro_started_head))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testIntroViewPagerNextButtonChangePage() {
		onView(withId(R.id.pocketpaint_intro_welcome_head))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_btn_next)).perform(click());

		onView(withId(R.id.pocketpaint_intro_possibilities_head))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_btn_next)).perform(click());

		onView(withId(R.id.pocketpaint_textview_intro_tools_header))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_btn_next)).perform(click());

		onView(withId(R.id.pocketpaint_intro_landscape_head))
				.check(matches(isDisplayed()));

		onView(withId(R.id.pocketpaint_btn_next)).perform(click());

		onView(withId(R.id.pocketpaint_intro_started_head))
				.check(matches(isDisplayed()));
	}
}
