package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Bitmap;

import org.catrobat.paintroid.ContextActivityWrapper;
import org.catrobat.paintroid.CurrentToolWrapper;
import org.catrobat.paintroid.DrawingSurfaceWrapper;
import org.catrobat.paintroid.LayerModelWrapper;
import org.catrobat.paintroid.PerspectiveWrapper;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ToolType;

public class ImportTool extends StampTool {

	public ImportTool(ContextActivityWrapper contextActivityWrapper, Context context, ToolType toolType, DrawingSurfaceWrapper drawingSurfaceWrapper,
					CurrentToolWrapper currentToolWrapper, PerspectiveWrapper perspectiveWrapper,
					LayerModelWrapper layerModelWrapper, CommandManager commandManager) {
		super(contextActivityWrapper, context, toolType, drawingSurfaceWrapper, currentToolWrapper, perspectiveWrapper, layerModelWrapper, commandManager);
		readyForPaste = true;
		longClickAllowed = false;
		createOverlayBitmap();
	}

	@Override
	public void setBitmapFromFile(Bitmap bitmap) {
		super.setBitmapFromFile(bitmap);

		final float maximumBorderRatioWidth = MAXIMUM_BORDER_RATIO * drawingSurfaceWrapper.getBitmapWidth();
		final float maximumBorderRatioHeight = MAXIMUM_BORDER_RATIO * drawingSurfaceWrapper.getBitmapHeight();

		final float minimumSize = DEFAULT_BOX_RESIZE_MARGIN;

		boxWidth = Math.max(minimumSize, Math.min(maximumBorderRatioWidth, bitmap.getWidth()));
		boxHeight = Math.max(minimumSize, Math.min(maximumBorderRatioHeight, bitmap.getHeight()));
	}
}
