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

package org.catrobat.paintroid;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class FileIO {
	private static final int BUFFER_SIZE = 1024;
	private static final String DEFAULT_FILENAME_TIME_FORMAT = "yyyy_MM_dd_hhmmss";
	private static final String ENDING = ".png";
	private static File paintroidMediaFile = null;

	private FileIO() {
	}

	private static Uri getBaseUri() {
		return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}

	static boolean saveBitmap(Context context, Bitmap bitmap, @Nullable String path, boolean saveCopy) {
		if (!initialisePaintroidMediaDirectory()) {
			return false;
		}

		final int quality = 100;
		final Bitmap.CompressFormat format = Bitmap.CompressFormat.PNG;
		OutputStream outputStream = null;
		File file = null;

		try {
			if (bitmap == null || bitmap.isRecycled()) {
				Log.e(PaintroidApplication.TAG, "ERROR saving bitmap. ");
				return false;
			} else if (path != null) {
				file = new File(path);
				outputStream = new FileOutputStream(file);
			} else if (PaintroidApplication.savedPictureUri != null && !saveCopy) {
				outputStream = context.getContentResolver().openOutputStream(
						PaintroidApplication.savedPictureUri);
			} else {
				file = createNewEmptyPictureFile();
				outputStream = new FileOutputStream(file);
			}
		} catch (FileNotFoundException e) {
			Log.e(PaintroidApplication.TAG,
					"ERROR writing image file. File not found. Path: " + path,
					e);
			return false;
		}

		if (outputStream != null) {
			boolean isSaved = bitmap.compress(format, quality, outputStream);
			try {
				outputStream.close();
			} catch (IOException e) {
				Log.e(PaintroidApplication.TAG, e.getMessage());
			}
			if (isSaved) {
				if (file != null) {
					ContentValues contentValues = new ContentValues();
					contentValues.put(MediaStore.MediaColumns.DATA,
							file.getAbsolutePath());

					PaintroidApplication.savedPictureUri = context
							.getContentResolver().insert(getBaseUri(),
									contentValues);
				}
			} else {
				Log.e(PaintroidApplication.TAG,
						"ERROR writing image file. Bitmap compress didn't work. ");
				return false;
			}
		}
		return true;
	}

	static String getDefaultFileName() {
		SimpleDateFormat simpleDateFormat =
				new SimpleDateFormat(DEFAULT_FILENAME_TIME_FORMAT, Locale.US);
		return simpleDateFormat.format(new Date()) + ENDING;
	}

	static File createNewEmptyPictureFile(String filename) {
		if (initialisePaintroidMediaDirectory()) {
			if (!filename.toLowerCase(Locale.US).endsWith(ENDING.toLowerCase(Locale.US))) {
				filename += ENDING;
			}
			return new File(paintroidMediaFile, filename);
		} else {
			return null;
		}
	}

	private static File createNewEmptyPictureFile() {
		return createNewEmptyPictureFile(getDefaultFileName());
	}

	private static boolean initialisePaintroidMediaDirectory() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			paintroidMediaFile = new File(
					Environment.getExternalStorageDirectory(),
					"/"
							+ PaintroidApplication.applicationContext
							.getString(R.string.ext_storage_directory_name) + "/");
		} else {
			return false;
		}
		if (paintroidMediaFile != null) {
			if (!paintroidMediaFile.isDirectory()) {
				return paintroidMediaFile.mkdirs();
			}
		} else {
			return false;
		}
		return true;
	}

	public static Bitmap getBitmapFromUri(Uri bitmapUri) {
		BitmapFactory.Options options = new BitmapFactory.Options();

//		TODO: special treatment necessary?
//		if (PaintroidApplication.openedFromCatroid) {
//			try {
//				InputStream inputStream = PaintroidApplication.applicationContext
//						.getContentResolver().openInputStream(bitmapUri);
//				Bitmap immutableBitmap = BitmapFactory
//						.decodeStream(inputStream);
//				inputStream.close();
//				return immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}

		options.inJustDecodeBounds = true;

		try {
			InputStream inputStream = PaintroidApplication.applicationContext
					.getContentResolver().openInputStream(bitmapUri);
			BitmapFactory.decodeStream(inputStream, null, options);
			inputStream.close();
		} catch (Exception e) {
			return null;
		}

		int tmpWidth = options.outWidth;
		int tmpHeight = options.outHeight;
		int sampleSize = 1;

		if (PaintroidApplication.scaleImage) {
			DisplayMetrics metrics = new DisplayMetrics();
			Display display = ((WindowManager) PaintroidApplication.applicationContext
					.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			display.getMetrics(metrics);
			int maxWidth = display.getWidth();
			int maxHeight = display.getHeight();

			while (tmpWidth > maxWidth || tmpHeight > maxHeight) {
				tmpWidth /= 2;
				tmpHeight /= 2;
				sampleSize *= 2;
			}
		}
		PaintroidApplication.scaleImage = true;

		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;

		Bitmap immutableBitmap;
		try {
			InputStream inputStream = PaintroidApplication.applicationContext
					.getContentResolver().openInputStream(bitmapUri);
			immutableBitmap = BitmapFactory.decodeStream(inputStream, null,
					options);
			inputStream.close();
		} catch (Exception e) {
			return null;
		}

		tmpWidth = immutableBitmap.getWidth();
		tmpHeight = immutableBitmap.getHeight();
		int[] tmpPixels = new int[tmpWidth * tmpHeight];
		immutableBitmap.getPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		Bitmap mutableBitmap = Bitmap.createBitmap(tmpWidth, tmpHeight,
				Bitmap.Config.ARGB_8888);
		mutableBitmap.setPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		return mutableBitmap;
	}

	public static Bitmap getBitmapFromFile(File bitmapFile, boolean openedFromCatroid) {

		BitmapFactory.Options options = new BitmapFactory.Options();

		if (openedFromCatroid) {
			options.inJustDecodeBounds = false;
			Bitmap immutableBitmap = BitmapFactory.decodeFile(
					bitmapFile.getAbsolutePath(), options);
			return immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
		}

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);

		int tmpWidth = options.outWidth;
		int tmpHeight = options.outHeight;
		int sampleSize = 1;

		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((WindowManager) PaintroidApplication.applicationContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(metrics);
		int maxWidth = display.getWidth();
		int maxHeight = display.getHeight();

		while (tmpWidth > maxWidth || tmpHeight > maxHeight) {
			tmpWidth /= 2;
			tmpHeight /= 2;
			sampleSize *= 2;
		}

		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;

		Bitmap immutableBitmap = BitmapFactory.decodeFile(
				bitmapFile.getAbsolutePath(), options);

		tmpWidth = immutableBitmap.getWidth();
		tmpHeight = immutableBitmap.getHeight();
		int[] tmpPixels = new int[tmpWidth * tmpHeight];
		immutableBitmap.getPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		Bitmap mutableBitmap = Bitmap.createBitmap(tmpWidth, tmpHeight,
				Bitmap.Config.ARGB_8888);
		mutableBitmap.setPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		return mutableBitmap;
	}

	public static String createFilePathFromUri(Activity activity, Uri uri) {
		// Problem here
		String filepath = null;
		String[] projection = {MediaStore.Images.Media.DATA};
		Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			filepath = cursor.getString(columnIndex);
		}

		if (filepath == null
				&& Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			String id = uri.getLastPathSegment().split(":")[1];
			final String[] imageColumns = {MediaStore.Images.Media.DATA};
			final String imageOrderBy = null;

			String state = Environment.getExternalStorageState();
			if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
			}
			uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

			cursor = activity.managedQuery(uri, imageColumns,
					MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

			if (cursor.moveToFirst()) {
				filepath = cursor.getString(cursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
			}
		} else if (filepath == null) {
			filepath = uri.getPath();
		}
		return filepath;
	}

	public static void copyStream(InputStream inputStream,
			OutputStream outputStream) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead;
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}
	}
}
