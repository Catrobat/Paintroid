package org.catrobat.paintroid.tools.implementation;

import android.graphics.Bitmap;

import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;

public class ImportTool extends StampTool {

	public ImportTool(ContextCallback contextCallback, ToolOptionsControllerContract toolOptionsController, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager, CommandFactory commandFactory) {
		super(contextCallback, toolOptionsController, toolPaint, workspace, commandManager, commandFactory);
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
