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
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import at.tugraz.ist.paintroid.MainActivity.ToolType;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.CursorTool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.tools.implementation.StampTool;

public class Utils {

	public static Tool createTool(ToolType toolType, Context context) {
		switch (toolType) {
			case BRUSH:
				return new DrawTool(context);
			case CURSOR:
				return new CursorTool(context);
			case STAMP:
				return new StampTool(context);
			default:
				break;
		}
		return new DrawTool(context);
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

	public static Bitmap decodeFile(Context c, File f) {
		Bitmap tmpBitmap = null;
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 640;

			// Find the correct scale value. It should be the power of 2.
			int tmpWidth = o.outWidth, tmpHeight = o.outHeight;
			int scale = 1;

			while (tmpWidth / 2 > REQUIRED_SIZE || tmpHeight / 2 > REQUIRED_SIZE) {
				tmpWidth /= 2;
				tmpHeight /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			tmpBitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);

			// http://sudarnimalan.blogspot.com/2011/09/android-convert-immutable-bitmap-into.html
			// this is the file going to use temporally to save the bytes.
			File file = new File(c.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tmp");
			file.getParentFile().mkdirs();

			// Open an RandomAccessFile
			RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

			// get the width and height of the source bitmap.
			int width = tmpBitmap.getWidth();
			int height = tmpBitmap.getHeight();

			// Copy the byte to the file
			// Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
			FileChannel channel = randomAccessFile.getChannel();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, width * height * 4);
			tmpBitmap.copyPixelsToBuffer(map);
			// recycle the source bitmap, this will be no longer used.
			tmpBitmap.recycle();
			// Create a new bitmap to load the bitmap again.
			tmpBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			map.position(0);
			// load it back from temporary
			tmpBitmap.copyPixelsFromBuffer(map);
			// close the temporary file and channel , then delete that also
			channel.close();
			randomAccessFile.close();
			file.delete();
		} catch (Exception e) {
			Log.e(PaintroidApplication.TAG, "ERROR ", e);
		}
		return tmpBitmap;
	}
}
