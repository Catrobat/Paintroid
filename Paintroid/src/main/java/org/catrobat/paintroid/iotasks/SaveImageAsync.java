/*
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

package org.catrobat.paintroid.iotasks;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.tools.Workspace;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.Nullable;

public class SaveImageAsync extends AsyncTask<Void, Void, Uri> {
	private static final String TAG = SaveImageAsync.class.getSimpleName();
	private WeakReference<SaveImageCallback> callbackRef;
	private int requestCode;
	private Uri uri;
	private boolean saveAsCopy;
	private Workspace workspace;

	public SaveImageAsync(SaveImageCallback activity, int requestCode, Workspace workspace, @Nullable Uri uri, boolean saveAsCopy) {
		this.callbackRef = new WeakReference<>(activity);
		this.requestCode = requestCode;
		this.uri = uri;
		this.saveAsCopy = saveAsCopy;
		this.workspace = workspace;
	}

	@Override
	protected void onPreExecute() {
		SaveImageCallback callback = callbackRef.get();
		if (callback == null || callback.isFinishing()) {
			cancel(false);
		} else {
			callback.onSaveImagePreExecute(requestCode);
		}
	}

	@Override
	protected Uri doInBackground(Void... voids) {
		SaveImageCallback callback = callbackRef.get();
		if (callback != null && !callback.isFinishing()) {
			try {
				Bitmap bitmap = workspace.getBitmapOfAllLayers();
				String fileName = FileIO.getDefaultFileName();
				int fileExistsValue = FileIO.checkIfDifferentFile(fileName);

				if (FileIO.isCatrobatImage) {
					List<Bitmap> bitmapList = workspace.getBitmapLisOfAllLayers();

					if (uri != null && fileExistsValue == Constants.IS_ORA) {
						setUriToFormatUri(fileExistsValue);
						return OpenRasterFileFormatConversion.saveOraFileToUri(bitmapList, uri, fileName, bitmap, callback.getContentResolver());
					} else {
						Uri imageUri = OpenRasterFileFormatConversion.exportToOraFile(bitmapList, fileName, bitmap, callback.getContentResolver());

						FileIO.currentFileNameOra = fileName;
						FileIO.uriFileOra = imageUri;

						return imageUri;
					}
				} else {
					if (uri != null && FileIO.catroidFlag) {
						return FileIO.saveBitmapToUri(uri, callback.getContentResolver(), bitmap);
					} else if (uri != null && fileExistsValue != Constants.IS_NO_FILE) {
						setUriToFormatUri(fileExistsValue);
						return FileIO.saveBitmapToUri(uri, callback.getContentResolver(), bitmap);
					} else {
						Uri imageUri = FileIO.saveBitmapToFile(fileName, bitmap, callback.getContentResolver());

						if (FileIO.ending.equals(".png")) {
							FileIO.currentFileNamePng = fileName;
							FileIO.uriFilePng = imageUri;
						} else {
							FileIO.currentFileNameJpg = fileName;
							FileIO.uriFileJpg = imageUri;
						}

						return imageUri;
					}
				}
			} catch (IOException e) {
				Log.d(TAG, "Can't save image file", e);
			}
		}

		return null;
	}

	private void setUriToFormatUri(int formatcode) {
		if (formatcode == Constants.IS_JPG) {
			if (FileIO.uriFileJpg != null) {
				uri = FileIO.uriFileJpg;
			}
		} else if (formatcode == Constants.IS_PNG) {
			if (FileIO.uriFilePng != null) {
				uri = FileIO.uriFilePng;
			}
		} else {
			if (FileIO.uriFileOra != null) {
				uri = FileIO.uriFileOra;
			}
		}
	}

	@Override
	protected void onPostExecute(Uri uri) {
		SaveImageCallback callback = callbackRef.get();
		if (callback != null && !callback.isFinishing()) {
			callback.onSaveImagePostExecute(requestCode, uri, saveAsCopy);
		}
	}

	public interface SaveImageCallback {
		void onSaveImagePreExecute(int requestCode);
		void onSaveImagePostExecute(int requestCode, Uri uri, boolean saveAsCopy);
		ContentResolver getContentResolver();
		boolean isFinishing();
	}
}
