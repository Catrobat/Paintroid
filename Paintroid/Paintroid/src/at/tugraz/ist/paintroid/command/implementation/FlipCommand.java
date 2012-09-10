package at.tugraz.ist.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;

public class FlipCommand extends BaseCommand {

	private FlipDirection mFlipDirection;

	public static enum FlipDirection {
		FLIP_HORIZONTAL, FLIP_VERTICAL
	};

	public FlipCommand(FlipDirection flipDirection) {
		mFlipDirection = flipDirection;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mFlipDirection == null) {
			setChanged();
			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
			return;
		}

		Matrix flipMatrix = new Matrix();

		switch (mFlipDirection) {
			case FLIP_HORIZONTAL:
				flipMatrix.setScale(1, -1);
				flipMatrix.postTranslate(0, bitmap.getHeight());
				Log.i(PaintroidApplication.TAG, "flip horizontal");
				break;
			case FLIP_VERTICAL:
				flipMatrix.setScale(-1, 1);
				flipMatrix.postTranslate(bitmap.getWidth(), 0);
				Log.i(PaintroidApplication.TAG, "flip vertical");
				break;
			default:
				setChanged();
				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
		}

		Bitmap flipBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		Canvas flipCanvas = new Canvas(flipBitmap);

		flipCanvas.drawBitmap(bitmap, flipMatrix, new Paint());
		if (PaintroidApplication.DRAWING_SURFACE != null) {
			PaintroidApplication.DRAWING_SURFACE.setBitmap(flipBitmap);
		}

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
