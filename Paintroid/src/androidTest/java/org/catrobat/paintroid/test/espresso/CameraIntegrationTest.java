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

package org.catrobat.paintroid.test.espresso;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.intent.IntentCallback;
import android.support.test.runner.intent.IntentMonitorRegistry;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getWorkingBitmap;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class CameraIntegrationTest {

	private static ArrayList<File> deletionFileList = null;
	private static IntentCallback cameraIntentCallback = null;

	@Rule
	public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE);

	@Before
	public void setUp() {
		onToolBarView().performSelectTool(ToolType.BRUSH);
		deletionFileList = new ArrayList<>();

		cameraIntentCallback = new IntentCallback() {
			@Override
			public void onIntentSent(Intent intent) {
				if (intent.getAction().equals("android.media.action.IMAGE_CAPTURE")) {
					try {
						Uri imageUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);

						assertTrue(imageUri.toString().contains("content"));

						String filename = imageUri.getPathSegments().get(imageUri.getPathSegments().size() - 1);

						File file = new File(Environment.getExternalStorageDirectory() + "/Pocket Paint/" + filename);
						deletionFileList.add(file);

						Context context = InstrumentationRegistry.getTargetContext();
						Bitmap icon = BitmapFactory.decodeResource(
								context.getResources(),
								R.drawable.ic_pocketpaint_tool_square);
						OutputStream out = context.getContentResolver().openOutputStream(imageUri);
						icon.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.flush();
						out.close();
					} catch (IOException e) {
						fail("Picture file could not be created.");
					}
				}
			}
		};
	}

	@After
	public void deleteTestImages() {
		for (File file : deletionFileList) {
			if (file.exists()) {
				assertTrue(file.delete());
			}
		}
	}

	@Test
	public void testCameraIntentSimulated() {
		intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
				new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

		IntentMonitorRegistry.getInstance().addIntentCallback(cameraIntentCallback);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_new_image))
				.perform(click());

		onView(withText(R.string.menu_new_image_from_camera))
				.perform(click());

		assertEquals("Color on drawing surface wrong",
				Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(new PointF(0, 0)));

		IntentMonitorRegistry.getInstance().removeIntentCallback(cameraIntentCallback);
	}

	@Test
	public void testCameraIntentCanceled() {
		PointF testPixel = new PointF(0, 0);

		intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
				new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		getWorkingBitmap().setPixel((int) testPixel.x, (int) testPixel.y, Color.BLACK);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_new_image))
				.perform(click());

		onView(withText(R.string.discard_button_text))
				.perform(click());

		onView(withText(R.string.menu_new_image_from_camera))
				.perform(click());

		assertEquals("Color on drawing surface wrong",
				Color.BLACK,
				PaintroidApplication.drawingSurface.getPixel(testPixel));
	}
}
