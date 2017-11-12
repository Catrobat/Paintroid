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

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.FlipCommand;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

import java.util.List;
import java.util.Observable;

public class TransformTool extends BaseToolWithRectangleShape {

	private static final float START_ZOOM_FACTOR = 0.95f;
	private static final boolean ROTATION_ENABLED = false;
	private static final boolean RESPECT_IMAGE_BORDERS = false;
	private static final boolean RESIZE_POINTS_VISIBLE = false;
	private static final boolean RESPECT_MAXIMUM_BORDER_RATIO = false;
	private static final boolean RESPECT_MAXIMUM_BOX_RESOLUTION = true;
	private static final float MAXIMUM_BITMAP_SIZE_FACTOR = 4.0f;

	private float mResizeBoundWidthXLeft;
	private float mResizeBoundWidthXRight = 0;
	private float mResizeBoundHeightYTop;
	private float mResizeBoundHeightYBottom = 0;

	private boolean mCropRunFinished = false;
	private boolean mMaxImageResolutionInformationAlreadyShown = false;

	private View mTransformToolOptionView;

	public TransformTool(Context context, ToolType toolType) {
		super(context, toolType);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BORDERS);
		setResizePointsVisible(RESIZE_POINTS_VISIBLE);
		setRespectMaximumBorderRatio(RESPECT_MAXIMUM_BORDER_RATIO);

		if (!PaintroidApplication.drawingSurface.isBitmapNull()) {
			mBoxHeight = (PaintroidApplication.drawingSurface.getBitmapHeight());
			mBoxWidth = (PaintroidApplication.drawingSurface.getBitmapWidth());
		}
		mToolPosition.x = mBoxWidth / 2f;
		mToolPosition.y = mBoxHeight / 2f;

		resetScaleAndTranslation();

