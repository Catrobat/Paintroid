/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

public class ImportTool extends BaseToolWithRectangleShape {
	private static final String BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP";

	public ImportTool(ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		rotationEnabled = true;
	}

	@Override
	public void drawShape(Canvas canvas) {
		if (drawingBitmap != null) {
			super.drawShape(canvas);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putParcelable(BUNDLE_TOOL_DRAWING_BITMAP, drawingBitmap);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		Bitmap bitmap = bundle.getParcelable(BUNDLE_TOOL_DRAWING_BITMAP);
		if (bitmap != null) {
			drawingBitmap = bitmap;
		}
	}

	@Override
	public void onClickOnButton() {
		highlightBox();
		Command command = commandFactory.createStampCommand(drawingBitmap, toolPosition, boxWidth, boxHeight, boxRotation);
		commandManager.addCommand(command);
	}

	public void setBitmapFromSource(Bitmap bitmap) {
		super.setBitmap(bitmap);

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
