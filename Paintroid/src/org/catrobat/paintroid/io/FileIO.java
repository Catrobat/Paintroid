package org.catrobat.paintroid.io;

import java.io.File;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.os.Environment;

public class FileIO {

	public static void savePaintroidImage(PaintroidImage paintroidImage) {

	}

	public static File createPaintroidMediaPath() {

		// if
		// (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))

		String path = Environment.getExternalStorageDirectory()
				+ File.separator
				+ PaintroidApplication.applicationContext
						.getString(R.string.app_name) + File.separator;
		return null;
	}
}
