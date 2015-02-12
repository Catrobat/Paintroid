package org.catrobat.paintroid.tools.implementation;

import android.app.Activity;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;

public class ImportTool extends StampTool {

	public ImportTool(Activity activity, ToolType toolType) {
		super(activity, toolType);
		mStampActive = true;
		mAttributeButton2.setEnabled(false);
	}

	@Override
	public int getAttributeButtonResource(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_BOTTOM_1:
			return R.drawable.icon_menu_stamp_paste;
		case BUTTON_ID_PARAMETER_BOTTOM_2:
			return NO_BUTTON_RESOURCE;
		default:
			return super.getAttributeButtonResource(buttonNumber);
		}
	}
}
