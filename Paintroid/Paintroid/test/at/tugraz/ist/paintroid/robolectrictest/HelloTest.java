package at.tugraz.ist.paintroid.robolectrictest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;

import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowActivity;

@RunWith(RobolectricTestRunner.class)
public class HelloTest {

	@Test
	public void appNameIsPaintroid() throws Exception {
		ShadowActivity shadowMainActivity = Robolectric.shadowOf(new MainActivity());
		String hello = shadowMainActivity.getResources().getString(R.string.app_name);
		assertThat(hello, equalTo("Paintroid"));
	}
}
