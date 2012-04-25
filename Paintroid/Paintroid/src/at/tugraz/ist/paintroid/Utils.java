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

package at.tugraz.ist.paintroid;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.CursorTool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.tools.implementation.MagicTool;
import at.tugraz.ist.paintroid.tools.implementation.PipetteTool;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;
import at.tugraz.ist.paintroid.ui.DrawingSurface;

public class Utils {

	public static Tool createTool(ToolType toolType, Context context, DrawingSurface drawingSurface) {
		switch (toolType) {
			case BRUSH:
				return new DrawTool(context, toolType);
			case CURSOR:
				return new CursorTool(context, toolType);
			case STAMP:
			case IMPORTPNG:
				return new StampTool(context, toolType, drawingSurface);
			case PIPETTE:
				return new PipetteTool(context, toolType, drawingSurface);
			case MAGIC:
				return new MagicTool(context, toolType);

			default:
				break;
		}
		return new DrawTool(context, ToolType.BRUSH);

	}

	public static String createFilePathFromUri(Activity activity, Uri uri) {
		String filepath = null;
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			filepath = cursor.getString(columnIndex);
		}
		if (filepath == null) {
			filepath = uri.getPath();
		}
		return filepath;
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

		Bitmap unmutableBitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), options);
		tmpWidth = unmutableBitmap.getWidth();
		tmpHeight = unmutableBitmap.getHeight();
		int[] tmpPixels = new int[tmpWidth * tmpHeight];
		unmutableBitmap.getPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth, tmpHeight);

		Bitmap mutableBitmap = Bitmap.createBitmap(tmpWidth, tmpHeight, Bitmap.Config.ARGB_8888);
		mutableBitmap.setPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth, tmpHeight);

		return mutableBitmap;
	}

	public static String getVersionName(Context context) {
		String versionName = "unknown";
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException nameNotFoundException) {
			Log.e(PaintroidApplication.TAG, "Name not found", nameNotFoundException);
		}
		return versionName;
	}
}
