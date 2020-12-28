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

import java.io.IOException;
import java.lang.ref.WeakReference;

public class LoadImageAsync extends AsyncTask<Void, Void, Bitmap> {
	private static final String TAG = LoadImageAsync.class.getSimpleName();
	private WeakReference<LoadImageCallback> callbackRef;
	private int maxWidth;
	private int maxHeight;
	private int requestCode;
	private Uri uri;
	private boolean scaleImage;

	public LoadImageAsync(LoadImageCallback callback, int requestCode, int maxWidth, int maxHeight, Uri uri) {
		this.callbackRef = new WeakReference<>(callback);
		this.requestCode = requestCode;
		this.uri = uri;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		this.scaleImage = true;
	}

	public LoadImageAsync(LoadImageCallback callback, int requestCode, Uri uri) {
		this.callbackRef = new WeakReference<>(callback);
		this.requestCode = requestCode;
		this.uri = uri;
		this.scaleImage = false;
	}

	@Override
	protected void onPreExecute() {
		LoadImageCallback callback = callbackRef.get();
		if (callback == null || callback.isFinishing()) {
			cancel(false);
		} else {
			callback.onLoadImagePreExecute(requestCode);
		}
	}

	@Override
	protected Bitmap doInBackground(Void... voids) {
		LoadImageCallback callback = callbackRef.get();
		if (callback == null || callback.isFinishing()) {
			return null;
		}

		if (uri == null) {
			Log.e(TAG, "Can't load image file, uri is null");
			return null;
		}

		try {
			ContentResolver resolver = callback.getContentResolver();
			FileIO.filename = "image";

			if (scaleImage) {
				return FileIO.getBitmapFromUri(resolver, uri, maxWidth, maxHeight);
			} else {
				return FileIO.getBitmapFromUri(resolver, uri);
			}
		} catch (IOException e) {
			Log.e(TAG, "Can't load image file", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		LoadImageCallback callback = callbackRef.get();
		if (callback != null && !callback.isFinishing()) {
			callback.onLoadImagePostExecute(requestCode, uri, result);
		}
	}

	public interface LoadImageCallback {
		void onLoadImagePostExecute(int requestCode, Uri uri, Bitmap result);
		void onLoadImagePreExecute(int requestCode);
		ContentResolver getContentResolver();
		boolean isFinishing();
	}
}
