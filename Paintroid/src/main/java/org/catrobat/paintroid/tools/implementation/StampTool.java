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
import android.graphics.PointF;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.ViewConfiguration;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;

public class StampTool extends BaseToolWithRectangleShape {

	private static final String BUNDLE_TOOL_READY_FOR_PASTE = "BUNDLE_TOOL_READY_FOR_PASTE";
	private static final String BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP";
	private static final boolean ROTATION_ENABLED = true;
	protected boolean readyForPaste;
	protected boolean longClickAllowed = true;

	private int longPressTimeout;
	private CountDownTimer downTimer;
	private boolean longClickPerformed;

	public StampTool(ContextCallback contextCallback, ToolOptionsVisibilityController toolOptionsViewController,
			ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
		readyForPaste = false;
		longPressTimeout = ViewConfiguration.getLongPressTimeout();
		this.rotationEnabled = ROTATION_ENABLED;

		setBitmap(Bitmap.createBitmap((int) boxWidth, (int) boxHeight, Config.ARGB_8888));
	}

	public void setBitmapFromFile(Bitmap bitmap) {
		super.setBitmap(bitmap);
		readyForPaste = true;
	}

	public void copyBoxContent() {
		if (isDrawingBitmapReusable()) {
			drawingBitmap.eraseColor(Color.TRANSPARENT);
		} else {
			drawingBitmap = Bitmap.createBitmap((int) boxWidth, (int) boxHeight, Config.ARGB_8888);
		}

		Bitmap layerBitmap = workspace.getBitmapOfAllLayers();

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

	@Override
	public boolean handleDown(PointF coordinate) {
		super.handleDown(coordinate);
		longClickPerformed = false;
		if (longClickAllowed) {
			if (downTimer != null) {
				downTimer.cancel();
			}
			downTimer = new CountDownTimer(longPressTimeout, longPressTimeout * 2) {

				@Override
				public void onTick(long millisUntilFinished) {
				}

				@Override
				public void onFinish() {
					if (movedDistance.x <= CLICK_IN_BOX_MOVE_TOLERANCE
							&& movedDistance.y <= CLICK_IN_BOX_MOVE_TOLERANCE
							&& boxContainsPoint(previousEventCoordinate)) {
						onLongClickInBox(touchDownPositionX, touchDownPositionY);
					}
				}
			}.start();
		}
		return true;
	}

	@Override
	public boolean handleMove(PointF coordinate) {
		return longClickPerformed || super.handleMove(coordinate);
	}

	@Override
	public boolean handleUp(PointF coordinate) {
		highlightBoxWhenClickInBox(false);
		if (longClickPerformed) {
			return true;
		}

		if (longClickAllowed) {
			downTimer.cancel();
		}

		return super.handleUp(coordinate);
	}

	@Override
	public ToolType getToolType() {
		return ToolType.STAMP;
	}

	@Override
	protected void onClickInBox() {
		if (!readyForPaste || drawingBitmap == null) {
			contextCallback.showNotification(R.string.stamp_tool_copy_hint);
		} else if (boxIntersectsWorkspace()) {
			pasteBoxContent();
			highlightBox();
		}
	}

	private void onLongClickInBox(float toolPositionX, float toolPositionY) {
		longClickPerformed = true;
		highlightBoxWhenClickInBox(true);
		toolPosition.set(toolPositionX, toolPositionY);
		workspace.invalidate();

		if (boxIntersectsWorkspace()) {
			copyBoxContent();
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
	}

	private boolean isDrawingBitmapReusable() {
		return drawingBitmap != null
				&& drawingBitmap.getWidth() == (int) boxWidth
				&& drawingBitmap.getHeight() == (int) boxHeight;
	}
}
