package org.catrobat.paintroid.test.junit.intro;

import android.content.SharedPreferences;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;

/**
 * Created by Clemens on 08.11.2016.
 */
/* TODO: new and correct tests needed for intro
public class IntroTest extends BaseIntroTest {

	public IntroTest() {
		super();
	}

	public void testFirstLaunch() throws InterruptedException {

		assertTrue("Intro slide should be shown",  msolo.searchText(getActivity().getString(R.string.intro_welcome_header)));
		assertTrue("Welcome Text should be shown",  msolo.searchText(getActivity().getString(R.string.intro_welcome_text)));
	}

	public void testNotFirstLaunch() {

		setFirstLaunch(false);
		msolo.waitForActivity(MainActivity.class);
		msolo.assertCurrentActivity("Drawing surface should be shown", MainActivity.class);
	}

}
*/