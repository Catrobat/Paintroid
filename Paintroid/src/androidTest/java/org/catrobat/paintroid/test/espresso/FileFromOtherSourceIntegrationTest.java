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
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FileFromOtherSourceIntegrationTest {

	private static ArrayList<File> deletionFileList = null;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@ClassRule
	public static GrantPermissionRule grantPermissionRule = EspressoUtils.grantPermissionRulesVersionCheck();

	private ContentResolver resolver;

	@Before
	public void setUp() {
		onToolBarView().performSelectTool(ToolType.BRUSH);
		deletionFileList = new ArrayList<>();
		resolver = launchActivityRule.getActivity().getContentResolver();
	}

	@Test
	public void testGetSharedPictureFromOtherApp() {
		Intent intent = new Intent();
		Uri receivedUri = createTestImageFile();
		Bitmap receivedBitmap = null;

		try {
			receivedBitmap = FileIO.getBitmapFromUri(resolver, receivedUri);
		} catch (Exception e) {
			Log.e("Can't read", "Can't get Bitmap from File");
		}

		Objects.requireNonNull(receivedBitmap);
		intent.setData(receivedUri);
		intent.setType("image/png");
		intent.setAction(Intent.ACTION_SEND);
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
			mainActivityIntentBitmap = FileIO.getBitmapFromUri(resolver, mainActivityIntentUri);
		} catch (Exception e) {
			Log.e("Can't read", "Can't get Bitmap from File");
		}

		Objects.requireNonNull(mainActivityIntentBitmap);

		assertEquals(intentAction, mainActivityIntentAction);
		assertEquals(intentType, mainActivityIntentType);
		assertEquals(intentUri, mainActivityIntentUri);
		assertEquals(receivedBitmap.getWidth(), mainActivityIntentBitmap.getWidth());
		assertEquals(receivedBitmap.getHeight(), mainActivityIntentBitmap.getHeight());
	}

	@After
	public void tearDown() {
		for (File file : deletionFileList) {
			if (file != null && file.exists()) {
				assertTrue(file.delete());
			}
		}
	}

	private Uri createTestImageFile() {
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);

		ContentValues contentValues = new ContentValues();
		contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.jpg");
		contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
		}

		Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

		try {
			OutputStream fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
			assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos));
			assert fos != null;
			fos.close();
		} catch (IOException e) {
			throw new AssertionError("Picture file could not be created.", e);
		}

		File imageFile = new File(imageUri.getPath(), "testfile.jpg");
		deletionFileList.add(imageFile);

		return imageUri;
	}
}
