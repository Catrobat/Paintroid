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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.annotation.VisibleForTesting;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.model.LayerModel;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.ToastFactory;
import org.catrobat.paintroid.ui.tools.NumberRangeFilter;

import java.util.Locale;

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

	private EditText widthEditText;
	private EditText heightEditText;

	private TextWatcher textWatcherHeight;
	private TextWatcher textWatcherWidth;

	private NumberRangeFilter rangeFilterHeight;
	private NumberRangeFilter rangeFilterWidth;

	private View transformToolOptionView;

	public TransformTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BORDERS);
		setResizePointsVisible(RESIZE_POINTS_VISIBLE);
		setRespectMaximumBorderRatio(RESPECT_MAXIMUM_BORDER_RATIO);

		boxHeight = PaintroidApplication.layerModel.getHeight();
		boxWidth = PaintroidApplication.layerModel.getWidth();
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
			setWidthAndHeightTexts(boxHeight, boxWidth);
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
		final DrawingSurface drawingSurface = PaintroidApplication.drawingSurface;
		resizeBoundWidthXRight = 0;
		resizeBoundHeightYBottom = 0;
		resizeBoundWidthXLeft = drawingSurface.getBitmapWidth();
		resizeBoundHeightYTop = drawingSurface.getBitmapHeight();
		resetScaleAndTranslation();
		resizeBoundWidthXRight = drawingSurface.getBitmapWidth() - 1;
		resizeBoundHeightYBottom = drawingSurface.getBitmapHeight() - 1;
		resizeBoundWidthXLeft = 0f;
		resizeBoundHeightYTop = 0f;
		setRectangle(new RectF(resizeBoundWidthXLeft,
				resizeBoundHeightYTop, resizeBoundWidthXRight,
				resizeBoundHeightYBottom));
		cropRunFinished = true;
		setWidthAndHeightTexts(boxHeight, boxWidth);
	}

	private void executeResizeCommand() {
		if (cropRunFinished) {
			cropRunFinished = false;
			initResizeBounds();
			if (areResizeBordersValid()) {
				Command resizeCommand = commandFactory.createResizeCommand(
						(int) Math.floor(resizeBoundWidthXLeft),
						(int) Math.floor(resizeBoundHeightYTop),
						(int) Math.floor(resizeBoundWidthXRight),
						(int) Math.floor(resizeBoundHeightYBottom),
						(int) maximumBoxResolution);
				PaintroidApplication.commandManager.addCommand(resizeCommand);
			} else {
				cropRunFinished = true;
				ToastFactory.makeText(context, R.string.resize_nothing_to_resize,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void flip(FlipCommand.FlipDirection flipDirection) {
		Command command = commandFactory.createFlipCommand(flipDirection);
		PaintroidApplication.commandManager.addCommand(command);
	}

	private void rotate(RotateCommand.RotateDirection rotateDirection) {
		Command command = commandFactory.createRotateCommand(rotateDirection);
		PaintroidApplication.commandManager.addCommand(command);

		float tempBoxWidth = boxWidth;
		boxWidth = boxHeight;
		boxHeight = tempBoxWidth;
	}

	private void autoCrop() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				Rect shapeBounds = cropAlgorithmSnail(LayerModel.getBitmapOfAllLayersToSave(PaintroidApplication.layerModel.getLayers()));
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
				setWidthAndHeightTexts(boxHeight, boxWidth);
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
		transformToolOptionView = inflater.inflate(R.layout.dialog_pocketpaint_transform_tool, toolSpecificOptionsLayout);

		rangeFilterHeight = new NumberRangeFilter(1, (int) (maximumBoxResolution / (boxWidth)));
		rangeFilterWidth = new NumberRangeFilter(1, (int) (maximumBoxResolution / (boxHeight)));

		widthEditText = toolSpecificOptionsLayout.findViewById(R.id.pocketpaint_transform_width_value);
		widthEditText.setFilters(new InputFilter[]{rangeFilterWidth});
		textWatcherWidth = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = widthEditText.getText().toString();
				if (str.isEmpty()) {
					str = "1";
				}
				boxWidth = Float.parseFloat(str);
			}
		};
		widthEditText.addTextChangedListener(textWatcherWidth);

		heightEditText = toolSpecificOptionsLayout.findViewById(R.id.pocketpaint_transform_height_value);
		heightEditText.setFilters(new InputFilter[]{rangeFilterHeight});
		textWatcherHeight = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = heightEditText.getText().toString();
				if (str.isEmpty()) {
					str = "1";
				}
				boxHeight = Float.parseFloat(str);
			}
		};
		heightEditText.addTextChangedListener(textWatcherHeight);

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int i = v.getId();
				if (i == R.id.pocketpaint_transform_auto_crop_btn) {
					autoCrop();
				} else if (i == R.id.pocketpaint_transform_rotate_left_btn) {
					rotate(RotateCommand.RotateDirection.ROTATE_LEFT);
				} else if (i == R.id.pocketpaint_transform_rotate_right_btn) {
					rotate(RotateCommand.RotateDirection.ROTATE_RIGHT);
				} else if (i == R.id.pocketpaint_transform_flip_horizontal_btn) {
					flip(FlipCommand.FlipDirection.FLIP_HORIZONTAL);
				} else if (i == R.id.pocketpaint_transform_flip_vertical_btn) {
					flip(FlipCommand.FlipDirection.FLIP_VERTICAL);
				}
			}
		};

		int[] buttonIdList = {
				R.id.pocketpaint_transform_auto_crop_btn,
				R.id.pocketpaint_transform_rotate_left_btn, R.id.pocketpaint_transform_rotate_right_btn,
				R.id.pocketpaint_transform_flip_horizontal_btn, R.id.pocketpaint_transform_flip_vertical_btn};

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

	private void setWidthAndHeightTexts(float heightValue, float widthValue) {

		final float height = heightValue;
		final float width = widthValue;
		((Activity) transformToolOptionView.getContext()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				widthEditText.removeTextChangedListener(textWatcherWidth);
				heightEditText.removeTextChangedListener(textWatcherHeight);

				rangeFilterHeight.setMax((int) (maximumBoxResolution / (boxWidth)));
				rangeFilterWidth.setMax((int) (maximumBoxResolution / (boxHeight)));

				widthEditText.setText(String.format(Locale.getDefault(), "%d", (int) width));
				heightEditText.setText(String.format(Locale.getDefault(), "%d", (int) height));

				widthEditText.addTextChangedListener(textWatcherWidth);
				heightEditText.addTextChangedListener(textWatcherHeight);
			}
		});
	}
}
