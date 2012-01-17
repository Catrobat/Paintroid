package at.tugraz.ist.paintroid.test.junit.ui;

import java.util.Observable;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.test.utils.TestObserver;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.ui.Toolbar;

public class ToolbarTests extends ActivityInstrumentationTestCase2<MainActivity> {

	protected MainActivity activity;
	protected Toolbar toolbar;

	public ToolbarTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		toolbar = (Toolbar) PrivateAccess.getMemberValue(MainActivity.class, activity, "toolbar");
	}

	public void testShouldChangeTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Tool newTool = new DrawTool(null);

		toolbar.setTool(newTool);

		Tool toolbarTool = toolbar.getCurrentTool();
		assertSame(newTool, toolbarTool);
	}

	public void testShouldNotifyObserversOnToolChange() {
		Tool tool = new DrawTool(null);
		TestObserver observer = new TestObserver();
		((Observable) toolbar).addObserver(observer);

		toolbar.setTool(tool);

		assertEquals(1, observer.getCallCount("update"));
		assertSame(toolbar, observer.getCall("update", 0).get(0));
	}
}
