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

package org.catrobat.paintroid.test.utils;

import android.app.UiAutomation;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.test.platform.app.InstrumentationRegistry;

public class ScreenshotOnFailRule extends TestWatcher {
	private static final String LOG_TAG = ScreenshotOnFailRule.class.getSimpleName();

	private final UiAutomation uiAutomation =
			Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
					? InstrumentationRegistry.getInstrumentation().getUiAutomation()
					: null;

	@Override
	protected void failed(Throwable e, Description description) {
		Log.i(LOG_TAG, "taking screenshot of failed test " + description.getMethodName());

		if (uiAutomation == null) {
			Log.i(LOG_TAG, "api level doesn't support screenshots.");
			return;
		}

		Bitmap screenshot = uiAutomation.takeScreenshot();
		if (screenshot == null) {
			Log.e(LOG_TAG, "failed to take screenshot");
			return;
		}

		File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/screenshots/" + InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName());
		if (!path.exists() && !path.mkdirs()) {
			Log.e(LOG_TAG, "failed to create screenshot path");
		}

		String filename = description.getClassName() + "-" + description.getMethodName() + ".png";
		saveScreenshot(screenshot, new File(path, filename));
	}

	private void saveScreenshot(Bitmap screenshot, File path) {

		BufferedOutputStream fileStream = null;
		try {
			fileStream = new BufferedOutputStream(new FileOutputStream(path));
			screenshot.compress(Bitmap.CompressFormat.PNG, 90, fileStream);
			fileStream.flush();
		} catch (IOException e) {
			Log.e(LOG_TAG, "failed to save screen shot to file", e);
		} finally {
			if (fileStream != null) {
				try {
					fileStream.close();
				} catch (IOException ioe) {
					Log.e(LOG_TAG, ioe.getMessage());
				}
			}
			screenshot.recycle();
		}
	}
}
