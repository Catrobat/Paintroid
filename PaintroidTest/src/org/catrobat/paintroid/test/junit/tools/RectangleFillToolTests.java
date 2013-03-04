package org.catrobat.paintroid.test.junit.tools;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.RectangleFillTool;
import org.catrobat.paintroid.ui.Statusbar.ToolButtonIDs;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Color;
import android.graphics.Paint;

public class RectangleFillToolTests extends BaseToolTest {

	public RectangleFillToolTests() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new RectangleFillTool(getActivity(), ToolType.RECT);
		super.setUp();
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();
		assertEquals(ToolType.RECT, toolType);
	}

	@Test
	public void testColorChangeWorks() {
		Paint red = new Paint();
		red.setColor(Color.RED);
		mToolToTest.setDrawPaint(red);
		int color = mToolToTest.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_TOP);
		assertEquals("Red colour expected", Color.RED, color);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonOne() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);
		assertEquals("Transparent should be displayed", R.drawable.icon_menu_no_icon, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForBottomButtonTwo() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);
		assertEquals("Color picker should be displayed", R.drawable.icon_menu_color_palette, resource);
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Rectangle tool icon should be displayed", R.drawable.icon_menu_rectangle, resource);
	}
}
