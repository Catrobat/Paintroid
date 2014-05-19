package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;

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
}
