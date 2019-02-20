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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.VisibleForTesting;
import android.view.ViewConfiguration;
import android.widget.Toast;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.helper.Conversion;
import org.catrobat.paintroid.ui.ToastFactory;

public class StampTool extends BaseToolWithRectangleShape {

	public static final String BUNDLE_TOOL_READY_FOR_PASTE = "BUNDLE_TOOL_READY_FOR_PASTE";
	private static final boolean ROTATION_ENABLED = true;
	private static final String BUNDLE_TOOL_DRAWING_BITMAP = "BUNDLE_TOOL_DRAWING_BITMAP";
	protected static CreateAndSetBitmapAsyncTask createAndSetBitmapAsync = null;
	protected boolean readyForPaste;
	protected boolean longClickAllowed = true;

	private int longPressTimeout;
	private Toast copyHintToast;
	private CountDownTimer downTimer;
	private boolean longClickPerformed;

	public StampTool(Context context, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(context, toolPaint, workspace, commandManager);
		readyForPaste = false;
		longPressTimeout = ViewConfiguration.getLongPressTimeout();
		setRotationEnabled(ROTATION_ENABLED);

		setBitmap(Bitmap.createBitmap((int) boxWidth, (int) boxHeight,
				Config.ARGB_8888));

		createAndSetBitmapAsync = new CreateAndSetBitmapAsyncTask();
	}

	public void setBitmapFromFile(Bitmap bitmap) {
		super.setBitmap(bitmap);
		readyForPaste = true;
	}

	@VisibleForTesting
	public void createAndSetBitmap() {
		float boxRotation = (this.boxRotation % 90 + 90) % 90;

		double rotationRadians = Math.toRadians(boxRotation);
		double boundingBoxX = Math.abs(boxWidth * Math.sin(rotationRadians)
				+ boxHeight * Math.cos(rotationRadians));

		double boundingBoxY = Math.abs(boxWidth * Math.cos(rotationRadians)
				+ boxHeight * Math.sin(rotationRadians));

		double distanceToMassCentre = Math.hypot(
				toolPosition.x + boundingBoxX / 2,
				toolPosition.y + boundingBoxY / 2);

		Bitmap tmpBitmap = Bitmap.createBitmap((int) distanceToMassCentre * 2,
				(int) distanceToMassCentre * 2, Config.ARGB_8888);

		Canvas tmpCanvas = new Canvas(tmpBitmap);

		Rect rectSource = new Rect((int) toolPosition.x
				- (int) distanceToMassCentre, (int) toolPosition.y
				- (int) distanceToMassCentre, (int) toolPosition.x
				+ (int) distanceToMassCentre, (int) toolPosition.y
				+ (int) distanceToMassCentre);

		Rect rectDest = new Rect(0, 0, (int) distanceToMassCentre * 2,
				(int) distanceToMassCentre * 2);

		tmpCanvas.save();
		tmpCanvas.rotate(-this.boxRotation, (float) distanceToMassCentre,
				(float) distanceToMassCentre);

		Bitmap copyOfCurrentDrawingSurfaceBitmap = workspace.getBitmapOfCurrentLayer();
		if (copyOfCurrentDrawingSurfaceBitmap == null || copyOfCurrentDrawingSurfaceBitmap.isRecycled()) {
			return;
		}
		tmpCanvas.drawBitmap(copyOfCurrentDrawingSurfaceBitmap, rectSource,
				rectDest, null);
		copyOfCurrentDrawingSurfaceBitmap.recycle();

		tmpCanvas.restore();

		// now get tmp back to bitmap, rotate and clip
		if (canUseOldDrawingBitmap()) {
			setBitmap(drawingBitmap = Bitmap.createBitmap((int) boxWidth,
					(int) boxHeight, Config.ARGB_8888));
		}
		Canvas canvasDraw = new Canvas(drawingBitmap);

		double left = distanceToMassCentre - boxWidth / 2;
		double top = distanceToMassCentre - boxHeight / 2;
		double right = distanceToMassCentre * 2 - left;
		double bottom = distanceToMassCentre * 2 - top;
		Rect rectSourceResult = new Rect((int) left, (int) top, (int) right,
				(int) bottom);

		Rect rectDestResult = new Rect(0, 0, (int) boxWidth, (int) boxHeight);

		canvasDraw.drawBitmap(tmpBitmap, rectSourceResult, rectDestResult, null);

		tmpBitmap.recycle();

		readyForPaste = true;
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
				float downToolPositionX = toolPosition.x;
				float downToolPositionY = toolPosition.y;
				@Override
				public void onTick(long millisUntilFinished) {
				}

				@Override
				public void onFinish() {
					if (movedDistance.x <= CLICK_IN_BOX_MOVE_TOLERANCE
							&& movedDistance.y <= CLICK_IN_BOX_MOVE_TOLERANCE
							&& isCoordinateInsideBox(previousEventCoordinate)) {
						onLongClickInBox(downToolPositionX, downToolPositionY);
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
		if (!readyForPaste) {
			copyHintToast = ToastFactory.makeText(context, R.string.stamp_tool_copy_hint, Toast.LENGTH_SHORT);
			copyHintToast.show();
		} else if (drawingBitmap != null && !drawingBitmap.isRecycled()) {
			paste();
			highlightBox();
		}
	}

	private void onLongClickInBox(float toolPositionX, float toolPositionY) {
		longClickPerformed = true;
		highlightBoxWhenClickInBox(true);
		toolPosition.set(toolPositionX, toolPositionY);
		workspace.invalidate();
		copy();
	}

	private void copy() {
		if (toolPosition.x - boxWidth / 2 < workspace.getWidth()
				&& toolPosition.y - boxHeight / 2 < workspace.getHeight()
				&& toolPosition.x + boxWidth / 2 >= 0
				&& toolPosition.y + boxHeight / 2 >= 0
				&& createAndSetBitmapAsync.getStatus() != AsyncTask.Status.RUNNING) {

			createAndSetBitmapAsync = new CreateAndSetBitmapAsyncTask();
			createAndSetBitmapAsync.execute();
		}
	}

	private void paste() {
		if (toolPosition.x - boxWidth / 2 < workspace.getWidth()
				&& toolPosition.y - boxHeight / 2 < workspace.getHeight()
				&& toolPosition.x + boxWidth / 2 >= 0
				&& toolPosition.y + boxHeight / 2 >= 0) {

			Command command = new StampCommand(drawingBitmap, Conversion.toPoint(toolPosition), boxWidth, boxHeight, boxRotation);
			commandManager.addCommand(command);
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

	private boolean canUseOldDrawingBitmap() {
		if (drawingBitmap != null && !drawingBitmap.isRecycled()
				&& drawingBitmap.getWidth() == (int) boxWidth
				&& drawingBitmap.getHeight() == (int) boxHeight) {
			drawingBitmap.eraseColor(Color.TRANSPARENT);
			return false;
		}
		return true;
	}

	protected class CreateAndSetBitmapAsyncTask extends
			AsyncTask<Void, Integer, Void> {
		@Override
		protected Void doInBackground(Void... arg0) {
			createAndSetBitmap();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			workspace.invalidate();
		}
	}
}