		mCropRunFinished = true;

		Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		int displayWidth = displaySize.x;
		int displayHeight = displaySize.y;
		setMaximumBoxResolution(displayWidth * displayHeight * MAXIMUM_BITMAP_SIZE_FACTOR);
		setRespectMaximumBoxResolution(RESPECT_MAXIMUM_BOX_RESOLUTION);
		initResizeBounds();
	}

	@Override
	public void resetInternalState() {
		resetScaleAndTranslation();
	}

	@Override
	protected void drawToolSpecifics(Canvas canvas) {
		if (mCropRunFinished) {
			mLinePaint.setColor(mPrimaryShapeColor);
			mLinePaint.setStrokeWidth(mToolStrokeWidth * 2);

			PointF rightTopPoint = new PointF(-mBoxWidth / 2, -mBoxHeight / 2);

			float tempWidth = mBoxWidth;

			for (int lines = 0; lines < 4; lines++) {
				float resizeLineLengthHeight = mBoxHeight / 10;
				float resizeLineLengthWidth = mBoxWidth / 10;

				canvas.drawLine(rightTopPoint.x - mToolStrokeWidth / 2,
						rightTopPoint.y, rightTopPoint.x + resizeLineLengthWidth,
						rightTopPoint.y, mLinePaint);

				canvas.drawLine(rightTopPoint.x, rightTopPoint.y
								- mToolStrokeWidth / 2, rightTopPoint.x,
						rightTopPoint.y + resizeLineLengthHeight, mLinePaint);

				canvas.drawLine(rightTopPoint.x + mBoxWidth / 2
								- resizeLineLengthWidth, rightTopPoint.y, rightTopPoint.x
								+ mBoxWidth / 2 + resizeLineLengthWidth, rightTopPoint.y,
						mLinePaint);
				canvas.rotate(90);
				float tempX = rightTopPoint.x;
				rightTopPoint.x = rightTopPoint.y;
				rightTopPoint.y = tempX;
				float tempHeight = mBoxHeight;
				mBoxHeight = mBoxWidth;
				mBoxWidth = tempHeight;
			}
			mBoxWidth = tempWidth;
		}
	}

	private void resetScaleAndTranslation() {
		PaintroidApplication.perspective.resetScaleAndTranslation();
		float zoomFactor = PaintroidApplication.perspective
				.getScaleForCenterBitmap() * START_ZOOM_FACTOR;
		PaintroidApplication.perspective.setScale(zoomFactor);
	}

	private void initialiseResizingState() {
		mCropRunFinished = false;
		mResizeBoundWidthXRight = 0;
		mResizeBoundHeightYBottom = 0;
		mResizeBoundWidthXLeft = PaintroidApplication.drawingSurface
				.getBitmapWidth();
		mResizeBoundHeightYTop = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		resetScaleAndTranslation();
	}

	protected void displayToastInformation(int stringID) {
		Toast.makeText(mContext, stringID, Toast.LENGTH_SHORT).show();
	}

	protected void executeResizeCommand() {
		if (mCropRunFinished == true) {
			mCropRunFinished = false;
			initResizeBounds();
			if (areResizeBordersValid()) {

				for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
					Command resizeCommand = new ResizeCommand((int) Math.floor(mResizeBoundWidthXLeft),
							(int) Math.floor(mResizeBoundHeightYTop),
							(int) Math.floor(mResizeBoundWidthXRight),
							(int) Math.floor(mResizeBoundHeightYBottom),
							(int) mMaximumBoxResolution);

					if (layer.getSelected()) {
						((ResizeCommand) resizeCommand).addObserver(this);
					}
					PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), resizeCommand);
				}

			} else {
				mCropRunFinished = true;
				displayToastInformation(R.string.resize_nothing_to_resize);
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
					mBoxWidth = shapeBounds.width() + 1;
					mBoxHeight = shapeBounds.height() + 1;
					mToolPosition.x = shapeBounds.left + (shapeBounds.width() + 1) / 2.0f;
					mToolPosition.y = shapeBounds.top + (shapeBounds.height() + 1) / 2.0f;
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

	static boolean containsNotTransparentPixel(int pixels[][], int fromX, int fromY, int toX, int toY) {
		for (int y = fromY; y <= toY; y++) {
			for (int x = fromX; x <= toX; x++) {
				if (pixels[y][x] != Color.TRANSPARENT) {
					return true;
				}
			}
		}
		return false;
	}

	public static Rect cropAlgorithmSnail(Bitmap bitmap) {
		if (bitmap == null) {
			Log.e("cropAlgorithmSnail", "bitmap is null!");
			return null;
		}

		int pixels[][] = new int[bitmap.getHeight()][bitmap.getWidth()];
		for (int i = 0; i < bitmap.getHeight(); i++) {
			bitmap.getPixels(pixels[i], 0, bitmap.getWidth(), 0, i, bitmap.getWidth(), 1);
		}

		Rect bounds = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
		int x, y;
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

	private boolean areResizeBordersValid() {
		if (mResizeBoundWidthXRight < mResizeBoundWidthXLeft
				|| mResizeBoundHeightYTop > mResizeBoundHeightYBottom) {
			return false;
		}
		if (mResizeBoundWidthXLeft >= PaintroidApplication.drawingSurface.getBitmapWidth() ||
				mResizeBoundWidthXRight < 0 || mResizeBoundHeightYBottom < 0 ||
				mResizeBoundHeightYTop >= PaintroidApplication.drawingSurface.getBitmapHeight()) {
			return false;
		}
		if (mResizeBoundWidthXLeft == 0 && mResizeBoundHeightYTop == 0 &&
				mResizeBoundWidthXRight == PaintroidApplication.drawingSurface.getBitmapWidth() - 1 &&
				mResizeBoundHeightYBottom == PaintroidApplication.drawingSurface.getBitmapHeight() - 1) {
			return false;
		}
		if ((mResizeBoundWidthXRight + 1 - mResizeBoundWidthXLeft)
				* (mResizeBoundHeightYBottom + 1 - mResizeBoundHeightYTop) > mMaximumBoxResolution) {
			return false;
		}

		return true;
	}

	@Override
	public void update(Observable observable, Object data) {
		super.update(observable, data);
		if (data instanceof BaseCommand.NOTIFY_STATES) {
			if (BaseCommand.NOTIFY_STATES.COMMAND_DONE == data
					|| BaseCommand.NOTIFY_STATES.COMMAND_FAILED == data) {
				initialiseResizingState();
				mResizeBoundWidthXRight = Float
						.valueOf(PaintroidApplication.drawingSurface
								.getBitmapWidth() - 1);
				mResizeBoundHeightYBottom = Float
						.valueOf(PaintroidApplication.drawingSurface
								.getBitmapHeight() - 1);
				mResizeBoundWidthXLeft = 0f;
				mResizeBoundHeightYTop = 0f;
				setRectangle(new RectF(mResizeBoundWidthXLeft,
						mResizeBoundHeightYTop, mResizeBoundWidthXRight,
						mResizeBoundHeightYBottom));
				mCropRunFinished = true;
			}
		}
	}

	private void setRectangle(RectF rectangle) {
		mBoxWidth = rectangle.right - rectangle.left + 1f;
		mBoxHeight = rectangle.bottom - rectangle.top + 1f;
		mToolPosition.x = rectangle.left + mBoxWidth / 2f;
		mToolPosition.y = rectangle.top + mBoxHeight / 2f;
	}

	private void initResizeBounds() {
		mResizeBoundWidthXLeft = mToolPosition.x - mBoxWidth / 2f;
		mResizeBoundWidthXRight = mToolPosition.x + mBoxWidth / 2f - 1f;
		mResizeBoundHeightYTop = mToolPosition.y - mBoxHeight / 2f;
		mResizeBoundHeightYBottom = mToolPosition.y + mBoxHeight / 2f - 1f;
	}

	@Override
	protected void onClickInBox() {
		executeResizeCommand();
	}

	@Override
	protected void preventThatBoxGetsTooLarge(float oldWidth, float oldHeight,
											  float oldPosX, float oldPosY) {
		super.preventThatBoxGetsTooLarge(oldWidth, oldHeight, oldPosX, oldPosY);
		if (!mMaxImageResolutionInformationAlreadyShown) {
			displayToastInformation(R.string.resize_max_image_resolution_reached);
			mMaxImageResolutionInformationAlreadyShown = true;
		}
	}

	@Override
	public void setupToolOptions() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTransformToolOptionView = inflater.inflate(R.layout.dialog_transform_tool, null);
		mToolSpecificOptionsLayout.addView(mTransformToolOptionView);

		View.OnTouchListener transformOptionsListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.bottom_bar_button_activated));
						return true;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
						switch (v.getId()) {
							case R.id.transform_auto_crop_btn:
								autoCrop();
								return true;
							case R.id.transform_rotate_left_btn:
								rotate(RotateCommand.RotateDirection.ROTATE_LEFT);
								return true;
							case R.id.transform_rotate_right_btn:
								rotate(RotateCommand.RotateDirection.ROTATE_RIGHT);
								return true;
							case R.id.transform_flip_horizontal_btn:
								flip(FlipCommand.FlipDirection.FLIP_HORIZONTAL);
								return true;
							case R.id.transform_flip_vertical_btn:
								flip(FlipCommand.FlipDirection.FLIP_VERTICAL);
								return true;
							default:
								return false;
						}
					default:
						return false;
				}
			}
		};

		int[] buttonIdList = {
				R.id.transform_auto_crop_btn,
				R.id.transform_rotate_left_btn, R.id.transform_rotate_right_btn,
				R.id.transform_flip_horizontal_btn, R.id.transform_flip_vertical_btn};

		for (int id : buttonIdList) {
			mTransformToolOptionView.findViewById(id).setOnTouchListener(transformOptionsListener);
		}

		mToolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

	@Override
	public void toggleShowToolOptions() {
		super.toggleShowToolOptions();
		if (!mToolOptionsShown) {
			Toast.makeText(mContext, R.string.transform_info_text, Toast.LENGTH_LONG).show();
		}
	}

	public static void undoResizeCommand(Layer undoLayer, ResizeCommand undoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == undoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> layerCommands = layerBitmapCommand.getLayerCommands();

			if (!layerCommands.isEmpty()) {
				int indexOfLastElement = layerCommands.size() - 1;
				Command lastCommand = layerCommands.get(indexOfLastElement);
				if (lastCommand instanceof ResizeCommand) {
					layerBitmapCommand.addCommandToUndoList();
					LayerListener.getInstance().selectLayer(layer);
					layerBitmapCommand.clearLayerBitmap();
					layerBitmapCommand.runAllCommands();
					continue;
				}
			}

			int undoWidth = undoLayer.getImage().getWidth();
			int undoHeight = undoLayer.getImage().getHeight();
			int currentWidth = layer.getImage().getWidth();
			int currentHeight = layer.getImage().getHeight();

			Command resizeCommand = new ResizeCommand(
					-undoCommand.getResizeCoordinateXLeft(),
					-undoCommand.getResizeCoordinateYTop(),
					currentWidth - (undoCommand.getResizeCoordinateXRight() - undoWidth),
					currentHeight - (undoCommand.getResizeCoordinateYBottom() - undoHeight),
					undoCommand.getMaximumBitmapResolution());

			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), resizeCommand);
		}

		if (!undoLayer.getSelected()) {
			LayerListener.getInstance().selectLayer(undoLayer);
		}
	}

	public static void redoResizeCommand(Layer redoLayer, ResizeCommand redoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == redoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> undoCommands = layerBitmapCommand.getLayerUndoCommands();

			if (!undoCommands.isEmpty()) {
				Command firstCommand = undoCommands.get(0);
				if (firstCommand instanceof ResizeCommand) {
					firstCommand.run(PaintroidApplication.drawingSurface.getCanvas(), layer);
					layerBitmapCommand.addCommandToRedoList();
					continue;
				}
			}

			Command resizeCommand = new ResizeCommand(
					redoCommand.getResizeCoordinateXLeft(),
					redoCommand.getResizeCoordinateYTop(),
					redoCommand.getResizeCoordinateXRight(),
					redoCommand.getResizeCoordinateYBottom(),
					redoCommand.getMaximumBitmapResolution());

			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), resizeCommand);
		}
	}

	public static void undoRotateCommand(Layer undoLayer, RotateCommand undoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == undoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> layerCommands = layerBitmapCommand.getLayerCommands();

			if (!layerCommands.isEmpty()) {
				int indexOfLastElement = layerCommands.size() - 1;
				Command lastCommand = layerCommands.get(indexOfLastElement);
				if (lastCommand instanceof RotateCommand) {
					layerBitmapCommand.addCommandToUndoList();
					LayerListener.getInstance().selectLayer(layer);
					layerBitmapCommand.clearLayerBitmap();
					layerBitmapCommand.runAllCommands();
					continue;
				}
			}

			RotateCommand.RotateDirection rotateDirection = null;
			switch (undoCommand.getRotateDirection()) {
				case ROTATE_LEFT:
					rotateDirection = RotateCommand.RotateDirection.ROTATE_RIGHT;
					break;
				case ROTATE_RIGHT:
					rotateDirection = RotateCommand.RotateDirection.ROTATE_LEFT;
			}

			Command rotateCommand = new RotateCommand(rotateDirection);
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), rotateCommand);
		}

		if (!undoLayer.getSelected()) {
			LayerListener.getInstance().selectLayer(undoLayer);
		}
	}

	public static void redoRotateCommand(Layer redoLayer, RotateCommand redoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == redoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> undoCommands = layerBitmapCommand.getLayerUndoCommands();

			if (!undoCommands.isEmpty()) {
				Command firstCommand = undoCommands.get(0);
				if (firstCommand instanceof RotateCommand) {
					firstCommand.run(PaintroidApplication.drawingSurface.getCanvas(), layer);
					layerBitmapCommand.addCommandToRedoList();
					continue;
				}
			}

			Command rotateCommand = new RotateCommand(redoCommand.getRotateDirection());
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), rotateCommand);
		}
	}

}
