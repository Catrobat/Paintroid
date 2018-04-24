package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Bitmap;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;

public class ImportTool extends StampTool {

	public ImportTool(Context context, ToolType toolType) {
		super(context, toolType);
		readyForPaste = true;
		longClickAllowed = false;
		createOverlayBitmap();
	}

	@Override
	public void setBitmapFromFile(Bitmap bitmap) {
		super.setBitmapFromFile(bitmap);

		final DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
		final float maximumBorderRatioWidth = MAXIMUM_BORDER_RATIO * drawingSurface.getBitmapWidth();
		final float maximumBorderRatioHeight = MAXIMUM_BORDER_RATIO * drawingSurface.getBitmapHeight();

		final float minimumSize = DEFAULT_BOX_RESIZE_MARGIN;

		boxWidth = Math.max(minimumSize, Math.min(maximumBorderRatioWidth, bitmap.getWidth()));
		boxHeight = Math.max(minimumSize, Math.min(maximumBorderRatioHeight, bitmap.getHeight()));
	}
}
