package at.tugraz.ist.paintroid.test.integration;

import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.widget.GridView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.implementation.BaseToolWithShape;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class StampToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String STAMP_TOOL_MEMBER_WIDTH = "mWidth";
	private static final String STAMP_TOOL_MEMBER_HEIGHT = "mHeight";
	private static final String STAMP_TOOL_MEMBER_POSITION = "mToolPosition";
	private static final String STAMP_TOOL_MEMBER_BOX_RESIZE_MARGIN = "DEFAULT_BOX_RESIZE_MARGIN";
	private static final String STAMP_TOOL_MEMBER_ROTATION = "mBoxRotation";
	private static final int STATUS_BAR_HEIGHT_LOW = 24;
	private static final int STATUS_BAR_HEIGHT_MEDIUM = 32;
	private static final int STATUS_BAR_HEIGHT_HEIGH = 48;
	private static final int RESIZE_MOVE_DISTANCE = 50;
	private static final int DRAG_STEPS = 10;
	private static final int X_OFFSET = 5;
	private static final int Y_OFFSET = 40;

	public StampToolIntegrationTest() throws Exception {
		super();
	}

	public void testResizeStampToolBox() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;
		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		int statusbarHeight = getStatusbarHeigt();

		// resize bigger top left
		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y - rectHeight / 2 + statusbarHeight;
		float dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller top left
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger top center
		dragFromX = rectPosition.x;
		dragToX = dragFromX;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, false, true, true);

		// resize smaller top center;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, false, true, false);

		// resize bigger top right
		dragFromX = rectPosition.x + rectWidth / 2;
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller top right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger center right
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y + statusbarHeight;
		dragToY = dragFromY;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, false, true);

		// resize smaller center right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, false, false);

		// resize bigger bottom right
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y + rectHeight / 2 + statusbarHeight;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller bottom right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger bottom center
		dragFromX = rectPosition.x;
		dragToX = dragFromX;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, false, true, true);

		// resize smaller bottom center
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, false, true, false);

		// resize bigger bottom left
		dragFromX = rectPosition.x - rectWidth / 2;
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller bottom left
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger center left
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y + statusbarHeight;
		dragToY = dragFromY;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, false, true);

		// resize smaller center left
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		testResize(dragFromX, dragToX, dragFromY, dragToY, true, false, false);

	}

	public void testResizeRectangleMinimumSizeBiggerThanMargin() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;
		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		int statusbarHeight = getStatusbarHeigt();

		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX + rectWidth + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y - rectHeight / 2 + statusbarHeight;
		float dragToY = dragFromY + rectHeight + RESIZE_MOVE_DISTANCE;

		mSolo.drag(dragFromX, dragToX, dragFromY, dragToY, DRAG_STEPS);

		float newWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		float boxResizeMargin = (Integer) PrivateAccess.getMemberValue(StampTool.class, stampTool,
				STAMP_TOOL_MEMBER_BOX_RESIZE_MARGIN);

		assertTrue("new width should be bigger or equal to the resize margin", newWidth >= boxResizeMargin);
		assertTrue("new height should be bigger or equal to the resize margin", newHeight >= boxResizeMargin);

	}

	public void testMoveStampRectangle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;
		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		int statusbarHeight = getStatusbarHeigt();
		float dragFromX = rectPosition.x;
		float dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y + statusbarHeight;
		float dragToY = dragFromY + RESIZE_MOVE_DISTANCE;

		mSolo.drag(dragFromX, dragToX, dragFromY, dragToY, DRAG_STEPS);

		float newWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		assertTrue("width should be the same", rectWidth == newWidth);
		assertTrue("height should be the same", rectHeight == newHeight);
		assertTrue("position should have moved", (newPosition.x == dragToX)
				&& (newPosition.y == dragToY - statusbarHeight));

	}

	public void testRectangleSizeChangeWhenZoomed() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		float scale = 1f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale);
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampToolZoom1 = (StampTool) PaintroidApplication.CURRENT_TOOL;
		float rectWidthZoom1 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom1,
				STAMP_TOOL_MEMBER_WIDTH);
		float rectHeightZoom1 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom1,
				STAMP_TOOL_MEMBER_HEIGHT);

		scale = 2f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale);
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampToolZoom2 = (StampTool) PaintroidApplication.CURRENT_TOOL;
		float rectWidthZoom2 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom2,
				STAMP_TOOL_MEMBER_WIDTH);
		float rectHeightZoom2 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom2,
				STAMP_TOOL_MEMBER_HEIGHT);

		assertTrue("rectangle should be smaller with scale 2", (rectWidthZoom1 > rectWidthZoom2)
				&& (rectHeightZoom1 > rectHeightZoom2));

		scale = 0.5f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale);
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampToolZoom05 = (StampTool) PaintroidApplication.CURRENT_TOOL;
		float rectWidthZoom05 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom05,
				STAMP_TOOL_MEMBER_WIDTH);
		float rectHeightZoom05 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom05,
				STAMP_TOOL_MEMBER_HEIGHT);

		assertTrue("rectangle should be bigger with scale 0.5", (rectWidthZoom1 < rectWidthZoom05)
				&& (rectHeightZoom1 < rectHeightZoom05));

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
				STAMP_TOOL_MEMBER_POSITION);

		float rotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);

		mSolo.clickOnScreen(position.x, position.y);
		// try rotate right
		mSolo.drag(X_OFFSET, mScreenWidth / 2, Y_OFFSET, Y_OFFSET + 30, DRAG_STEPS);
		float newRotation = (Float) PrivateAccess
				.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be bigger after rotating.", rotation < newRotation);

		// try rotate left
		rotation = newRotation;
		mSolo.drag(mScreenWidth / 2, X_OFFSET, Y_OFFSET, Y_OFFSET + 30, DRAG_STEPS);
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// try rotate even more left (start from bottom of screen)
		rotation = newRotation;
		mSolo.drag(mScreenWidth - X_OFFSET, mScreenWidth - X_OFFSET * 2, mScreenHeight / 2, Y_OFFSET, DRAG_STEPS);
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// and now a lot to the right
		rotation = newRotation;
		mSolo.drag(X_OFFSET, X_OFFSET * 2, mScreenHeight / 2, Y_OFFSET, DRAG_STEPS);
		mSolo.drag(mScreenWidth / 2, mScreenWidth - X_OFFSET, Y_OFFSET, Y_OFFSET * 2, DRAG_STEPS);
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation < newRotation);
	}

	private void testResize(float dragFromX, float dragToX, float dragFromY, float dragToY, boolean resizeWidth,
			boolean resizeHeight, boolean resizeBigger) throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mSolo.clickOnView(mToolBarButtonMain);
		assertTrue("Wainting for DialogTools", mSolo.waitForView(GridView.class, 1, TIMEOUT));
		mSolo.clickOnText(getActivity().getString(R.string.button_floating_box));
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		StampTool stampTool = (StampTool) PaintroidApplication.CURRENT_TOOL;
		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		mSolo.drag(dragFromX, dragToX, dragFromY, dragToY, DRAG_STEPS);
		float newWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		if (resizeBigger) {
			if (resizeWidth) {
				assertTrue("new width should be bigger", newWidth > rectWidth);
			} else {
				assertTrue("height should not have changed", newWidth == rectWidth);
			}
			if (resizeHeight) {
				assertTrue("new width should be bigger", newHeight > rectHeight);
			} else {
				assertTrue("height should not have chaned", newHeight == rectHeight);
			}
		} else {
			if (resizeWidth) {
				assertTrue("new height should be smaller", newWidth < rectWidth);
			} else {
				assertTrue("height should not have changed", newWidth == rectWidth);
			}
			if (resizeHeight) {
				assertTrue("new width should be smaller", newHeight < rectHeight);
			} else {
				assertTrue("height should not have chaned", newHeight == rectHeight);
			}
		}

		assertTrue("position should be the same", (newPosition.x == rectPosition.x)
				&& (newPosition.y == rectPosition.y));

	}

	private int getStatusbarHeigt() {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				return STATUS_BAR_HEIGHT_LOW;
			case DisplayMetrics.DENSITY_MEDIUM:
				return STATUS_BAR_HEIGHT_MEDIUM;
			case DisplayMetrics.DENSITY_HIGH:
				return STATUS_BAR_HEIGHT_HEIGH;
			default:
				return 0;
		}
	}
}
