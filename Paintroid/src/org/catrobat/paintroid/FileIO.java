/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("NewApi")
public abstract class FileIO {
	private static File PAINTROID_MEDIA_FILE = null;

	private FileIO() {
	}

	public static File saveBitmap(Context context, Bitmap bitmap, String name) {
		if (initialisePaintroidMediaDirectory() == false) {
			return null;
		}

		final int QUALITY = 100;
		final String ENDING = ".png";
		final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;
		File file = null;

		if (bitmap == null || bitmap.isRecycled() || name == null
				|| name.length() < 1) {
			Log.e(PaintroidApplication.TAG, "ERROR saving bitmap " + name);
		} else {
			file = createNewEmptyPictureFile(context, name + ENDING);
		}

		if (file != null) {
			try {
				if (file.exists() == false) {
					// new File(file.getParent()).mkdirs();
					file.createNewFile();
				}
				bitmap.compress(FORMAT, QUALITY, new FileOutputStream(file));
				String[] paths = new String[] { file.getAbsolutePath() };
				MediaScannerConnection.scanFile(context, paths, null, null);
				Toast.makeText(context,
						"saved file to: " + file.getAbsolutePath(),
						Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e(PaintroidApplication.TAG, "ERROR writing " + file, e);
			}
		}

		return file;
	}

	public static File createNewEmptyPictureFile(Context context,
			String filename) {
		if (initialisePaintroidMediaDirectory() == true) {
			return new File(PAINTROID_MEDIA_FILE, filename);
		} else {
			return null;
		}
	}

	public static String getRealPathFromURI(Context context, Uri imageUri) {
		String path = null;
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(imageUri,
				filePathColumn, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			path = cursor.getString(columnIndex);
		} else {
			try {
				File file = new File(new java.net.URI(imageUri.toString()));
				path = file.getAbsolutePath();
			} catch (URISyntaxException e) {
				Log.e("PAINTROID", "URI ERROR ", e);
			}
		}

		return path;
	}

	private static boolean initialisePaintroidMediaDirectory() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			PAINTROID_MEDIA_FILE = new File(
					Environment.getExternalStorageDirectory(),
					"/"
							+ PaintroidApplication.applicationContext
									.getString(R.string.app_name) + "/");
		} else {
			return false;
		}
		if (PAINTROID_MEDIA_FILE != null) {
			if (PAINTROID_MEDIA_FILE.isDirectory() == false) {

				return PAINTROID_MEDIA_FILE.mkdirs();
			}
		} else {
			return false;
		}
		return true;
	}

	public static Bitmap getBitmapFromFile(File bitmapFile) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);

		int tmpWidth = options.outWidth;
		int tmpHeight = options.outHeight;
		int sampleSize = 1;

		while (tmpWidth / 2 > 640 || tmpHeight / 2 > 640) {
			tmpWidth /= 2;
			tmpHeight /= 2;
			sampleSize *= 2;
		}

		options.inJustDecodeBounds = false;
		options.inSampleSize = sampleSize;

		Bitmap unmutableBitmap = BitmapFactory.decodeFile(
				bitmapFile.getAbsolutePath(), options);
		tmpWidth = unmutableBitmap.getWidth();
		tmpHeight = unmutableBitmap.getHeight();
		int[] tmpPixels = new int[tmpWidth * tmpHeight];
		unmutableBitmap.getPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		Bitmap mutableBitmap = Bitmap.createBitmap(tmpWidth, tmpHeight,
				Bitmap.Config.ARGB_8888);
		mutableBitmap.setPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		return mutableBitmap;
	}

	public static String createFilePathFromUri(Activity activity, Uri uri) {
		String filepath = null;
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			filepath = cursor.getString(columnIndex);
		}
		if (filepath == null) {
			filepath = uri.getPath();
		}
		return filepath;
	}
}
