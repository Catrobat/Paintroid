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

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import org.catrobat.paintroid.common.Constants;

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
	private static final String DEFAULT_FILENAME_TIME_FORMAT = "yyyy_MM_dd_hhmmss";
	private static final String ENDING = ".png";
	private static final String TAG = FileIO.class.getSimpleName();
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
				Log.e(TAG, "ERROR saving bitmap. ");
				return false;
			} else if (path != null) {
				file = new File(path);
				outputStream = new FileOutputStream(file);
			} else if (NavigationDrawerMenuActivity.savedPictureUri != null && !saveCopy) {
				outputStream = context.getContentResolver().openOutputStream(
						NavigationDrawerMenuActivity.savedPictureUri);
			} else {
				file = createNewEmptyPictureFile();
				outputStream = new FileOutputStream(file);
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "ERROR writing image file. File not found. Path: " + path, e);
			return false;
		}

		if (outputStream != null) {
			boolean isSaved = bitmap.compress(format, quality, outputStream);
			try {
				outputStream.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
			if (isSaved) {
				if (file != null) {
					ContentValues contentValues = new ContentValues();
					contentValues.put(MediaStore.MediaColumns.DATA,
							file.getAbsolutePath());

					NavigationDrawerMenuActivity.savedPictureUri = context
							.getContentResolver().insert(getBaseUri(),
									contentValues);
				}
			} else {
				Log.e(TAG, "ERROR writing image file. Bitmap compress didn't work. ");
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
					Environment.getExternalStorageDirectory(), File.separatorChar
					+ Constants.EXT_STORAGE_DIRECTORY_NAME + File.separatorChar);
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

	private static void setInvalidOptions(BitmapFactory.Options options) {
		options.outWidth = -1;
		options.outHeight = -1;
	}

	private static Bitmap decodeBitmapFromUri(Context context, Uri bitmapUri, BitmapFactory.Options options) {
		Bitmap bitmap;
		try {
			InputStream inputStream = context.getContentResolver().openInputStream(bitmapUri);
			if (inputStream == null) {
				setInvalidOptions(options);
				return null;
			}
			bitmap = BitmapFactory.decodeStream(inputStream, null, options);
			inputStream.close();
		} catch (Exception e) {
			setInvalidOptions(options);
			return null;
		}
		return bitmap;
	}

	private static int calculateSampleSize(int width, int height, int maxWidth, int maxHeight) {
		int sampleSize = 1;
		while (width > maxWidth || height > maxHeight) {
			width /= 2;
			height /= 2;
			sampleSize *= 2;
		}
		return sampleSize;
	}

	static Bitmap getBitmapFromUri(Context context, Uri bitmapUri, boolean scaleImage) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		if (!scaleImage) {
			options.inMutable = true;
			return decodeBitmapFromUri(context, bitmapUri, options);
		}

		options.inJustDecodeBounds = true;
		decodeBitmapFromUri(context, bitmapUri, options);
		if (options.outHeight < 0 || options.outWidth < 0) {
			return null;
		}

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int sampleSize = calculateSampleSize(options.outWidth, options.outHeight,
				metrics.widthPixels, metrics.heightPixels);

		options.inMutable = true;
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;

		return enableAlpha(decodeBitmapFromUri(context, bitmapUri, options));
	}

	public static Bitmap getBitmapFromFile(File bitmapFile, int maxWidth, int maxHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);

		int tmpWidth = options.outWidth;
		int tmpHeight = options.outHeight;
		int sampleSize = 1;

		while (tmpWidth > maxWidth || tmpHeight > maxHeight) {
			tmpWidth /= 2;
			tmpHeight /= 2;
			sampleSize *= 2;
		}

		options.inMutable = true;
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;

		return enableAlpha(BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options));
	}

	private static Bitmap enableAlpha(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		bitmap.setHasAlpha(true);
		return bitmap;
	}
}
