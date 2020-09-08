/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import org.catrobat.paintroid.common.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

public final class FileIO {
	private static final String DEFAULT_FILENAME_TIME_FORMAT = "yyyy_MM_dd_hhmmss";
	private static final String ENDING = ".png";
	private static final int COMPRESS_QUALITY = 100;
	private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;

	private FileIO() {
		throw new AssertionError();
	}

	private static void saveBitmapToStream(OutputStream outputStream, Bitmap bitmap) throws IOException {
		if (bitmap == null || bitmap.isRecycled()) {
			throw new IllegalArgumentException("Bitmap is invalid");
		}
		if (!bitmap.compress(COMPRESS_FORMAT, COMPRESS_QUALITY, outputStream)) {
			throw new IOException("Can not write png to stream.");
		}
	}

	public static Uri saveBitmapToUri(Uri uri, ContentResolver resolver, Bitmap bitmap) throws IOException {
		OutputStream outputStream = resolver.openOutputStream(uri);
		if (outputStream == null) {
			throw new IllegalArgumentException("Can not open uri.");
		}
		try {
			saveBitmapToStream(outputStream, bitmap);
		} finally {
			outputStream.close();
		}
		return uri;
	}

	public static Uri saveBitmapToFile(String fileName, Bitmap bitmap) throws IOException {
		if (!(Constants.MEDIA_DIRECTORY.exists() || Constants.MEDIA_DIRECTORY.mkdirs())) {
			throw new IOException("Can not create media directory.");
		}

		File file = new File(Constants.MEDIA_DIRECTORY, fileName);
		OutputStream outputStream = new FileOutputStream(file);
		try {
			saveBitmapToStream(outputStream, bitmap);
		} finally {
			outputStream.close();
		}
		return Uri.fromFile(file);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	public static Uri saveBitmapToCache(Bitmap bitmap, MainActivity mainActivity) {
		Uri uri = null;
		try {
			File cachePath = new File(mainActivity.getCacheDir(), "images");
			cachePath.mkdirs();
			FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			stream.close();
			File imagePath = new File(mainActivity.getCacheDir(), "images");
			File newFile = new File(imagePath, "image.png");
			uri = FileProvider.getUriForFile(mainActivity, "$(applicationId).org.catrobat.paintroid", newFile);
		} catch (IOException e) {
			Log.e("Can not write", "Can not write png to stream.", e);
		}
		return uri;
	}

	public static String getDefaultFileName() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DEFAULT_FILENAME_TIME_FORMAT, Locale.US);
		return simpleDateFormat.format(new Date()) + ENDING;
	}

	public static File createNewEmptyPictureFile(String filename) throws IOException {
		if (filename == null) {
			filename = getDefaultFileName();
		}
		if (!Constants.MEDIA_DIRECTORY.exists() && !Constants.MEDIA_DIRECTORY.mkdirs()) {
			throw new IOException("Can not create media directory.");
		}
		if (!filename.toLowerCase(Locale.US).endsWith(ENDING.toLowerCase(Locale.US))) {
			filename += ENDING;
		}
		return new File(Constants.MEDIA_DIRECTORY, filename);
	}

	private static Bitmap decodeBitmapFromUri(ContentResolver resolver, @NonNull Uri uri, BitmapFactory.Options options) throws IOException {
		InputStream inputStream = resolver.openInputStream(uri);
		if (inputStream == null) {
			throw new IOException("Can't open input stream");
		}
		try {
			return BitmapFactory.decodeStream(inputStream, null, options);
		} finally {
			inputStream.close();
		}
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

	public static Bitmap getBitmapFromUri(ContentResolver resolver, @NonNull Uri bitmapUri) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		return enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options));
	}

	public static Bitmap getBitmapFromUri(ContentResolver resolver, @NonNull Uri bitmapUri, int maxWidth, int maxHeight) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		decodeBitmapFromUri(resolver, bitmapUri, options);
		if (options.outHeight < 0 || options.outWidth < 0) {
			throw new IOException("Can't load bitmap from uri");
		}

		int sampleSize = calculateSampleSize(options.outWidth, options.outHeight,
				maxWidth, maxHeight);

		options.inMutable = true;
		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;

		return enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options));
	}

	public static Bitmap getBitmapFromFile(File bitmapFile) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		return enableAlpha(BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options));
	}

	private static Bitmap enableAlpha(Bitmap bitmap) {
		if (bitmap != null) {
			bitmap.setHasAlpha(true);
		}
		return bitmap;
	}
}
