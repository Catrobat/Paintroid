package org.catrobat.paintroid.test.junit.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.DrawingSurfaceStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.Before;

import android.graphics.Bitmap;

public class StampToolTest extends BaseToolTest {

	private static final float SQUARE_LENGTH = 100;
	private static final float MIN_ROTATION = -450;
	private static final float MAX_ROTATION = 450;
	private static final float ROTATION_STEPSIZE = 30.0f;
	private static final float LENGTH_TOLERANCE = 5;

	public StampToolTest() {
		super();
		PaintroidApplication.DRAWING_SURFACE = new DrawingSurfaceStub();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new StampTool(getActivity(), Tool.ToolType.STAMP);
		super.setUp();
	}

	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();

		assertEquals(ToolType.STAMP, toolType);
	}

	public void testBoundingboxAlgorithm() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		StampTool stampTool = (StampTool) mToolToTest;

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxWidth", (int) (SQUARE_LENGTH));
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxHeight", (int) (SQUARE_LENGTH));

		for (float i = MIN_ROTATION; i < MAX_ROTATION; i = i + ROTATION_STEPSIZE) {
			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, stampTool, "mBoxRotation", (int) (i));

			invokeCreateAndSetBitmap(stampTool, PaintroidApplication.DRAWING_SURFACE);

			Bitmap currentToolBitmap = (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
					stampTool, "mDrawingBitmap");

			float width = currentToolBitmap.getWidth();
			float height = currentToolBitmap.getHeight();

			boolean widthOk = (width - LENGTH_TOLERANCE < SQUARE_LENGTH) && (width + LENGTH_TOLERANCE > SQUARE_LENGTH);
			boolean heightOk = (height - LENGTH_TOLERANCE < SQUARE_LENGTH)
					&& (height + LENGTH_TOLERANCE > SQUARE_LENGTH);

			assertEquals("Width after rotation should stay the same(including toleranced due to rounding)", true,
					widthOk);
			assertEquals("Height after rotation should stay the same(including toleranced due to rounding)", true,
					heightOk);
		}

	}

	private void invokeCreateAndSetBitmap(Object object, Object parameter) throws NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Method method = object.getClass().getDeclaredMethod("createAndSetBitmap", DrawingSurface.class);
		method.setAccessible(true);

		Object[] parameters = new Object[1];
		parameters[0] = parameter;
		method.invoke(object, parameters);
	}
}
