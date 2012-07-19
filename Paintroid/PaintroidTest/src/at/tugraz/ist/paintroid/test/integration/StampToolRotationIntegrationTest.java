package at.tugraz.ist.paintroid.test.integration;

import android.graphics.PointF;
import android.widget.GridView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.implementation.BaseToolWithShape;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class StampToolRotationIntegrationTest extends BaseIntegrationTestClass {

	private static final int X_OFFSET = 5;
	private static final int Y_OFFSET = 40;
	private static final int DRAG_STEPS = 30;

	private static final String BASE_TOOL_POSITION_FIELD = "mToolPosition";
	private static final String STAMP_TOOL_ROTATION_FIELD = "mBoxRotation";

	public StampToolRotationIntegrationTest() throws Exception {
		super();
	}

	public void testStampToolRotation() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		// select stamp
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;
		PointF position = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				BASE_TOOL_POSITION_FIELD);

		float rotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_ROTATION_FIELD);

		mSolo.clickOnScreen(position.x, position.y);
		// try rotate right
		mSolo.drag(X_OFFSET, mScreenWidth / 2, Y_OFFSET, Y_OFFSET + 30, DRAG_STEPS);
		float newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_ROTATION_FIELD);
		assertTrue("Rotation value should be bigger after rotating.", rotation < newRotation);

		// try rotate left
		rotation = newRotation;
		mSolo.drag(mScreenWidth / 2, X_OFFSET, Y_OFFSET, Y_OFFSET + 30, DRAG_STEPS);
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_ROTATION_FIELD);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// try rotate even more left (start from bottom of screen)
		rotation = newRotation;
		mSolo.drag(mScreenWidth - X_OFFSET, mScreenWidth - X_OFFSET * 2, mScreenHeight / 2, Y_OFFSET, DRAG_STEPS);
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_ROTATION_FIELD);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// and now a lot to the right
		rotation = newRotation;
		mSolo.drag(X_OFFSET, X_OFFSET * 2, mScreenHeight / 2, Y_OFFSET, DRAG_STEPS);
		mSolo.drag(mScreenWidth / 2, mScreenWidth - X_OFFSET, Y_OFFSET, Y_OFFSET * 2, DRAG_STEPS);
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_ROTATION_FIELD);
		assertTrue("Rotation value should be smaller after rotating.", rotation < newRotation);
	}
}
