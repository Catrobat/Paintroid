package org.catrobat.paintroid;

import android.graphics.Bitmap;

public class FileIODataTransfer {
	private Bitmap bitmap;
	private Boolean toBeScaled;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public Boolean getToBeScaled() {
		return toBeScaled;
	}

	public FileIODataTransfer(Bitmap bitmap, Boolean scaling) {
		this.bitmap = bitmap;
		toBeScaled = scaling;
	}
}
