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
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.StampToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

public class StampTool extends BaseToolWithRectangleShape {

	private static final String BUNDLE_TOOL_READY_FOR_PASTE = "BUNDLE_TOOL_READY_FOR_PASTE";
	private static final String BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP";
	private static final boolean ROTATION_ENABLED = true;
	private final StampToolOptionsView stampToolOptionsView;
	protected boolean readyForPaste;

	public StampTool(StampToolOptionsView stampToolOptionsView, ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
					ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		readyForPaste = false;
		this.rotationEnabled = ROTATION_ENABLED;
		this.stampToolOptionsView = stampToolOptionsView;

		setBitmap(Bitmap.createBitmap((int) boxWidth, (int) boxHeight, Config.ARGB_8888));

		if (stampToolOptionsView != null) {
			StampToolOptionsView.Callback callback =
					new StampToolOptionsView.Callback() {
						@Override
						public void copyClicked() {
							highlightBox();
							copyBoxContent();
							StampTool.this.stampToolOptionsView.enablePaste(true);
						}

						@Override
						public void cutClicked() {
							highlightBox();
							copyBoxContent();
							cutBoxContent();
							StampTool.this.stampToolOptionsView.enablePaste(true);
						}

						@Override
						public void pasteClicked() {
							highlightBox();
							pasteBoxContent();
						}
					};

			stampToolOptionsView.setCallback(callback);
		}

		toolOptionsViewController.showDelayed();
	}

	public void copyBoxContent() {
		if (isDrawingBitmapReusable()) {
			drawingBitmap.eraseColor(Color.TRANSPARENT);
		} else {
			drawingBitmap = Bitmap.createBitmap((int) boxWidth, (int) boxHeight, Config.ARGB_8888);
		}

		Bitmap layerBitmap = workspace.getBitmapOfCurrentLayer();

		Canvas canvas = new Canvas(drawingBitmap);
		canvas.translate(-toolPosition.x + boxWidth / 2, -toolPosition.y + boxHeight / 2);
		canvas.rotate(-boxRotation, toolPosition.x, toolPosition.y);
		canvas.drawBitmap(layerBitmap, 0, 0, null);

		readyForPaste = true;
	}

	private void pasteBoxContent() {
		Command command = commandFactory.createStampCommand(drawingBitmap, toolPosition, boxWidth, boxHeight, boxRotation);
		commandManager.addCommand(command);
	}

	private void cutBoxContent() {
		Command command = commandFactory.createCutCommand(toolPosition, boxWidth, boxHeight, boxRotation);
		commandManager.addCommand(command);
	}

	@Override
	public ToolType getToolType() {
		return ToolType.STAMP;
	}

	@Override
	public void onClickOnButton() {
		if (!readyForPaste || drawingBitmap == null) {
			contextCallback.showNotification(R.string.stamp_tool_copy_hint);
		} else if (boxIntersectsWorkspace()) {
			pasteBoxContent();
			highlightBox();
		}
	}

	@Override
	public void resetInternalState() {
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		bundle.putParcelable(BUNDLE_TOOL_DRAWING_BITMAP, drawingBitmap);
		bundle.putBoolean(BUNDLE_TOOL_READY_FOR_PASTE, readyForPaste);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		readyForPaste = bundle.getBoolean(BUNDLE_TOOL_READY_FOR_PASTE, readyForPaste);
		Bitmap bitmap = bundle.getParcelable(BUNDLE_TOOL_DRAWING_BITMAP);
		if (bitmap != null) {
			drawingBitmap = bitmap;
		}

		stampToolOptionsView.enablePaste(readyForPaste);
	}

	private boolean isDrawingBitmapReusable() {
		return drawingBitmap != null
				&& drawingBitmap.getWidth() == (int) boxWidth
				&& drawingBitmap.getHeight() == (int) boxHeight;
	}
}
