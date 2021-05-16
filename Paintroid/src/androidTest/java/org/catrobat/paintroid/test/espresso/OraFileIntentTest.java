/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2021 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class OraFileIntentTest {
	private static ArrayList<File> deletionFileList = null;
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);
	private ContentResolver resolver;

	@Before
	public void setUp() {
		onToolBarView().performSelectTool(ToolType.BRUSH);
		deletionFileList = new ArrayList<>();
		resolver = launchActivityRule.getActivity().getContentResolver();
	}

	@After
	public void tearDown() {
		for (File file : deletionFileList) {
			if (file != null && file.exists()) {
				assertTrue(file.delete());
			}
		}
	}

	@Test
	public void testCheckIntentForOraFile() {
		Intent intent = new Intent();
		Uri receivedUri = createTestImageFile();
		Bitmap receivedBitmap = null;

		try {
			receivedBitmap = FileIO.getBitmapFromUri(resolver, receivedUri, launchActivityRule.getActivity().getBaseContext());
		} catch (Exception e) {
			Log.e("Can't Read", "Can't get Bitmap from File");
		}

		Objects.requireNonNull(receivedBitmap);
		intent.setAction(Intent.ACTION_EDIT);
		intent.setData(receivedUri);
		intent.setType("image/*");
		intent.putExtra(Intent.EXTRA_STREAM, receivedUri);

		launchActivityRule.launchActivity(intent);
		Intent mainActivityIntent = launchActivityRule.getActivity().getIntent();

		String intentAction = intent.getAction();
		String intentType = intent.getType();
		Bundle intentBundle = intent.getExtras();
		Objects.requireNonNull(intentBundle);
		Uri intentUri = (Uri) intentBundle.get(Intent.EXTRA_STREAM);

		String mainActivityIntentAction = mainActivityIntent.getAction();
		String mainActivityIntentType = mainActivityIntent.getType();
		Bundle mainActivityIntentBundle = mainActivityIntent.getExtras();
		Objects.requireNonNull(mainActivityIntentBundle);
		Uri mainActivityIntentUri = (Uri) mainActivityIntentBundle.get(Intent.EXTRA_STREAM);
		Bitmap mainActivityIntentBitmap = null;
		Objects.requireNonNull(mainActivityIntentUri);

		try {
			mainActivityIntentBitmap = FileIO.getBitmapFromUri(resolver, mainActivityIntentUri, launchActivityRule.getActivity().getBaseContext());
		} catch (Exception e) {
			Log.e("Can't read", "Can't get Bitmap From File");
		}

		Objects.requireNonNull(mainActivityIntentBitmap);

		assertEquals(intentAction, mainActivityIntentAction);
		assertEquals(intentType, mainActivityIntentType);
		assertEquals(intentUri, mainActivityIntentUri);
		assertEquals(receivedBitmap.getWidth(), mainActivityIntentBitmap.getWidth());
		assertEquals(receivedBitmap.getHeight(), mainActivityIntentBitmap.getHeight());
	}

	private Uri createTestImageFile() {
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);

		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.ora");
		contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
		}

		Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
		try {
			OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
			assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos));
			assert fos != null;
			fos.close();
		} catch (IOException e) {
			throw new AssertionError("Picture file could not be created.", e);
		}

		File imageFile = new File(imageUri.getPath(), "testfile.ora");
		deletionFileList.add(imageFile);
		return imageUri;
	}
}
