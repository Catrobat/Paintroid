package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.Test;

public class ImportPngToolTest extends BaseIntegrationTestClass {

	private static String FAILING_FILE_NAME = "thisisnofile";

	public ImportPngToolTest() throws Exception {
		super();
	}

	public void testWrongFileOnStamp() {

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// getActivity().importPngToFloatingBox(FAILING_FILE_NAME);
			}
		});

		assertTrue("Error Dialog after trying to load unusable file did not appear",
				mSolo.waitForText(mSolo.getString(R.string.dialog_loading_image_failed_text), 1, TIMEOUT, true));
	}

	@Test
	public void testIconsInitial() {
		mSolo.sleep(1000);
		selectTool(ToolType.STAMP);
		StampTool stampTool = (StampTool) PaintroidApplication.currentTool;
		assertEquals("Wrong icon for parameter button 1", R.drawable.icon_menu_stamp_paste,
				stampTool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1));
	}
}
