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

package org.catrobat.paintroid.ui;

import android.content.Context;
import android.net.Uri;

import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.iotasks.CreateFileAsync;
import org.catrobat.paintroid.iotasks.LoadImageAsync;
import org.catrobat.paintroid.iotasks.SaveImageAsync;
import org.catrobat.paintroid.tools.Workspace;

public class MainActivityInteractor implements MainActivityContracts.Interactor {

	@Override
	public void saveCopy(SaveImageAsync.SaveImageCallback callback, int requestCode, Workspace workspace, Context context) {
		new SaveImageAsync(callback, requestCode, workspace, null, true, context).execute();
	}

	@Override
	public void createFile(CreateFileAsync.CreateFileCallback callback, int requestCode, String filename) {
		new CreateFileAsync(callback, requestCode, filename).execute();
	}

	@Override
	public void saveImage(SaveImageAsync.SaveImageCallback callback, int requestCode, Workspace workspace, Uri uri, Context context) {
		new SaveImageAsync(callback, requestCode, workspace, uri, false, context).execute();
	}

	@Override
	public void loadFile(LoadImageAsync.LoadImageCallback callback, int requestCode, Uri uri, Context context, boolean scaling) {
		new LoadImageAsync(callback, requestCode, uri, context, scaling).execute();
	}
}
