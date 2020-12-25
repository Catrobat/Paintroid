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

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.helper.CropAlgorithm;
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter;
import org.catrobat.paintroid.tools.helper.JavaCropAlgorithm;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.catrobat.paintroid.tools.options.TransformToolOptionsView;
import org.catrobat.paintroid.ui.tools.NumberRangeFilter;

import androidx.annotation.VisibleForTesting;

public class TransformTool extends BaseToolWithRectangleShape {

	public static final String TAG = TransformTool.class.getSimpleName();
	@VisibleForTesting
	public static final float MAXIMUM_BITMAP_SIZE_FACTOR = 4.0f;
	private static final float START_ZOOM_FACTOR = 0.95f;
	private static final boolean ROTATION_ENABLED = false;
	private static final boolean RESIZE_POINTS_VISIBLE = false;
	private static final boolean RESPECT_MAXIMUM_BORDER_RATIO = false;
	private static final boolean RESPECT_MAXIMUM_BOX_RESOLUTION = true;
	@VisibleForTesting
	public float resizeBoundWidthXLeft;
	@VisibleForTesting
	public float resizeBoundWidthXRight = 0;
	@VisibleForTesting
	public float resizeBoundHeightYTop;
	@VisibleForTesting
	public float resizeBoundHeightYBottom = 0;

	private boolean cropRunFinished = false;
	private boolean maxImageResolutionInformationAlreadyShown = false;
	private boolean zeroSizeBitmap = false;

	private TransformToolOptionsView transformToolOptionsView;
	private NumberRangeFilter rangeFilterHeight;
	private NumberRangeFilter rangeFilterWidth;
	private final CropAlgorithm cropAlgorithm;

	public TransformTool(TransformToolOptionsView transformToolOptionsView, final ContextCallback contextCallback,
						ToolOptionsVisibilityController toolOptionsViewController, ToolPaint toolPaint, Workspace workspace, CommandManager commandManager) {
		super(contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);

		this.transformToolOptionsView = transformToolOptionsView;

		this.rotationEnabled = ROTATION_ENABLED;
		this.resizePointsVisible = RESIZE_POINTS_VISIBLE;
		this.respectMaximumBorderRatio = RESPECT_MAXIMUM_BORDER_RATIO;

		boxHeight = workspace.getHeight();
		boxWidth = workspace.getWidth();
		toolPosition.x = boxWidth / 2f;
		toolPosition.y = boxHeight / 2f;

		cropAlgorithm = new JavaCropAlgorithm();

		cropRunFinished = true;

		this.maximumBoxResolution = metrics.widthPixels * metrics.heightPixels * MAXIMUM_BITMAP_SIZE_FACTOR;
		this.respectMaximumBoxResolution = RESPECT_MAXIMUM_BOX_RESOLUTION;
		initResizeBounds();

		toolOptionsViewController.setCallback(new ToolOptionsViewController.Callback() {
			@Override
			public void onHide() {
				if (!zeroSizeBitmap) {
					contextCallback.showNotification(R.string.transform_info_text, ContextCallback.NotificationDuration.LONG);
				} else {
					zeroSizeBitmap = false;
				}
			}

			@Override
			public void onShow() {
				updateToolOptions();
			}
		});

		transformToolOptionsView.setCallback(new TransformToolOptionsView.Callback() {
			@Override
			public void autoCropClicked() {
				autoCrop();
			}

			@Override
			public void rotateCounterClockwiseClicked() {
				rotateCounterClockWise();
			}

			@Override
			public void rotateClockwiseClicked() {
				rotateClockWise();
			}

			@Override
			public void flipHorizontalClicked() {
				flipHorizontal();
			}

			@Override
			public void flipVerticalClicked() {
				flipVertical();
			}

			@Override
			public void setBoxWidth(float boxWidth) {
				TransformTool.this.boxWidth = boxWidth;
			}

			@Override
			public void setBoxHeight(float boxHeight) {
				TransformTool.this.boxHeight = boxHeight;
			}

			@Override
			public void hideToolOptions() {
				TransformTool.this.toolOptionsViewController.hide();
			}

			@Override
			public void applyResizeClicked(int resizePercentage) {
				onApplyResizeClicked(resizePercentage);
			}
		});

		rangeFilterHeight = new DefaultNumberRangeFilter(1, (int) (maximumBoxResolution / boxWidth));
		rangeFilterWidth = new DefaultNumberRangeFilter(1, (int) (maximumBoxResolution / boxHeight));

		transformToolOptionsView.setHeightFilter(rangeFilterHeight);
		transformToolOptionsView.setWidthFilter(rangeFilterWidth);

		updateToolOptions();
		toolOptionsViewController.showDelayed();
	}

