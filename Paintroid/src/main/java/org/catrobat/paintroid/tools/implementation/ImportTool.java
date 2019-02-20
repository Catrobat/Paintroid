package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Bitmap;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;

public class ImportTool extends StampTool {

	public ImportTool(Context context, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(context, toolPaint, workspace, commandManager);
		readyForPaste = true;
		longClickAllowed = false;
	}

	@Override
	public void setBitmapFromFile(Bitmap bitmap) {
		super.setBitmapFromFile(bitmap);

		final float maximumBorderRatioWidth = MAXIMUM_BORDER_RATIO * workspace.getWidth();
		final float maximumBorderRatioHeight = MAXIMUM_BORDER_RATIO * workspace.getHeight();

		final float minimumSize = DEFAULT_BOX_RESIZE_MARGIN;

		boxWidth = Math.max(minimumSize, Math.min(maximumBorderRatioWidth, bitmap.getWidth()));
		boxHeight = Math.max(minimumSize, Math.min(maximumBorderRatioHeight, bitmap.getHeight()));
	}

	@Override
	public ToolType getToolType() {
		return ToolType.IMPORTPNG;
	}
}
