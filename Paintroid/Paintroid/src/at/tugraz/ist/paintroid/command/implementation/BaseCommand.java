/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.command.implementation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.Command;

public abstract class BaseCommand implements Command {
	protected Paint mPaint;
	protected Bitmap mBitmap;
	protected File mFileToStoredBitmap;

	public BaseCommand() {
	}

	public BaseCommand(Paint paint) {
		if (paint != null) {
			mPaint = new Paint(paint);
		} else {
			Log.w(PaintroidApplication.TAG, "Object is null falling back to default object in " + this.toString());
			mPaint = new Paint();
			mPaint.setColor(Color.BLACK);
			mPaint.setStrokeWidth(1);
			mPaint.setStrokeCap(Cap.SQUARE);
		}
	}

	@Override
	public abstract void run(Canvas canvas, Bitmap bitmap);

	@Override
	public void freeResources() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		if (mFileToStoredBitmap != null && mFileToStoredBitmap.exists()) {
			mFileToStoredBitmap.delete();
		}
	}

	protected final void storeBitmap() {
		File cacheDir = PaintroidApplication.APPLICATION_CONTEXT.getCacheDir();
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		mFileToStoredBitmap = new File(cacheDir.getAbsolutePath(), Long.toString(random.nextLong()));
		try {
			FileOutputStream fos = new FileOutputStream(mFileToStoredBitmap);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			Log.e(PaintroidApplication.TAG, "Cannot store bitmap. ", e);
		}
		mBitmap.recycle();
		mBitmap = null;
	}
}
