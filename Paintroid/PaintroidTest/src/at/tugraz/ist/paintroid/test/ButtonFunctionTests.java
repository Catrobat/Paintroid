package at.tugraz.ist.paintroid.test;

import java.util.ArrayList;

import android.graphics.Color;
import android.graphics.Paint.Cap;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;

import com.jayway.android.robotium.solo.Solo;


public class ButtonFunctionTests extends ActivityInstrumentationTestCase2<MainActivity>{
	private Solo solo;
	private MainActivity mainActivity;
	
	// Buttonindexes
	final int COLORPICKER = 0;
	final int STROKE = 0;
	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int FILE = 7;
	
	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;
	
	final int LICENSETEXT = 5;
	final int WARNINGTEXT = 1;
	
	final String aboutTitleText = "About Paintroid...";
	final String licenseText = "Catroid: An on-device graphical " +
			"programming language for Android devices Copyright " +
			"(C) 2010 Catroid development team " +
			"(<http://code.google.com/p/catroid/wiki/Credits>)\n\n" +
			"This program is free software: you can redistribute " +
			"it and/or modify it under the terms of the GNU General " +
			"Public License as published by the Free Software " +
			"Foundation, either version 3 of the License, or (at " +
			"your option) any later version.\n\nThis program is " +
			"distributed in the hope that it will be useful, " +
			"but WITHOUT ANY WARRANTY; without even the implied " +
			"warranty of MERCHANTABILITY or FITNESS FOR A " +
			"PARTICULAR PURPOSE. See the GNU General Public " +
			"License for more details.\n\nYou should have " +
			"received a copy of the GNU General Public License " +
			"along with this program. If not, see " +
			"<http://www.gnu.org/licenses/>.";
	final String warningTitleText = "Warning!!!!";
	final String warningText = "Please open a drawing first by clicking on New Drawing or Load in the main menu";

	public ButtonFunctionTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Smoke
	/**
	 * Check if Buttons change their background when they have been clicked
	 * 
	 */
	public void testChangedButtonBackground() throws Exception {
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		
		solo.clickOnImageButton(MAGNIFIY);
		assertEquals(mainActivity.getImageButtonBackground(MAGNIFIY),solo.getImageButton(MAGNIFIY).getContext());
		solo.clickOnImageButton(HAND);
		assertEquals(mainActivity.getImageButtonBackground(HAND),solo.getImageButton(HAND).getContext());
		solo.clickOnImageButton(BRUSH);
		assertEquals(mainActivity.getImageButtonBackground(BRUSH),solo.getImageButton(BRUSH).getContext());
		solo.clickOnImageButton(EYEDROPPER);
		assertEquals(mainActivity.getImageButtonBackground(EYEDROPPER),solo.getImageButton(EYEDROPPER).getContext());
		solo.clickOnImageButton(WAND);
		assertEquals(mainActivity.getImageButtonBackground(WAND),solo.getImageButton(WAND).getContext());
		
	}
	
	/**
	 * Test if the color picker sets the correct color
	 * 
	 */
	public void testColorPicker() throws Exception{
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		ArrayList<View> actual_views = solo.getViews();
		View colorPickerView = null;
		for (View view : actual_views) {
			if(view instanceof DialogColorPicker.ColorPickerView)
			{
				colorPickerView = view;
			}
		}
		assertNotNull(colorPickerView);
		int[] colorPickerViewCoordinates = new int[2];
		colorPickerView.getLocationOnScreen(colorPickerViewCoordinates);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+100);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
	
