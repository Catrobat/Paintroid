package at.tugraz.ist.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

public class ClearCommand extends BaseCommand {
	protected int mColor;

	public ClearCommand() {
		mColor = Color.TRANSPARENT;
	}

	public ClearCommand(int color) {
		mColor = color;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		bitmap.eraseColor(mColor);
	}
}
