package org.catrobat.paintroid.test.junit.intro;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import org.catrobat.paintroid.Session;
import org.catrobat.paintroid.WelcomeActivity;

import static org.catrobat.paintroid.Session.IS_FIRST_TIME_LAUNCH;
import static org.catrobat.paintroid.Session.PREF_NAME;

/**
 * Created by Clemens on 08.11.2016.
 */
public class BaseIntroTest extends ActivityInstrumentationTestCase2<WelcomeActivity> {

	protected Solo msolo = null;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private Context _context;
	private int PRIVATE_MODE = 0;

	public BaseIntroTest() {
		super(WelcomeActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		msolo = new Solo(getInstrumentation());
		editor = getActivity().getApplicationContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE).edit();
		setFirstLaunch(true);
		getActivity();

	}

	protected void setFirstLaunch(boolean b) {
		editor.putBoolean(IS_FIRST_TIME_LAUNCH, !b);
		editor.commit();
	}
}
