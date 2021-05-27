/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.iotasks.BitmapReturnValue;
import org.catrobat.paintroid.presenter.MainActivityPresenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

public final class FileIO {
	public static String filename = "image";
	public static String ending = ".png";
	public static int compressQuality = 100;
	public static Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
	public static boolean catroidFlag = false;
	public static boolean isCatrobatImage = false;
	public static boolean wasImageLoaded = false;

	public static String currentFileNameJpg = null;
	public static String currentFileNamePng = null;
	public static String currentFileNameOra = null;
	public static Uri uriFileJpg = null;
	public static Uri uriFilePng = null;
	public static Uri uriFileOra = null;

	private FileIO() {
		throw new AssertionError();
	}

	private static void saveBitmapToStream(OutputStream outputStream, Bitmap bitmap) throws IOException {
		if (bitmap == null || bitmap.isRecycled()) {
			throw new IllegalArgumentException("Bitmap is invalid");
		}

		if (compressFormat == Bitmap.CompressFormat.JPEG) {
			Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), bitmap.getConfig());
			Canvas canvas = new Canvas(newBitmap);
			canvas.drawColor(Color.WHITE);
			canvas.drawBitmap(bitmap, 0F, 0F, null);

			bitmap = newBitmap;
		}

		if (!bitmap.compress(compressFormat, compressQuality, outputStream)) {
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

	public static Uri saveBitmapToFile(String fileName, Bitmap bitmap, ContentResolver resolver) throws IOException {
		OutputStream fos;
		Uri imageUri;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			ContentValues contentValues = new ContentValues();
			contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
			contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
			contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

			imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
			fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));

			try {
				saveBitmapToStream(fos, bitmap);

				Objects.requireNonNull(fos, "Can't create fileoutputstream!");
			} finally {
				fos.close();
			}
		} else {
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

			imageUri = Uri.fromFile(file);
		}

		return imageUri;
	}

	public static Uri saveBitmapToCache(Bitmap bitmap, MainActivity mainActivity) {
		Uri uri = null;
		try {
			File cachePath = new File(mainActivity.getCacheDir(), "images");
			cachePath.mkdirs();
			FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
			saveBitmapToStream(stream, bitmap);
			stream.close();
			File imagePath = new File(mainActivity.getCacheDir(), "images");
			File newFile = new File(imagePath, "image.png");
			String fileProviderString = mainActivity.getApplicationContext().getPackageName() + ".fileprovider";
			uri = FileProvider.getUriForFile(mainActivity.getApplicationContext(), fileProviderString, newFile);
		} catch (IOException e) {
			Log.e("Can not write", "Can not write png to stream.", e);
		}
		return uri;
	}

	public static String getDefaultFileName() {
		return filename + ending;
	}

	public static File createNewEmptyPictureFile(String filename, Activity activity) throws NullPointerException {
		if (filename == null) {
			filename = getDefaultFileName();
		}

		if (!filename.toLowerCase(Locale.US).endsWith(ending.toLowerCase(Locale.US))) {
			filename += ending;
		}

		if (!Objects.requireNonNull(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).exists()
				&& !Objects.requireNonNull(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)).mkdirs()) {
			throw new NullPointerException("Can not create media directory.");
		}

		return new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
	}

	private static Bitmap decodeBitmapFromUri(ContentResolver resolver, @NonNull Uri uri, BitmapFactory.Options options, Context context) throws IOException {
		InputStream inputStream = resolver.openInputStream(uri);
		Bitmap bitmap;
		float angle;
		if (inputStream == null) {
			throw new IOException("Can't open input stream");
		}
		try {
			bitmap = BitmapFactory.decodeStream(inputStream, null, options);
			if (options.inJustDecodeBounds) {
				return bitmap;
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				angle = getBitmapOrientationFromInputStream(resolver, uri);
			} else {
				angle = getBitmapOrientationFromUri(uri, context);
			}

			return getOrientedBitmap(bitmap, angle);
		} finally {
			inputStream.close();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private static float getBitmapOrientationFromInputStream(ContentResolver resolver, @NonNull Uri uri) throws IOException {
		InputStream inputStream = resolver.openInputStream(uri);
		if (inputStream == null) {
			return 0f;
		}

		try {
			ExifInterface exifInterface = new ExifInterface(inputStream);
			return getBitmapOrientation(exifInterface);
		} finally {
			inputStream.close();
		}
	}

	private static float getBitmapOrientationFromUri(@NonNull Uri uri, Context context) throws IOException {
		ExifInterface exifInterface = new ExifInterface(MainActivityPresenter.getPathFromUri(context, uri));
		return getBitmapOrientation(exifInterface);
	}

	public static Bitmap getOrientedBitmap(Bitmap bitmap, float angle) {
		if (bitmap == null) {
			return null;
		}

		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		bitmap.recycle();
		bitmap = null;

		return rotatedBitmap;
	}

	public static float getBitmapOrientation(ExifInterface exifInterface) {
		if (exifInterface == null) {
			return 0f;
		}

		int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		float angle = 0;
		switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				angle = 90f;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				angle = 180f;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				angle = 270f;
				break;
		}

		return angle;
	}

	public static void parseFileName(Uri uri, ContentResolver resolver) {
		String fileName = "image";

		Cursor cursor = null;
		try {
			cursor = resolver.query(uri, new String[]{
					MediaStore.Images.ImageColumns.DISPLAY_NAME
			}, null, null, null);

			if (cursor != null && cursor.moveToFirst()) {
				fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
			}
		} finally {

			if (cursor != null) {
				cursor.close();
			}
		}

		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			ending = ".jpg";
			compressFormat = Bitmap.CompressFormat.JPEG;
			filename = fileName.substring(0, fileName.length() - FileIO.ending.length());
		} else if (fileName.endsWith(".png")) {
			ending = ".png";
			compressFormat = Bitmap.CompressFormat.PNG;
			filename = fileName.substring(0, fileName.length() - FileIO.ending.length());
		}
	}

	public static void saveFileFromUri(Uri uri, File destFile, Context context) {
		try (InputStream fileInputStream = context.getContentResolver().openInputStream(uri); OutputStream fileOutputStream = new FileOutputStream(destFile)) {
			copyStreams(fileInputStream, fileOutputStream);
		} catch (IOException e) {
			Log.e("FileIO", "Can not copy streams.", e);
		}
	}

	public static long copyStreams(InputStream from, OutputStream to) throws IOException {
		byte[] buffer = new byte[4096];
		long total = 0;
		while (true) {
			int read = from.read(buffer);
			if (read == -1) {
				break;
			}
			to.write(buffer, 0, read);
			total += read;
		}
		return total;
	}

	public static int checkIfDifferentFile(String filename) {
		if (currentFileNamePng == null && currentFileNameJpg == null && currentFileNameOra == null) {
			return Constants.IS_NO_FILE;
		}

		if (currentFileNameJpg != null && currentFileNameJpg.equals(filename)) {
			return Constants.IS_JPG;
		}

		if (currentFileNamePng != null && currentFileNamePng.equals(filename)) {
			return Constants.IS_PNG;
		}

		if (currentFileNameOra != null && currentFileNameOra.equals(filename)) {
			return Constants.IS_ORA;
		}

		return Constants.IS_NO_FILE;
	}

	public static int calculateSampleSize(int width, int height, int maxWidth, int maxHeight) {
		int sampleSize = 1;
		while (width > maxWidth || height > maxHeight) {
			width /= 2;
			height /= 2;
			sampleSize *= 2;
		}
		return sampleSize;
	}

	public static Bitmap getBitmapFromUri(ContentResolver resolver, @NonNull Uri bitmapUri, Context context) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		return enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options, context));
	}

	public static boolean hasEnoughMemory(ContentResolver resolver, @NonNull Uri bitmapUri, Context context) throws IOException {
		long requiredMemory;
		long availableMemory;
		boolean scaling = false;

		ActivityManager.MemoryInfo memoryinfo = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(memoryinfo);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		decodeBitmapFromUri(resolver, bitmapUri, options, context);
		if (options.outHeight < 0 || options.outWidth < 0) {
			throw new IOException("Can't load bitmap from uri");
		}

		if (((memoryinfo.availMem - memoryinfo.threshold) * 0.9) > 5000 * 5000 * 4) {
			availableMemory = (long) 5000 * 5000 * 4;
		} else {
			availableMemory = (long) ((memoryinfo.availMem - memoryinfo.threshold) * 0.9);
		}
		requiredMemory = options.outWidth * options.outHeight * 4;
		if (requiredMemory > availableMemory) {
			scaling = true;
		}

		return scaling;
	}

	public static int getScaleFactor(ContentResolver resolver, @NonNull Uri bitmapUri, Context context) throws IOException {
		float heightToWidthFactor;
		float availablePixels;
		float availableHeight;
		float availableWidth;
		float availableMemory;

		ActivityManager.MemoryInfo memoryinfo = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(memoryinfo);
		BitmapFactory.Options options = new BitmapFactory.Options();
		decodeBitmapFromUri(resolver, bitmapUri, options, context);
		if (options.outHeight <= 0 || options.outWidth <= 0) {
			throw new IOException("Can't load bitmap from uri");
		}
		Runtime info = Runtime.getRuntime();
		availableMemory = (float) ((info.maxMemory() - info.totalMemory() + info.freeMemory()) * 0.9);
		heightToWidthFactor = (float) (options.outWidth / (options.outHeight * 1.0));
		availablePixels = (float) ((availableMemory * 0.9) / 4.0); //4 byte per pixel, 10% safety buffer on memory
		availableHeight = (float) Math.sqrt(availablePixels / heightToWidthFactor);
		availableWidth = availablePixels / availableHeight;
		return calculateSampleSize(options.outWidth, options.outHeight,
				(int) availableWidth, (int) availableHeight);
	}

	public static BitmapReturnValue getBitmapReturnValueFromUri(ContentResolver resolver, @NonNull Uri bitmapUri, Context context) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		options.inJustDecodeBounds = false;
		boolean scaling = hasEnoughMemory(resolver, bitmapUri, context);
		return new BitmapReturnValue(null, enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options, context)), scaling);
	}

	public static BitmapReturnValue getScaledBitmapFromUri(ContentResolver resolver, @NonNull Uri bitmapUri, Context context) throws IOException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		options.inJustDecodeBounds = false;
		options.inSampleSize = getScaleFactor(resolver, bitmapUri, context);
		return new BitmapReturnValue(null, enableAlpha(decodeBitmapFromUri(resolver, bitmapUri, options, context)), false);
	}

	public static Bitmap getBitmapFromFile(File bitmapFile) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		return enableAlpha(BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options));
	}

	public static Bitmap enableAlpha(Bitmap bitmap) {
		if (bitmap != null) {
			bitmap.setHasAlpha(true);
		}
		return bitmap;
	}
}
