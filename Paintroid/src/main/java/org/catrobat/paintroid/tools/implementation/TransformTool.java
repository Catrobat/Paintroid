/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
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
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.listener.TransformToolOptionsListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.ToolType;

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
	private int mIntermediateResizeBoundWidthXLeft;
	private int mIntermediateResizeBoundWidthXRight;
	private int mIntermediateResizeBoundHeightYTop;
	private int mIntermediateResizeBoundHeightYBottom;
	private boolean mBitmapIsEmpty;

	private boolean mCropRunFinished = false;
	private boolean mResizeInformationAlreadyShown = false;
	private boolean mMaxImageResolutionInformationAlreadyShown = false;

	private View mTransformToolOptionView;

	public TransformTool(Context context, ToolType toolType) {
		super(context, toolType);

		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTransformToolOptionView = inflater.inflate(R.layout.dialog_transform_tool, null);
		mToolSpecificOptionsLayout.addView(mTransformToolOptionView);

		TransformToolOptionsListener.init(mContext, mTransformToolOptionView);

		setRotationEnabled(ROTATION_ENABLED);
		setRespectImageBounds(RESPECT_IMAGE_BORDERS);
		setResizePointsVisible(RESIZE_POINTS_VISIBLE);
		setRespectMaximumBorderRatio(RESPECT_MAXIMUM_BORDER_RATIO);

		if(!PaintroidApplication.drawingSurface.isBitmapNull()) {
			mBoxHeight = (PaintroidApplication.drawingSurface.getBitmapHeight() /100)
					* TransformToolOptionsListener.getInstance().getSeekBarSize();
			mBoxWidth = (PaintroidApplication.drawingSurface.getBitmapWidth() / 100)
					* TransformToolOptionsListener.getInstance().getSeekBarSize();
		}
		mToolPosition.x = mBoxWidth / 2f;
		mToolPosition.y = mBoxHeight / 2f;

		resetScaleAndTranslation();

		mCropRunFinished = true;

		DisplayResizeInformationAsyncTask displayResizeInformation = new DisplayResizeInformationAsyncTask();
		displayResizeInformation.execute();

		Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
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
		mIntermediateResizeBoundWidthXLeft = 0;
		mIntermediateResizeBoundWidthXRight = PaintroidApplication.drawingSurface
				.getBitmapWidth();
		mIntermediateResizeBoundHeightYTop = 0;
		mIntermediateResizeBoundHeightYBottom = PaintroidApplication.drawingSurface
				.getBitmapHeight();
		resetScaleAndTranslation();
	}

	protected void displayToastInformation(int stringID) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.image_toast_layout, (ViewGroup) ((Activity) mContext)
						.findViewById(R.id.image_toast_layout_root));

		if (stringID != R.string.resize_to_resize_tap_text) {
			ImageView toastImage = (ImageView) layout.findViewById(R.id.toast_image);
			toastImage.setVisibility(View.GONE);

			TextView text = (TextView) layout.findViewById(R.id.toast_text);
			text.setText(mContext.getText(stringID));
		}

		Toast toast = new Toast(mContext);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	protected void executeResizeCommand() {
		if (mCropRunFinished == true) {
			mCropRunFinished = false;
			initResizeBounds();
			if (areResizeBordersValid()) {
				IndeterminateProgressDialog.getInstance().show();
				Command command = new ResizeCommand((int) Math.floor(mResizeBoundWidthXLeft),
						(int) Math.floor(mResizeBoundHeightYTop),
						(int) Math.floor(mResizeBoundWidthXRight),
						(int) Math.floor(mResizeBoundHeightYBottom),
						(int) mMaximumBoxResolution);

				((ResizeCommand) command).addObserver(this);
				Layer layer = LayerListener.getInstance().getCurrentLayer();
				PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
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
		Command command = new RotateCommand(rotateDirection);
		IndeterminateProgressDialog.getInstance().show();
		((RotateCommand) command).addObserver(this);
		Layer layer = LayerListener.getInstance().getCurrentLayer();
		PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), command);
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

	protected class DisplayResizeInformationAsyncTask extends
			AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		@Override
		protected void onPostExecute(Void nothing) {
			if (!mResizeInformationAlreadyShown) {
				displayToastInformation(R.string.resize_to_resize_tap_text);
				mResizeInformationAlreadyShown = true;
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
	public void setupToolOptions(){

		TransformToolOptionsListener.getInstance().getAngleSeekBar()
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				TransformToolOptionsListener.getInstance().setAngleText();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				rotate(RotateCommand.RotateDirection.ROTATE_RIGHT);
			}

		});

		TransformToolOptionsListener.getInstance().getSizeSeekBar()
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				TransformToolOptionsListener.getInstance().setSizeText();
				mBoxHeight = (PaintroidApplication.drawingSurface.getBitmapHeight() /100)
						* TransformToolOptionsListener.getInstance().getSeekBarSize();
				mBoxWidth = (PaintroidApplication.drawingSurface.getBitmapWidth() / 100)
						* TransformToolOptionsListener.getInstance().getSeekBarSize();

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		TransformToolOptionsListener.getInstance().getFlipVerticalButton()
				.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(mContext.getResources()
								.getColor(R.color.bottom_bar_button_activated));
						break;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(mContext.getResources()
								.getColor(R.color.transparent));
						flip(FlipCommand.FlipDirection.FLIP_VERTICAL);
						break;
					default:
						return false;
				}
				return true;
			}
		});


		TransformToolOptionsListener.getInstance().getFlipHorizontalButton()
				.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						v.setBackgroundColor(mContext.getResources()
								.getColor(R.color.bottom_bar_button_activated));
						break;
					case MotionEvent.ACTION_UP:
						v.setBackgroundColor(mContext.getResources()
								.getColor(R.color.transparent));
						flip(FlipCommand.FlipDirection.FLIP_HORIZONTAL);
						break;
					default:
						return false;
				}
				return true;
			}
		});



		mToolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				toggleShowToolOptions();
			}
		});
	}

}