	@Override
	public void resetInternalState() {
		initialiseResizingState();
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas, float boxWidth, float boxHeight) {
		if (cropRunFinished) {
			linePaint.setColor(primaryShapeColor);
			linePaint.setStrokeWidth(toolStrokeWidth * 2);

			PointF rightTopPoint = new PointF(-boxWidth / 2, -boxHeight / 2);

			for (int lines = 0; lines < 4; lines++) {
				float resizeLineLengthHeight = boxHeight / 10;
				float resizeLineLengthWidth = boxWidth / 10;

				canvas.drawLine(rightTopPoint.x - toolStrokeWidth / 2,
						rightTopPoint.y, rightTopPoint.x + resizeLineLengthWidth,
						rightTopPoint.y, linePaint);

				canvas.drawLine(rightTopPoint.x, rightTopPoint.y
								- toolStrokeWidth / 2, rightTopPoint.x,
						rightTopPoint.y + resizeLineLengthHeight, linePaint);

				canvas.drawLine(rightTopPoint.x + boxWidth / 2
								- resizeLineLengthWidth, rightTopPoint.y, rightTopPoint.x
								+ boxWidth / 2 + resizeLineLengthWidth, rightTopPoint.y,
						linePaint);
				canvas.rotate(90);
				float tempX = rightTopPoint.x;
				rightTopPoint.x = rightTopPoint.y;
				rightTopPoint.y = tempX;
				float tempHeight = boxHeight;
				boxHeight = boxWidth;
				boxWidth = tempHeight;
			}
		}
	}

	private void resetScaleAndTranslation() {
		workspace.resetPerspective();
		float zoomFactor = workspace.getScaleForCenterBitmap() * START_ZOOM_FACTOR;
		workspace.setScale(zoomFactor);
	}

	private void initialiseResizingState() {
		cropRunFinished = false;
		resizeBoundWidthXRight = 0;
		resizeBoundHeightYBottom = 0;
		resizeBoundWidthXLeft = workspace.getWidth();
		resizeBoundHeightYTop = workspace.getHeight();
		resetScaleAndTranslation();
		resizeBoundWidthXRight = workspace.getWidth() - 1;
		resizeBoundHeightYBottom = workspace.getHeight() - 1;
		resizeBoundWidthXLeft = 0f;
		resizeBoundHeightYTop = 0f;
		setRectangle(new RectF(resizeBoundWidthXLeft,
				resizeBoundHeightYTop, resizeBoundWidthXRight,
				resizeBoundHeightYBottom));
		cropRunFinished = true;
		updateToolOptions();
	}

	private void executeResizeCommand() {
		if (cropRunFinished) {
			cropRunFinished = false;
			initResizeBounds();
			if (areResizeBordersValid()) {
				Command resizeCommand = commandFactory.createCropCommand(
						(int) Math.floor(resizeBoundWidthXLeft),
						(int) Math.floor(resizeBoundHeightYTop),
						(int) Math.floor(resizeBoundWidthXRight),
						(int) Math.floor(resizeBoundHeightYBottom),
						(int) maximumBoxResolution);
				commandManager.addCommand(resizeCommand);
			} else {
				cropRunFinished = true;
				contextCallback.showNotification(R.string.resize_nothing_to_resize);
			}
		}
	}

	private void onApplyResizeClicked(int resizePercentage) {
		int newWidth = (int) ((float) workspace.getWidth() / 100 * resizePercentage);
		int newHeight = (int) ((float) workspace.getHeight() / 100 * resizePercentage);

		if (newWidth == 0 || newHeight == 0) {
			zeroSizeBitmap = true;
			contextCallback.showNotification(R.string.resize_cannot_resize_to_this_size, ContextCallback.NotificationDuration.LONG);
		} else {
			Command command = commandFactory.createResizeCommand(newWidth, newHeight);
			commandManager.addCommand(command);
		}
	}

