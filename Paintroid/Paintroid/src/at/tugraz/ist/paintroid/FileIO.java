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
		// only static methods!
	}

	/**
	 * @param context
	 *            Context in which method is called.
	 * @param bitmap
	 *            Bitmap which shall be saved. Can be null (only an empty File is created).
	 * @param name
	 *            Name of the file to be saved (without filetype).
	 * @return Null if an error occured, otherwise the File which was created.
	 */
	public static File saveBitmap(Context context, Bitmap bitmap, String name) {
		final int QUALITY = 90;
		final String FILETYPE = ".png";
		final Bitmap.CompressFormat FORMAT = Bitmap.CompressFormat.PNG;
		//TODO: Enable saving of different filetpyes.

		File file = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), name + FILETYPE);
			if (bitmap != null) {
				try {
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					bitmap.compress(FORMAT, QUALITY, fileOutputStream);

					String[] paths = new String[] { file.getAbsolutePath() };
					MediaScannerConnection.scanFile(context, paths, null, null);
				} catch (Exception e) {
					Log.e("PAINTROID", "I/O ERROR: Could not write " + file, e);
				}
			}
		} else {
			Log.e("PAINTROID", "I/O ERROR: Media not mounted");
		}
		return file;
	}

	public static String getRealPathFromURI(Context context, Uri imageUri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String path = cursor.getString(columnIndex);
		return path;
	}
}
