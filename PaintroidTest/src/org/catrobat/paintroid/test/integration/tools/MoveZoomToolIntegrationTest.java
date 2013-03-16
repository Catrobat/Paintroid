package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Test;

public class MoveZoomToolIntegrationTest extends BaseIntegrationTestClass {

	public MoveZoomToolIntegrationTest() throws Exception {
		super();
	}

	@Test
	public void testZoomOut() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mMenuBottomParameter1);
		mSolo.sleep(200);
		float scaleAfterZoom = PaintroidApplication.perspective.getScale();

		assertTrue("Zooming-out has not worked", scaleBeforeZoom > scaleAfterZoom);
	}

	@Test
	public void testZoomIn() {
		float scaleBeforeZoom = PaintroidApplication.perspective.getScale();
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(200);
		float scaleAfterZoom = PaintroidApplication.perspective.getScale();

		assertTrue("Zooming-in has not worked", scaleBeforeZoom < scaleAfterZoom);
	}
}
