package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.MoveZoomTool;
import org.catrobat.paintroid.ui.implementation.PerspectiveImplementation;
import org.catrobat.paintroid.ui.implementation.StatusbarImplementation.ToolButtonIDs;
import org.junit.Before;
import org.junit.Test;

import android.graphics.PointF;

public class MoveZoomTest extends BaseToolTest {

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new MoveZoomTool(getActivity(), ToolType.MOVE);
		super.setUp();
	}

	public void testMove() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		float screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

		int offset = 50;

		PointF fromPoint = new PointF(screenWidth / 2, screenHeight / 2);
		PointF toPoint = new PointF(fromPoint.x + offset, fromPoint.y + offset);

		float translationXBefore = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceTranslationX");
		float translationYBefore = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceTranslationY");

		mToolToTest.handleDown(fromPoint);
		mToolToTest.handleMove(toPoint);

		float translationXAfter = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceTranslationX");
		float translationYAfter = (Float) PrivateAccess.getMemberValue(PerspectiveImplementation.class,
				PaintroidApplication.CURRENT_PERSPECTIVE, "mSurfaceTranslationY");

		assertEquals("translation of X should be the offset", translationXAfter - offset, translationXBefore);
		assertEquals("translation of Y should be the offset", translationYAfter - offset, translationYBefore);
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Move tool icon should be displayed", R.drawable.icon_menu_move, resource);
	}
}
