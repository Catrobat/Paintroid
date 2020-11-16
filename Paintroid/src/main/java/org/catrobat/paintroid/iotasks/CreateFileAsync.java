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

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.catrobat.paintroid.FileIO;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;

public class CreateFileAsync extends AsyncTask<Void, Void, File> {
	private static final String TAG = CreateFileAsync.class.getSimpleName();
	private WeakReference<CreateFileCallback> callbackRef;
	private int requestCode;
	private String filename;

	public CreateFileAsync(CreateFileCallback callback, int requestCode, @Nullable String filename) {
		this.callbackRef = new WeakReference<>(callback);
		this.requestCode = requestCode;
		this.filename = filename;
	}

	@Override
	protected File doInBackground(Void... voids) {
		CreateFileCallback callback = callbackRef.get();
		try {
			return FileIO.createNewEmptyPictureFile(filename, callback.getFileActivity());
		} catch (NullPointerException e) {
			Log.e(TAG, "Can't create file", e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(File file) {
		CreateFileCallback callback = callbackRef.get();
		if (callback != null && !callback.isFinishing()) {
			callback.onCreateFilePostExecute(requestCode, file);
		}
	}

	public interface CreateFileCallback {
		void onCreateFilePostExecute(int requestCode, File file);
		Activity getFileActivity();
		boolean isFinishing();
	}
}
