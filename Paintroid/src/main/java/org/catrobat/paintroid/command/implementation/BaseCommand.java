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

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.util.Log;

import org.catrobat.paintroid.command.Command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import androidx.annotation.VisibleForTesting;

public abstract class BaseCommand implements Command {
	private static final String TAG = BaseCommand.class.getSimpleName();
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Paint paint;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public Bitmap bitmap;
	@VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
	public File fileToStoredBitmap;

	public BaseCommand() {
	}

	public BaseCommand(Paint paint) {
		if (paint != null) {
			this.paint = new Paint(paint);
		} else {
			Log.w(TAG, "Object is null falling back to default object in " + this.toString());
			this.paint = new Paint();
			this.paint.setColor(Color.BLACK);
			this.paint.setStrokeWidth(1);
			this.paint.setStrokeCap(Cap.SQUARE);
		}
	}

	@Override
	public void freeResources() {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		if (fileToStoredBitmap != null && fileToStoredBitmap.exists()) {
			fileToStoredBitmap.delete();
		}
	}

	protected final void storeBitmap(File cacheDir) {
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		fileToStoredBitmap = new File(cacheDir.getAbsolutePath(),
				Long.toString(random.nextLong()));
		try {
			FileOutputStream fos = new FileOutputStream(fileToStoredBitmap);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			Log.e(TAG, "Cannot store bitmap. ", e);
		}
		bitmap.recycle();
		bitmap = null;
	}

	public enum NotifyStates {
		COMMAND_STARTED, COMMAND_DONE, COMMAND_FAILED
	}
}
