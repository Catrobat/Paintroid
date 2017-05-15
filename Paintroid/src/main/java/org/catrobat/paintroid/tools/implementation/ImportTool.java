package org.catrobat.paintroid.tools.implementation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import org.catrobat.paintroid.tools.ToolType;

public class ImportTool extends StampTool {

	public ImportTool(Activity activity, ToolType toolType) {
		super(activity, toolType);
		mReadyForPaste = true;
		setOnLongClickAllowed(false);
		createOverlayButton();
	}

}
