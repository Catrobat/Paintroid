package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.TransformToolOptionsListener;
import org.catrobat.paintroid.listener.LayerListener;

public class RotateCommand extends BaseCommand {

	private final static float ANGLE = 90;
	private RotateDirection mRotateDirection;

	public static enum RotateDirection {
		ROTATE_LEFT, ROTATE_RIGHT
	}

	public RotateCommand(RotateDirection rotateDirection) {
		mRotateDirection = rotateDirection;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mRotateDirection == null) {
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		Matrix rotateMatrix = new Matrix();

		switch (mRotateDirection) {
			case ROTATE_RIGHT:
				rotateMatrix.postRotate(TransformToolOptionsListener.getInstance().getSeekBarAngle());
				Log.i(PaintroidApplication.TAG, "rotate right");
				break;

			case ROTATE_LEFT:
				rotateMatrix.postRotate(-90);
				Log.i(PaintroidApplication.TAG, "rotate left");
				break;

			default:
				setChanged();
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
		}

		rotateMatrix.postTranslate(-bitmap.getWidth()/2, -bitmap.getHeight()/2);

		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
		Canvas rotateCanvas = new Canvas(rotatedBitmap);

		rotateCanvas.drawBitmap(bitmap, rotateMatrix, new Paint());

		PaintroidApplication.drawingSurface.recycleBitmap();
		if (PaintroidApplication.drawingSurface != null) {
			PaintroidApplication.drawingSurface.setBitmap(rotatedBitmap);
		}
		LayerListener.getInstance().getCurrentLayer().setImage(rotatedBitmap);
		LayerListener.getInstance().refreshView();

		setChanged();

		PaintroidApplication.perspective.resetScaleAndTranslation();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);

	}
}