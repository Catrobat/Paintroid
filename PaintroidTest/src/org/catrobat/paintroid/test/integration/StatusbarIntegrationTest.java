package org.catrobat.paintroid.test.integration;

import java.util.ArrayList;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Statusbar;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StatusbarIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mStatusbar";
	private static final String PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME = "mToolNameToast";

	public StatusbarIntegrationTest() throws Exception {
		super();
	}

	public void testAllButtonsAreVisible() {
		ArrayList<Integer> expectedButtons = new ArrayList<Integer>();
		expectedButtons.add(R.id.btn_status_undo);
		expectedButtons.add(R.id.btn_status_redo);
		expectedButtons.add(R.id.btn_status_color);
		expectedButtons.add(R.id.btn_status_tool);

		ArrayList<ImageButton> imageButtons = mSolo.getCurrentImageButtons();
		for (ImageButton button : imageButtons) {
			expectedButtons.remove((Object) button.getId());
		}

		assertEquals("all buttons should be found", 0, expectedButtons.size());
	}

	public void testSwitchToMoveOnSwitchToolIconClicked() {
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be move tool now", ToolType.MOVE, PaintroidApplication.currentTool.getToolType());
	}

	public void testSwitchBackToPreviousToolOnSwitchToolIconClickedTwice() {
		selectTool(ToolType.CURSOR);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be move tool now", ToolType.MOVE, PaintroidApplication.currentTool.getToolType());
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be cursor tool now", ToolType.CURSOR, PaintroidApplication.currentTool.getToolType());

	}

	public void testPreviousToolIsNotMoveTool() {
		selectTool(ToolType.MOVE);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be brush tool now", ToolType.BRUSH, PaintroidApplication.currentTool.getToolType());
	}

	public void testPreviousToolIsNotZoomTool() {
		selectTool(ToolType.ZOOM);
		mSolo.clickOnView(mButtonTopTool);
		assertEquals("tool should be brush tool now", ToolType.BRUSH, PaintroidApplication.currentTool.getToolType());
	}

	public void testToastShowsRightToolName() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		Statusbar statusbar = (Statusbar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(),
				PRIVATE_ACCESS_STATUSBAR_NAME);
		mSolo.clickOnView(mButtonTopTool);
		Toast toolNameToast = (Toast) PrivateAccess.getMemberValue(Statusbar.class, statusbar,
				PRIVATE_ACCESS_TOOL_NAME_TOAST_NAME);
		String toolNameToastString = ((TextView) ((LinearLayout) toolNameToast.getView()).getChildAt(0)).getText()
				.toString();
		assertEquals("toast should display name of moveTool", mSolo.getString(ToolType.MOVE.getNameResource()),
				toolNameToastString);
	}
}
