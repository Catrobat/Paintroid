package org.catrobat.paintroid.test.espresso;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentTest {

    @Rule
    public ActivityTestRule<MainActivity> launchActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private ContentResolver contentResolver;
    private static ArrayList<File> deletionFileList = null;

    @Before
    public void setUp() {
        deletionFileList = new ArrayList<>();
        contentResolver = launchActivityTestRule.getActivity().getContentResolver();
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
    public void testAppliedChangesAfterOrientationChangePersist() {
        MockitoAnnotations.initMocks(this);
        Intents.init();

        Uri testUri = createTestImageFile();

        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("image/*")
                .putExtra(Intent.EXTRA_STREAM, testUri);

        launchActivityTestRule.launchActivity(intent);

        assertNull(launchActivityTestRule.getActivity().model.getSavedPictureUri());
        assertNull(launchActivityTestRule.getActivity().model.getCameraImageUri());

        onDrawingSurfaceView()
                .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);

        onToolBarView().performSelectTool(ToolType.FILL);

        onDrawingSurfaceView()
                .perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

        onDrawingSurfaceView()
                .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

        launchActivityTestRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        onDrawingSurfaceView()
                .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
    }

    private Uri createTestImageFile() {
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "testfile.jpeg");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        }

        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        try {
            OutputStream fos = contentResolver.openOutputStream(Objects.requireNonNull(imageUri));
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos));
            assert fos != null;
            fos.close();
        } catch (IOException e) {
            throw new AssertionError("Picture file could not be created.", e);
        }

        File imageFile = new File(imageUri.getPath(), "testfile.jpeg");
        deletionFileList.add(imageFile);
        return imageUri;
    }
}
