package at.tugraz.ist.paintroid.test.integration;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.jayway.android.robotium.solo.Solo;

public class BaseIntegrationTestClass extends ActivityInstrumentationTestCase2<MainActivity> {

	protected Solo mSolo;
	protected TextView mToolBarButtonMain;
	protected TextView mToolBarButtonOne;
	protected TextView mToolBarButtonTwo;
	protected int mScreenWidth;
	protected int mScreenHeight;
	protected final int TIMEOUT = 2000;
	protected MainActivity mMainActivity;

	public BaseIntegrationTestClass() throws Exception {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mSolo = new Solo(getInstrumentation(), getActivity());
		mMainActivity = (MainActivity) mSolo.getCurrentActivity();
		Utils.setLocale(mSolo, Locale.ENGLISH);
		mToolBarButtonMain = (TextView) getActivity().findViewById(R.id.btn_Tool);
		mToolBarButtonOne = (TextView) getActivity().findViewById(R.id.btn_Parameter1);
		mToolBarButtonTwo = (TextView) getActivity().findViewById(R.id.btn_Parameter2);
		mScreenWidth = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getWidth();
		mScreenHeight = mSolo.getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		try {
			mSolo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();

		super.tearDown();
	}

}
