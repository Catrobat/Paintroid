import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.widget.GridView;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.integration.BaseIntegrationTestClass;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.implementation.BaseToolWithShape;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class StampToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String STAMP_TOOL_MEMBER_WIDTH = "mWidth";
	private static final String STAMP_TOOL_MEMBER_HEIGHT = "mHeight";
	private static final String STAMP_TOOL_MEMBER_POSITION = "mToolPosition";
	private static final int STATUS_BAR_HEIGHT_LOW = 24;
	private static final int STATUS_BAR_HEIGHT_MEDIUM = 32;
	private static final int STATUS_BAR_HEIGHT_HEIGH = 48;

	public StampToolIntegrationTest() throws Exception {
		super();
	}

	public void testResizeStampToolBox() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		// select stamp
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

		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX - 50;
		int statusbarHeight = getStatusbarHeigt();
		float dragFromY = rectPosition.y - rectHeight / 2 + statusbarHeight;
		float dragToY = dragFromY - 50;
		int stepCount = 10;
		mSolo.drag(dragFromX, dragToX, dragFromY, dragToY, stepCount);
		float newWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		assertTrue("new height should be bigger", newHeight > rectHeight);
		assertTrue("new width should be bigger", newWidth > rectHeight);
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
