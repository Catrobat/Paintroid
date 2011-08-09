/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test;

import java.util.Arrays;
import java.util.Locale;

import org.junit.Assert;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.FileActivity;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerView;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;

import com.jayway.android.robotium.solo.Solo;

public class ButtonFunctionTests extends ActivityInstrumentationTestCase2<MainActivity> {
	static final String TAG = "PAINTROIDTEST";

	private Solo solo;
	private MainActivity mainActivity;
	private int[] toolbarButtonId;
	private int[] toolbarButtonNormalId;
	private int[] toolbarButtonActiveId;

	public ButtonFunctionTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		solo = new Solo(getInstrumentation(), getActivity());
		mainActivity = (MainActivity) solo.getCurrentActivity();
		Locale defaultLocale = new Locale("en");
		Locale.setDefault(defaultLocale);
		Configuration config_before = new Configuration();
		config_before.locale = defaultLocale;
		mainActivity.getBaseContext().getResources()
				.updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());

		toolbarButtonId = new int[] { R.id.ibtn_handTool, R.id.ibtn_zoomTool, R.id.ibtn_brushTool,
				R.id.ibtn_eyeDropperTool, R.id.ibtn_magicWandTool, R.id.ibtn_undoTool, R.id.ibtn_redoTool,
				R.id.ibtn_fileActivity };
		toolbarButtonNormalId = new int[] { R.drawable.ic_hand, R.drawable.ic_zoom, R.drawable.ic_brush,
				R.drawable.ic_eyedropper, R.drawable.ic_magicwand, R.drawable.ic_undo, R.drawable.ic_redo,
				R.drawable.ic_filemanager };
		toolbarButtonActiveId = new int[] { R.drawable.ic_hand_active, R.drawable.ic_zoom_active,
				R.drawable.ic_brush_active, R.drawable.ic_eyedropper_active, R.drawable.ic_magicwand_active };
	}

	@Override
	public void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

	/**
	 * Check if toolbar buttons have to correct background images on startup.
	 */
	@Smoke
	public void testInitialToolbarButtonBackgrounds() throws Exception {
		int initialSelectedButtonId = R.id.ibtn_brushTool;
		for (int i = 0; i < toolbarButtonId.length; i++) {
			ImageButton toolButton = (ImageButton) mainActivity.findViewById(toolbarButtonId[i]);
			assertNotNull(toolButton);
			Drawable toolIcon = mainActivity.getResources().getDrawable(toolbarButtonNormalId[i]);
			if (toolbarButtonId[i] == initialSelectedButtonId) {
				toolIcon = mainActivity.getResources().getDrawable(toolbarButtonActiveId[i]);
			}

			Drawable buttonBg = toolButton.getBackground();
			Assert.assertArrayEquals(Utils.drawableToPixelArray(toolIcon), Utils.drawableToPixelArray(buttonBg));
		}
	}

	/**
	 * Check if toolbar buttons with alternative backgrounds change their background image when clicked.
	 */
	@Smoke
	public void testChangeToolbarButtonBackgrounds() throws Exception {
		for (int i = 0; i < toolbarButtonActiveId.length; i++) {
			ImageButton toolButton = (ImageButton) mainActivity.findViewById(toolbarButtonId[i]);
			assertNotNull(toolButton);

			solo.clickOnView(toolButton);
			Drawable toolIcon = mainActivity.getResources().getDrawable(toolbarButtonActiveId[i]);

			Drawable buttonBg = toolButton.getBackground();
			Assert.assertArrayEquals(Utils.drawableToPixelArray(toolIcon), Utils.drawableToPixelArray(buttonBg));

			int activeButtonId = toolbarButtonId[i];
			for (int j = 0; j < toolbarButtonId.length; j++) {
				ImageButton otherButton = (ImageButton) mainActivity.findViewById(toolbarButtonId[j]);
				assertNotNull(otherButton);
				toolIcon = mainActivity.getResources().getDrawable(toolbarButtonNormalId[j]);
				if (toolbarButtonId[i] == activeButtonId) {
					continue;
				}

				buttonBg = otherButton.getBackground();
				Assert.assertArrayEquals(Utils.drawableToPixelArray(toolIcon), Utils.drawableToPixelArray(buttonBg));
			}
		}
	}

	/**
	 * Test the button associated with the colorpicker.
	 */
	@Smoke
	public void testColorPickerButton() throws Exception {
		ImageButton button = (ImageButton) mainActivity.findViewById(R.id.ibtn_Color);
		assertNotNull(button);

		// inital color should be black
		int stdColor = mainActivity.getResources().getColor(R.color.std_color);
		assertEquals(stdColor, Color.BLACK);
		Drawable buttonBg = button.getBackground();
		int buttonColor = Utils.colorFromDrawable(buttonBg);
		assertEquals(stdColor, buttonColor);

		// clicking on the button should show the colorpicker dialog
		solo.clickOnView(button);
		boolean dialog = false;
		for (View v : solo.getCurrentViews()) {
			if (dialog = v instanceof ColorPickerView) {
				break;
			}
		}
		assertTrue(dialog);
	}

	/**
	 * Test the button associated with the stroke shape/width picker.
	 */
	@Smoke
	public void testStrokePickerButton() throws Exception {
		ImageButton button = (ImageButton) mainActivity.findViewById(R.id.ibtn_brushStroke);
		assertNotNull(button);

		Drawable icon = mainActivity.getResources().getDrawable(R.drawable.circle_3_32);
		Drawable buttonBg = button.getBackground();
		Assert.assertArrayEquals(Utils.drawableToPixelArray(icon), Utils.drawableToPixelArray(buttonBg));

		// clicking on the button should show the stroke picker dialog
		solo.clickOnView(button);
		boolean dialog = false;
		for (ImageButton b : solo.getCurrentImageButtons()) {
			if (dialog = b.getId() == R.id.stroke_ibtn_rect) {
				break;
			}
		}
		assertTrue(dialog);
	}

	/**
	 * Test if the undo button restores the previous bitmap.
	 */
	@Smoke
	public void testUndoButton() throws Exception {
		ImageButton button = (ImageButton) mainActivity.findViewById(R.id.ibtn_undoTool);

		Display display = mainActivity.getWindowManager().getDefaultDisplay();
		final int width = display.getWidth();
		final int height = display.getHeight();

		DrawingSurface drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);

		Bitmap before = Bitmap.createBitmap(drawingSurface.getBitmap());

		solo.clickOnScreen(width / 2, height / 2);
		solo.sleep(500);
		Bitmap after = drawingSurface.getBitmap();

		assertFalse(Arrays.equals(Utils.bitmapToPixelArray(before), Utils.bitmapToPixelArray(after)));

		solo.clickOnView(button);
		after = drawingSurface.getBitmap();

		Assert.assertArrayEquals(Utils.bitmapToPixelArray(before), Utils.bitmapToPixelArray(after));

		before.recycle();
	}

	/**
	 * Test if the redo button restores the undone action.
	 */
	@Smoke
	public void testRedoButton() throws Exception {
		ImageButton undo = (ImageButton) mainActivity.findViewById(R.id.ibtn_undoTool);
		ImageButton redo = (ImageButton) mainActivity.findViewById(R.id.ibtn_redoTool);

		Display display = mainActivity.getWindowManager().getDefaultDisplay();
		final int width = display.getWidth();
		final int height = display.getHeight();

		DrawingSurface drawingSurface = (DrawingSurface) mainActivity.findViewById(R.id.surfaceview);

		Bitmap before = Bitmap.createBitmap(drawingSurface.getBitmap());

		solo.clickOnScreen(width / 2, height / 2);
		solo.sleep(500);
		Bitmap after = drawingSurface.getBitmap();
		Bitmap edited = Bitmap.createBitmap(after);

		assertFalse(Arrays.equals(Utils.bitmapToPixelArray(before), Utils.bitmapToPixelArray(after)));

		solo.clickOnView(undo);
		after = drawingSurface.getBitmap();

		Assert.assertArrayEquals(Utils.bitmapToPixelArray(before), Utils.bitmapToPixelArray(after));

		solo.clickOnView(redo);
		after = drawingSurface.getBitmap();

		assertFalse(Arrays.equals(Utils.bitmapToPixelArray(before), Utils.bitmapToPixelArray(after)));
		Assert.assertArrayEquals(Utils.bitmapToPixelArray(edited), Utils.bitmapToPixelArray(after));

		before.recycle();
		edited.recycle();
	}

	/**
	 * Test if the file button shows the file manager activity.
	 */
	@Smoke
	public void testFileManagerButton() throws Exception {
		ImageButton button = (ImageButton) mainActivity.findViewById(R.id.ibtn_fileActivity);
		solo.clickOnView(button);
		Activity activity = solo.getCurrentActivity();
		assertTrue(activity instanceof FileActivity);
	}
}
