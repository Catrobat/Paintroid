package at.tugraz.ist.paintroid.robolectrictest;

import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class HelloTest {

    @Test
    public void appNameIsPaintroid() throws Exception {
        String hello = new MainActivity().getResources().getString(R.string.app_name);
        assertThat(hello, equalTo("Paintroid"));
    }
}
