package at.tugraz.ist.paintroid.test.junit.ui.button;

import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.junit.stubs.ToolStub;
import at.tugraz.ist.paintroid.test.junit.stubs.ToolbarStub;
import at.tugraz.ist.paintroid.ui.button.AttributeButton;

public class AttributeButtonTests extends ActivityInstrumentationTestCase2<MainActivity> {

	protected MainActivity activity;
	protected AttributeButton attributeButton1;
	protected AttributeButton attributeButton2;
	protected ToolbarStub toolbarStub;
	protected ToolStub toolStub;

	public AttributeButtonTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		activity = this.getActivity();
		attributeButton1 = (AttributeButton) activity.findViewById(R.id.btn_Parameter1);
		attributeButton2 = (AttributeButton) activity.findViewById(R.id.btn_Parameter2);
		toolbarStub = new ToolbarStub();
		toolStub = new ToolStub();
		toolbarStub.setReturnValue("getCurrentTool", toolStub);
	}

	public void testAttributeButton1SetToolbarShouldAddObservable() {
		attributeButton1.setToolbar(toolbarStub);

		assertEquals(1, toolStub.getCallCount("addObserver"));
		assertSame(attributeButton1, toolStub.getCall("addObserver", 0).get(0));
	}
}