	private void flipHorizontal() {
		Command command = commandFactory.createFlipCommand(FlipCommand.FlipDirection.FLIP_HORIZONTAL);
		commandManager.addCommand(command);
	}

	private void flipVertical() {
		Command command = commandFactory.createFlipCommand(FlipCommand.FlipDirection.FLIP_VERTICAL);
		commandManager.addCommand(command);
	}

	private void rotateCounterClockWise() {
		Command command = commandFactory.createRotateCommand(RotateCommand.RotateDirection.ROTATE_LEFT);
		commandManager.addCommand(command);

		swapWidthAndHeight();
	}

	private void rotateClockWise() {
		Command command = commandFactory.createRotateCommand(RotateCommand.RotateDirection.ROTATE_RIGHT);
		commandManager.addCommand(command);

		swapWidthAndHeight();
	}

	private void swapWidthAndHeight() {
		float tempBoxWidth = boxWidth;
		boxWidth = boxHeight;
		boxHeight = tempBoxWidth;
	}

	private void autoCrop() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Rect shapeBounds = cropAlgorithm.crop(workspace.getBitmapOfAllLayers());
				if (shapeBounds != null) {
					boxWidth = shapeBounds.width() + 1;
					boxHeight = shapeBounds.height() + 1;
					toolPosition.x = shapeBounds.left + (shapeBounds.width() + 1) / 2.0f;
					toolPosition.y = shapeBounds.top + (shapeBounds.height() + 1) / 2.0f;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				workspace.invalidate();
				toolOptionsViewController.hide();
			}
		}.execute();
	}

	private boolean areResizeBordersValid() {
		if (resizeBoundWidthXRight < resizeBoundWidthXLeft
				|| resizeBoundHeightYTop > resizeBoundHeightYBottom) {
			return false;
		}
		if (resizeBoundWidthXLeft >= workspace.getWidth()
				|| resizeBoundWidthXRight < 0 || resizeBoundHeightYBottom < 0
				|| resizeBoundHeightYTop >= workspace.getHeight()) {
			return false;
		}
		if (resizeBoundWidthXLeft == 0 && resizeBoundHeightYTop == 0
				&& resizeBoundWidthXRight == workspace.getWidth() - 1
				&& resizeBoundHeightYBottom == workspace.getHeight() - 1) {
			return false;
		}
		if ((resizeBoundWidthXRight + 1 - resizeBoundWidthXLeft)
				* (resizeBoundHeightYBottom + 1 - resizeBoundHeightYTop) > maximumBoxResolution) {
			return false;
		}

		return true;
	}

	private void setRectangle(RectF rectangle) {
		boxWidth = rectangle.right - rectangle.left + 1f;
		boxHeight = rectangle.bottom - rectangle.top + 1f;
		toolPosition.x = rectangle.left + boxWidth / 2f;
		toolPosition.y = rectangle.top + boxHeight / 2f;
	}

	private void initResizeBounds() {
		resizeBoundWidthXLeft = toolPosition.x - boxWidth / 2f;
		resizeBoundWidthXRight = toolPosition.x + boxWidth / 2f - 1f;
		resizeBoundHeightYTop = toolPosition.y - boxHeight / 2f;
		resizeBoundHeightYBottom = toolPosition.y + boxHeight / 2f - 1f;
	}

	@Override
	public void onClickOnButton() {
		executeResizeCommand();
	}

	@Override
	protected void preventThatBoxGetsTooLarge(float oldWidth, float oldHeight,
			float oldPosX, float oldPosY) {
		super.preventThatBoxGetsTooLarge(oldWidth, oldHeight, oldPosX, oldPosY);
		if (!maxImageResolutionInformationAlreadyShown) {
			contextCallback.showNotification(R.string.resize_max_image_resolution_reached);
			maxImageResolutionInformationAlreadyShown = true;
		}
	}

	@Override
	public ToolType getToolType() {
		return ToolType.TRANSFORM;
	}

	private void updateToolOptions() {
		rangeFilterHeight.setMax((int) (maximumBoxResolution / boxWidth));
		rangeFilterWidth.setMax((int) (maximumBoxResolution / boxHeight));

		transformToolOptionsView.setWidth((int) boxWidth);
		transformToolOptionsView.setHeight((int) boxHeight);
	}
}
