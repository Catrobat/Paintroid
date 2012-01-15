package at.tugraz.ist.paintroid.test.junit.ui;

import android.test.AndroidTestCase;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.implementation.ToolbarImplementation;

public class ToolbarTests extends AndroidTestCase {

	protected Toolbar toolbar;

	@Override
	public void setUp() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		MainActivity activity = new MainActivity();
		toolbar = new ToolbarImplementation(activity);
	}

	public void testShouldNotifyObserversOnToolChange() {
		toolbar.setTool(null);
	}
}
