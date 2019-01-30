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
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.intent.IntentCallback;
import android.support.test.runner.intent.IntentMonitorRegistry;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.OutputStream;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;

@RunWith(AndroidJUnit4.class)
public class CameraIntegrationTest {
	private static IntentCallback cameraIntentCallback = null;

	@Rule
	public IntentsTestRule<MainActivity> intentsTestRule = new IntentsTestRule<>(MainActivity.class);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA);

	@Before
	public void setUp() {
		onToolBarView().performSelectTool(ToolType.BRUSH);

		cameraIntentCallback = new IntentCallback() {
			@Override
			public void onIntentSent(Intent intent) {
				assertEquals("android.media.action.IMAGE_CAPTURE", intent.getAction());

				Uri imageUri = intent.getParcelableExtra(MediaStore.EXTRA_OUTPUT);
				assertNotNull(imageUri);

				Bitmap bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(bitmap);
				canvas.drawColor(Color.GREEN);

				ContentResolver contentResolver = InstrumentationRegistry.getTargetContext().getContentResolver();
				try {
					OutputStream out = contentResolver.openOutputStream(imageUri);
					assertNotNull(out);
					assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out));
					out.flush();
					out.close();
				} catch (IOException e) {
					throw new AssertionError("Picture file could not be created.", e);
				}
			}
		};

		IntentMonitorRegistry.getInstance().addIntentCallback(cameraIntentCallback);
	}

	@After
	public void tearDown() {
		IntentMonitorRegistry.getInstance().removeIntentCallback(cameraIntentCallback);
	}

	@Test
	public void testCameraIntentSimulated() {
		intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
				new Instrumentation.ActivityResult(Activity.RESULT_OK, null));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_new_image))
				.perform(click());

		onView(withText(R.string.menu_new_image_from_camera))
				.perform(click());

		onDrawingSurfaceView()
				.checkPixelColor(Color.GREEN, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testCameraIntentCanceled() {
		intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
				new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_new_image))
				.perform(click());

		onView(withText(R.string.discard_button_text))
				.perform(click());

		onView(withText(R.string.menu_new_image_from_camera))
				.perform(click());

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}
}
