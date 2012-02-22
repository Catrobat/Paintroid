/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URISyntaxException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class FileIO {

	private FileIO() {
	}

	// /**
	// * @param context Context in which method is called.
	// * @param bitmap Bitmap which shall be saved. Can be null (only an empty File is created).
	// * @param uri Uri of the file to be saved.
	// * @return Null if an error occured, otherwise the File which was created.
	// */
	// public static File saveBitmap(Context context, Bitmap bitmap, Uri uri) {
	// if (uri != null) {
	// return saveBitmap(context, bitmap, uri.getPath());
	// } else {
	// Log.e(PaintroidApplication.TAG, "CANNOT SAVE BITMAP, URI IS NULL");
	// return null;
	// }
	// }

	/**
	 * @param context Context in which method is called.
	 * @param bitmap Bitmap which shall be saved. Can be null (only an empty File is created).
	 * @param name Name of the file to be saved (without filetype).
	 * @return Null if an error occured, otherwise the File which was created.
	 */
	public static File saveBitmap(Context context, Bitmap bitmap, String name) {
		final int QUALITY = 100;
		final String ENDING = ".png";
		final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;
		File file = null;

		if (bitmap == null || bitmap.isRecycled() || name == null || name.length() < 1) {
			Log.e(PaintroidApplication.TAG, "ERROR saving bitmap " + name);
		} else {
			file = createNewEmptyPictureFile(context, name + ENDING);
		}
		if (file != null) {
			try {
				bitmap.compress(FORMAT, QUALITY, new FileOutputStream(file));
				String[] paths = new String[] { file.getAbsolutePath() };
				MediaScannerConnection.scanFile(context, paths, null, null);
			} catch (Exception e) {
				Log.e(PaintroidApplication.TAG, "ERROR writing " + file, e);
			}
		}
		return file;
	}

	public static File createNewEmptyPictureFile(Context context, String filename) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
		} else {
			return null;
		}
	}

	public static String getRealPathFromURI(Context context, Uri imageUri) {
		String path = "";
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);

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
}
