/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.paintroid.test.espresso;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SaveCompressImageIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new IntentsTestRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	private File testImageFile;
	private static ArrayList<File> deletionFileList = null;
	private MainActivity activity;

	@Before
	public void setUp() {
		try {
			activity = activityTestRule.getActivity();
			testImageFile = File.createTempFile("PocketPaintTest", ".jpg");
			deletionFileList = new ArrayList<>();
			deletionFileList.add(testImageFile);
			Bitmap bitmap = createTestBitmap();
			OutputStream outputStream = new FileOutputStream(testImageFile);
			assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream));
			outputStream.close();
		} catch (IOException e) {
			throw new AssertionError("Could not create temp file", e);
		}

		Intent intent = new Intent();
		intent.setData(Uri.fromFile(testImageFile));
		Instrumentation.ActivityResult resultOK = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK);
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
	public void testSaveImage() throws IOException {
		String testName = UUID.randomUUID().toString();
		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_load_image)).perform(click());

		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image)).perform(click());
		onView(withId(R.id.pocketpaint_image_name_save_text)).perform(replaceText(testName));
		onView(withId(R.id.pocketpaint_save_dialog_spinner)).perform(click());
		onData(allOf(is(instanceOf(String.class)), is("jpg"))).inRoot(isPlatformPopup()).perform(click());
		onView(withText(R.string.save_button_text)).perform(click());
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inMutable = true;
		Bitmap compressedBitmap = FileIO.INSTANCE.decodeBitmapFromUri(this.activity.getContentResolver(), Objects.requireNonNull(activity.model.getSavedPictureUri()), options, this.activity.getApplicationContext());
		Bitmap testBitmap = FileIO.INSTANCE.getBitmapFromFile(testImageFile);
		assertThat(compressedBitmap.getWidth(), is(equalTo(testBitmap.getWidth())));
		assertThat(compressedBitmap.getHeight(), is(equalTo(testBitmap.getHeight())));
	}

	private Bitmap createTestBitmap() {
		Bitmap bitmap;
		int width = 720;
		int height = 1280;
		Bitmap.Config bitmapConfig = Bitmap.Config.ARGB_8888;
		int bytesPerPixel = 4;

		byte[] b = new byte[width * height * bytesPerPixel];
		Random r = new Random();
		r.setSeed(0);
		r.nextBytes(b);
		bitmap = Bitmap.createBitmap(width, height, bitmapConfig);
		Canvas canvas = new Canvas(bitmap);
		int byteIndex = 0;
		Paint paint = new Paint();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int color = Color.argb(b[byteIndex++], b[byteIndex++], b[byteIndex++], b[byteIndex++]);
				paint.setColor(color);
				canvas.drawPoint(j, i, paint);
			}
		}
		return bitmap;
	}
}
