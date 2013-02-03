package org.catrobat.paintroid.test.junit.tools;

import static org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPaintEquals;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.MagicCommand;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.MagicTool;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

public class MagicToolTest extends BaseToolTest {

	public MagicToolTest() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new MagicTool(getActivity(), Tool.ToolType.MAGIC);
		super.setUp();
	}

	@Test
	public void testHandleDownNoCommandCommited() {
		assertTrue(mToolToTest.handleDown(new PointF(0, 0)));
		assertEquals("Magic command should not have been commited", 0,
				mCommandManagerStub.getCallCount("commitCommand"));
	}

	@Test
	public void testHandleMoveNoCommandCommited() {
		assertTrue(mToolToTest.handleMove(new PointF(0, 1)));
		assertEquals("Magic command should not have been commited", 0,
				mCommandManagerStub.getCallCount("commitCommand"));
	}

	@Test
	public void testHandleUpMagicCommandCommited() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		Point handleUpPoint = new Point(0, 0);
		assertTrue(mToolToTest.handleUp(new PointF(handleUpPoint)));
		assertEquals("No MagicCommand committed", 1, mCommandManagerStub.getCallCount("commitCommand"));
		Command command = (Command) mCommandManagerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof MagicCommand);
		Point point = (Point) PrivateAccess.getMemberValue(MagicCommand.class, command, "mColorPixel");
		assertTrue("Wrong replace coordinate of commited command", handleUpPoint.equals(point.x, point.y));
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "mPaint");
		assertPaintEquals(mPaint, paint);
	}

	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();

		assertEquals(ToolType.MAGIC, toolType);
	}

	@Test
	public void testShouldReturnCorrectResourceForTopButtonFourIfColorIsTransparent() {
		Paint transparent = new Paint();
		transparent.setColor(Color.TRANSPARENT);
		mToolToTest.setDrawPaint(transparent);
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Transparent should be displayed", R.drawable.checkeredbg_repeat, resource);
	}

	@Test
	public void testShouldReturnCorrectColorForTopButtonFourIfColorIsTransparent() {
		Paint transparent = new Paint();
		transparent.setColor(Color.TRANSPARENT);
		mToolToTest.setDrawPaint(transparent);
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Transparent colour expected", Color.TRANSPARENT, color);
	}

	@Test
	public void testShouldReturnCorrectColorForTopButtonFourIfColorIsBlack() {
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Red colour expected", Color.BLACK, color);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonOne() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
		assertEquals("Transparend should be displayed", R.drawable.icon_menu_no_icon, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonTwo() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
		assertEquals("Transparend should be displayed", R.drawable.icon_menu_color_palette, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Magic tool icon should be displayed", R.drawable.icon_menu_magic, resource);
	}
}