		assertEquals("-3819337", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+50, colorPickerViewCoordinates[1]+160);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-7769489", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+250, colorPickerViewCoordinates[1]+200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-10415870", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.WHITE), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-61696", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+297);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.BLACK), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+297);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.BLACK), mainActivity.getCurrentSelectedColor());
		
		// Change hue
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+60, colorPickerViewCoordinates[1]+10);
		// Wait till hue is changed
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+100);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-4147259", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+50, colorPickerViewCoordinates[1]+160);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-8360055", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+250, colorPickerViewCoordinates[1]+200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-12647839", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.WHITE), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+60, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-5963521", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+297);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.BLACK), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+60, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+297);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.BLACK), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-61696", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals("-58624", mainActivity.getCurrentSelectedColor());
		
		solo.clickOnButton(COLORPICKER);
		solo.waitForView(DialogColorPicker.ColorPickerView.class, 1, 200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+123, colorPickerViewCoordinates[1]+10);
		Thread.sleep(200);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+145, colorPickerViewCoordinates[1]+33);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+200, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.TRANSPARENT), mainActivity.getCurrentSelectedColor());
		
	}
	
	/**
	 * Test stroke and shape picker
	 * 
	 */
	public void testBrushShape() throws Exception{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKECIRLCE);
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE1);
		solo.waitForDialogToClose(100);
		assertEquals(1, mainActivity.getCurrentBrushWidth());
		assertEquals(Cap.ROUND, mainActivity.getCurrentBrush());
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE3);
		solo.waitForDialogToClose(100);
		assertEquals(15, mainActivity.getCurrentBrushWidth());
		assertEquals(Cap.ROUND, mainActivity.getCurrentBrush());
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKERECT);
		solo.waitForDialogToClose(100);
		assertEquals(15, mainActivity.getCurrentBrushWidth());
		assertEquals(Cap.SQUARE, mainActivity.getCurrentBrush());
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE3);
		solo.waitForDialogToClose(100);
		assertEquals(15, mainActivity.getCurrentBrushWidth());
		assertEquals(Cap.SQUARE, mainActivity.getCurrentBrush());
		
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKECIRLCE);
		solo.clickOnImageButton(STROKE);
		solo.clickOnImageButton(STROKE4);
		solo.waitForDialogToClose(100);
		assertEquals(25, mainActivity.getCurrentBrushWidth());
		assertEquals(Cap.ROUND, mainActivity.getCurrentBrush());
		
	}

	/**
	 * Tests if there is a new Bitmap created
	 * 
	 */
	public void testNewDrawing() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		assertNotNull(mainActivity.getCurrentImage());
		
	}
	

	/**
	 * Tests if the Bitmap(DrawingSurface) is now cleared
	 * 
	 */
	public void testClearDrawing() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.clickOnMenuItem("Clear Drawing");
		if(mainActivity.getCurrentImage() != null)
		{
			assertNull(mainActivity.getCurrentImage().getNinePatchChunk());
		}
	}

	/**
	 * Tests if reset of ZoomValue works
	 * 
	 */
	public void testResetZoom() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(MAGNIFIY);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.drag(66, 500, 700, 55, 100);

		assertFalse(mainActivity.getZoomLevel().equals(String.valueOf(1.0)));
		
		solo.clickOnMenuItem("Reset Zoom");
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		assertEquals(mainActivity.getZoomLevel(), String.valueOf(1.0));
	}
	
	public void testScroll() throws Exception{
		
	}
	
	/**
	 * Tests if Zooming works
	 * 
	 */
	public void testZoom() throws Exception{
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		solo.clickOnImageButton(MAGNIFIY);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		solo.drag(66, 500, 700, 55, 100);

		assertFalse(mainActivity.getZoomLevel().equals(String.valueOf(1.0)));
	}
	
	/**
	 * Tests if the about dialog is present
	 * @throws Exception
	 */
	public void testAbout() throws Exception{
		solo.clickOnMenuItem("About");
		assertTrue(solo.waitForText(aboutTitleText, 1, 300));
		solo.clickOnButton("Cancel");
		assertFalse(solo.waitForText(aboutTitleText, 1, 300));
	}
	
	/**
	 * Tests if the license dialog is present
	 * @throws Exception
	 */
	public void testGpl() throws Exception{
		solo.clickOnMenuItem("About");
		assertTrue(solo.waitForText(aboutTitleText, 1, 300));
		solo.clickOnButton("License");
		assertEquals(licenseText, solo.getText(LICENSETEXT).getText());
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
}
