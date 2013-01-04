/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import java.io.File;

import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.CropTool;
import org.catrobat.paintroid.tools.implementation.CursorTool;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.tools.implementation.EraserTool;
import org.catrobat.paintroid.tools.implementation.FlipTool;
import org.catrobat.paintroid.tools.implementation.MagicTool;
import org.catrobat.paintroid.tools.implementation.MoveZoomTool;
import org.catrobat.paintroid.tools.implementation.PipetteTool;
import org.catrobat.paintroid.tools.implementation.StampTool;

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

public class Utils {

	public static Tool createTool(ToolType toolType, Context context) {
		switch (toolType) {
		case BRUSH:
			return new DrawTool(context, toolType);
		case CURSOR:
			return new CursorTool(context, toolType);
		case STAMP:
		case IMPORTPNG:
			return new StampTool(context, toolType);
		case PIPETTE:
			return new PipetteTool(context, toolType);
		case MAGIC:
			return new MagicTool(context, toolType);
		case CROP:
			return new CropTool(context, toolType);
		case ERASER:
			return new EraserTool(context, toolType);
		case FLIP:
			return new FlipTool(context, toolType);
		case MOVE:
		case ZOOM:
			return new MoveZoomTool(context, toolType);
			// case FILL_RECT:
			// return new RectangleFillTool(context, toolType);
		default:
			break;
		}
		return new DrawTool(context, ToolType.BRUSH);

	}

	public static String createFilePathFromUri(Activity activity, Uri uri) {
		String filepath = null;
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity
				.managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int columnIndex = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
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

		Bitmap unmutableBitmap = BitmapFactory.decodeFile(
				bitmapFile.getAbsolutePath(), options);
		tmpWidth = unmutableBitmap.getWidth();
		tmpHeight = unmutableBitmap.getHeight();
		int[] tmpPixels = new int[tmpWidth * tmpHeight];
		unmutableBitmap.getPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		Bitmap mutableBitmap = Bitmap.createBitmap(tmpWidth, tmpHeight,
				Bitmap.Config.ARGB_8888);
		mutableBitmap.setPixels(tmpPixels, 0, tmpWidth, 0, 0, tmpWidth,
				tmpHeight);

		return mutableBitmap;
	}

	public static String getVersionName(Context context) {
		String versionName = "unknown";
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException nameNotFoundException) {
			Log.e(PaintroidApplication.TAG, "Name not found",
					nameNotFoundException);
		}
		return versionName;
	}
}
