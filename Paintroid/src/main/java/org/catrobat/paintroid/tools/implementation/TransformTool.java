/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.ToastFactory;

import java.util.Observable;

public class TransformTool extends BaseToolWithRectangleShape {

	private static final float START_ZOOM_FACTOR = 0.95f;
	private static final boolean ROTATION_ENABLED = false;
	private static final boolean RESPECT_IMAGE_BORDERS = false;
	private static final boolean RESIZE_POINTS_VISIBLE = false;
	private static final boolean RESPECT_MAXIMUM_BORDER_RATIO = false;
	private static final boolean RESPECT_MAXIMUM_BOX_RESOLUTION = true;
	@VisibleForTesting
	public static final float MAXIMUM_BITMAP_SIZE_FACTOR = 4.0f;

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

	private View transformToolOptionView;

	public TransformTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BORDERS);
		setResizePointsVisible(RESIZE_POINTS_VISIBLE);
		setRespectMaximumBorderRatio(RESPECT_MAXIMUM_BORDER_RATIO);

		final DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;

		if (!drawingSurface.isBitmapNull()) {
			boxHeight = drawingSurface.getBitmapHeight();
			boxWidth = drawingSurface.getBitmapWidth();
		}
		toolPosition.x = boxWidth / 2f;
		toolPosition.y = boxHeight / 2f;

		resetScaleAndTranslation();

		cropRunFinished = true;

		final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		setMaximumBoxResolution(metrics.widthPixels * metrics.heightPixels
				* MAXIMUM_BITMAP_SIZE_FACTOR);
		setRespectMaximumBoxResolution(RESPECT_MAXIMUM_BOX_RESOLUTION);
		initResizeBounds();
	}

	private static boolean containsNotTransparentPixel(int[][] pixels, int fromX, int fromY, int toX, int toY) {
		for (int y = fromY; y <= toY; y++) {
			for (int x = fromX; x <= toX; x++) {
				if (pixels[y][x] != Color.TRANSPARENT) {
					return true;
				}
			}
		}
		return false;
	}

	@VisibleForTesting
	public static Rect cropAlgorithmSnail(Bitmap bitmap) {
		if (bitmap == null) {
			Log.e("cropAlgorithmSnail", "bitmap is null!");
			return null;
		}

		int[][] pixels = new int[bitmap.getHeight()][bitmap.getWidth()];
		for (int i = 0; i < bitmap.getHeight(); i++) {
			bitmap.getPixels(pixels[i], 0, bitmap.getWidth(), 0, i, bitmap.getWidth(), 1);
		}

		Rect bounds = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
		int x;
		int y;
		for (y = bounds.top; y <= bounds.bottom; y++) {
			bounds.top = y;
			if (containsNotTransparentPixel(pixels, bounds.left, y, bounds.right, y)) {
				break;
			}
		}
		if (y > bounds.bottom) {
			Log.i("cropAlgorithmSnail", "nothing to crop");
			return null;
		}

		for (x = bounds.left; x <= bounds.right; x++) {
			bounds.left = x;
			if (containsNotTransparentPixel(pixels, x, bounds.top, x, bounds.bottom)) {
				break;
			}
		}

		for (y = bounds.bottom; y >= bounds.top; y--) {
			bounds.bottom = y;
			if (containsNotTransparentPixel(pixels, bounds.left, y, bounds.right, y)) {
				break;
			}
		}

		for (x = bounds.right; x >= bounds.left; x--) {
			bounds.right = x;
			if (containsNotTransparentPixel(pixels, x, bounds.top, x, bounds.bottom)) {
				break;
			}
		}

		return bounds;
	}

	@Override
	public void resetInternalState() {
		resetScaleAndTranslation();
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
		PaintroidApplication.perspective.resetScaleAndTranslation();
		float zoomFactor = PaintroidApplication.perspective
				.getScaleForCenterBitmap() * START_ZOOM_FACTOR;
		PaintroidApplication.perspective.setScale(zoomFactor);
	}

	private void initialiseResizingState() {
		cropRunFinished = false;
		resizeBoundWidthXRight = 0;
		resizeBoundHeightYBottom = 0;
		resizeBoundWidthXLeft = PaintroidApplication.drawingSurface
				.getBitmapWidth();
		resizeBoundHeightYTop = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		resetScaleAndTranslation();
	}

	private void executeResizeCommand() {
		if (cropRunFinished) {
			cropRunFinished = false;
			initResizeBounds();
			if (areResizeBordersValid()) {

				for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
					Command resizeCommand = new ResizeCommand((int) Math.floor(resizeBoundWidthXLeft),
							(int) Math.floor(resizeBoundHeightYTop),
							(int) Math.floor(resizeBoundWidthXRight),
							(int) Math.floor(resizeBoundHeightYBottom),
							(int) maximumBoxResolution);

					if (layer.getSelected()) {
						((ResizeCommand) resizeCommand).addObserver(this);
					}
					PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), resizeCommand);
				}
			} else {
				cropRunFinished = true;
				ToastFactory.makeText(context, R.string.resize_nothing_to_resize,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void flip(FlipCommand.FlipDirection flipDirection) {
		Command command = new FlipCommand(flipDirection);
		IndeterminateProgressDialog.getInstance().show();
		((FlipCommand) command).addObserver(this);
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
	}

	private void rotate(RotateCommand.RotateDirection rotateDirection) {
		IndeterminateProgressDialog.getInstance().show();
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			Command command = new RotateCommand(rotateDirection);

			if (layer.getSelected()) {
				((RotateCommand) command).addObserver(this);
			}
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
		}
	}

	private void autoCrop() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				IndeterminateProgressDialog.getInstance().show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				Rect shapeBounds = cropAlgorithmSnail(LayerListener.getInstance().getBitmapOfAllLayersToSave());
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
				PaintroidApplication.drawingSurface.refreshDrawingSurface();
				IndeterminateProgressDialog.getInstance().dismiss();
			}
		}.execute();
	}

	private boolean areResizeBordersValid() {
		if (resizeBoundWidthXRight < resizeBoundWidthXLeft
				|| resizeBoundHeightYTop > resizeBoundHeightYBottom) {
			return false;
		}
		if (resizeBoundWidthXLeft >= PaintroidApplication.drawingSurface.getBitmapWidth()
				|| resizeBoundWidthXRight < 0 || resizeBoundHeightYBottom < 0
				|| resizeBoundHeightYTop >= PaintroidApplication.drawingSurface.getBitmapHeight()) {
			return false;
		}
		if (resizeBoundWidthXLeft == 0 && resizeBoundHeightYTop == 0
				&& resizeBoundWidthXRight == PaintroidApplication.drawingSurface.getBitmapWidth() - 1
				&& resizeBoundHeightYBottom == PaintroidApplication.drawingSurface.getBitmapHeight() - 1) {
			return false;
		}
		if ((resizeBoundWidthXRight + 1 - resizeBoundWidthXLeft)
				* (resizeBoundHeightYBottom + 1 - resizeBoundHeightYTop) > maximumBoxResolution) {
			return false;
		}

		return true;
	}

	@Override
	public void update(Observable observable, Object data) {
		super.update(observable, data);
		if (data instanceof BaseCommand.NotifyStates
				&& (BaseCommand.NotifyStates.COMMAND_DONE == data
				|| BaseCommand.NotifyStates.COMMAND_FAILED == data)) {
			initialiseResizingState();
			final DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
			resizeBoundWidthXRight = drawingSurface.getBitmapWidth() - 1;
			resizeBoundHeightYBottom = drawingSurface.getBitmapHeight() - 1;
			resizeBoundWidthXLeft = 0f;
			resizeBoundHeightYTop = 0f;
			setRectangle(new RectF(resizeBoundWidthXLeft,
					resizeBoundHeightYTop, resizeBoundWidthXRight,
					resizeBoundHeightYBottom));
			cropRunFinished = true;
		}
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
	protected void onClickInBox() {
		executeResizeCommand();
	}

	@Override
	protected void preventThatBoxGetsTooLarge(float oldWidth, float oldHeight,
			float oldPosX, float oldPosY) {
		super.preventThatBoxGetsTooLarge(oldWidth, oldHeight, oldPosX, oldPosY);
		if (!maxImageResolutionInformationAlreadyShown) {
			ToastFactory.makeText(context, R.string.resize_max_image_resolution_reached,
					Toast.LENGTH_SHORT).show();
			maxImageResolutionInformationAlreadyShown = true;
		}
	}

	@Override
	public void setupToolOptions() {
		LayoutInflater inflater = LayoutInflater.from(context);
		transformToolOptionView = inflater.inflate(R.layout.dialog_transform_tool, toolSpecificOptionsLayout);

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.transform_auto_crop_btn:
						autoCrop();
						break;
					case R.id.transform_rotate_left_btn:
						rotate(RotateCommand.RotateDirection.ROTATE_LEFT);
						break;
					case R.id.transform_rotate_right_btn:
						rotate(RotateCommand.RotateDirection.ROTATE_RIGHT);
						break;
					case R.id.transform_flip_horizontal_btn:
						flip(FlipCommand.FlipDirection.FLIP_HORIZONTAL);
						break;
					case R.id.transform_flip_vertical_btn:
						flip(FlipCommand.FlipDirection.FLIP_VERTICAL);
						break;
					default:
						break;
				}
			}
		};

		int[] buttonIdList = {
				R.id.transform_auto_crop_btn,
				R.id.transform_rotate_left_btn, R.id.transform_rotate_right_btn,
				R.id.transform_flip_horizontal_btn, R.id.transform_flip_vertical_btn};

		for (int id : buttonIdList) {
			transformToolOptionView.findViewById(id).setOnClickListener(onClickListener);
		}

		toolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

	@Override
	public void toggleShowToolOptions() {
		super.toggleShowToolOptions();
		if (!toolOptionsShown) {
			ToastFactory.makeText(context, R.string.transform_info_text, Toast.LENGTH_LONG).show();
		}
	}
}
