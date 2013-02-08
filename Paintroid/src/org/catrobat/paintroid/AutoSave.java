package org.catrobat.paintroid;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;

public class AutoSave {

	// autosave
	private static int mAutoSaveCounter;
	private static String mPicturePath = null;
	private static String mAutoSaveDirectory = FileIO
			.createNewEmptyPictureFile(null, "autosave").getAbsolutePath()
			+ File.pathSeparator;
	private static String mDefaultAutoSaveName = mAutoSaveDirectory
			+ "autosave.png";
	private static Bitmap bitmap = null;

	public static void clear() {
		File file = new File("");
		clear(file);
	}

	public static void clear(File currentFile) {
		File file = new File("autoSaveDirectory");
		for (File f : file.listFiles()) {
			if (!f.equals(currentFile)) {
				f.delete();
			}
		}
	}

	public static void autoSaveImageExists(String catroidPicturePath,
			Activity activity) {

		File file = null;
		if (null == catroidPicturePath) {
			file = new File(mDefaultAutoSaveName);
		} else {
			mPicturePath = catroidPicturePath;
			String checksum = Utils.md5Checksum(mPicturePath);
			file = new File(checksum + ".png");
			bitmap = Utils.getBitmapFromFile(file);
		}

		if (file.exists()) {
			takeAutoSaveImage(activity);
		}

	}

	public static void trigger() {

		String checksum = "";
		mAutoSaveCounter++;

		if (mAutoSaveCounter % 10 == 0) {
			if (null == mPicturePath) {

			} else {
				checksum = Utils.md5Checksum(mPicturePath);
			}

			FileIO.saveBitmap(PaintroidApplication.APPLICATION_CONTEXT,
					PaintroidApplication.DRAWING_SURFACE.getBitmap(),
					"autosave");
			// TODO: AUTOSAVE Commands

		}
	}

	public static void incrementCounter() {
		mAutoSaveCounter--;
	}

	public static void takeAutoSaveImage(Activity activity) {

		AlertDialog.Builder newAutoSaveAlertDialogBuilder = new AlertDialog.Builder(
				activity);
		newAutoSaveAlertDialogBuilder
				.setMessage(R.string.dialog_autosave_image)
				.setCancelable(true)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								setDrawingSurface();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		newAutoSaveAlertDialogBuilder.create().show();
	}

	public static void setDrawingSurface() {
		PaintroidApplication.DRAWING_SURFACE.setBitmap(bitmap);
	}
}
