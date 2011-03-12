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

import java.util.ArrayList;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;
import at.tugraz.ist.paintroid.graphic.DrawingSurface;
import at.tugraz.ist.paintroid.graphic.utilities.Tool.ToolState;

import com.jayway.android.robotium.solo.Solo;

public class CursorTests extends ActivityInstrumentationTestCase2<MainActivity> {

	private Solo solo;
	private MainActivity mainActivity;
	private int screenWidth;
	private int screenHeight;

	// Buttonindexes
	final int COLORPICKER = 0;
	final int STROKE = 0;
	final int HAND = 1;
	final int MAGNIFIY = 2;
	final int BRUSH = 3;
	final int EYEDROPPER = 4;
	final int WAND = 5;
	final int UNDO = 6;
	final int REDO = 7;
	final int FILE = 8;
	
	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;
	
	final int CursorStateINACTIVE = 0;
	final int CursorStateACTIVE = 1;
	final int CursorStateDRAW = 2;

	public CursorTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);

	}

	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
		String languageToLoad_before  = "en";
		Locale locale_before = new Locale(languageToLoad_before);
		Locale.setDefault(locale_before);
		
		Configuration config_before = new Configuration();
		config_before.locale = locale_before;
		
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.getBaseContext().getResources().updateConfiguration(config_before, mainActivity.getBaseContext().getResources().getDisplayMetrics());
		
		screenWidth = solo.getCurrentActivity().getWindowManager()
		.getDefaultDisplay().getWidth();
		screenHeight = solo.getCurrentActivity().getWindowManager()
		.getDefaultDisplay().getHeight();
	}
	
	public void testCursorStates() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		solo.clickOnImageButton(BRUSH);
		// double tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		solo.drag(screenWidth/2, screenWidth/2+1, screenHeight/2, screenHeight/2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, mainActivity.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, mainActivity.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, mainActivity.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		solo.drag(screenWidth/2, screenWidth/2+1, screenHeight/2, screenHeight/2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.INACTIVE, mainActivity.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		Thread.sleep(400);
		assertEquals(ToolState.INACTIVE, mainActivity.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		solo.drag(screenWidth/2, screenWidth/2+1, screenHeight/2, screenHeight/2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, mainActivity.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, mainActivity.getToolState());
		// double tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		solo.drag(screenWidth/2, screenWidth/2+1, screenHeight/2, screenHeight/2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.INACTIVE, mainActivity.getToolState());
		
	}
	
	public void testCursorDraw() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		solo.clickOnImageButton(BRUSH);
		// double tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		solo.drag(screenWidth/2, screenWidth/2+1, screenHeight/2, screenHeight/2, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, mainActivity.getToolState());
		// single tap
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, mainActivity.getToolState());
		
		int testPixel1 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
		int testPixel2 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+30, coordinatesOfLastClick[1]);
		
		assertEquals(mainActivity.getCurrentSelectedColor(), String.valueOf(testPixel1));
		assertEquals(Color.WHITE, testPixel2);
		
	}
	
	public void testCursorDrawPath() throws Exception {
		solo.clickOnImageButton(FILE);
		solo.clickOnButton("New Drawing");
		assertTrue(solo.waitForActivity("MainActivity", 500));
		mainActivity = (MainActivity) solo.getCurrentActivity();
		mainActivity.setAntiAliasing(false);
		solo.clickOnImageButton(BRUSH);
		
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
		solo.clickOnScreen(colorPickerViewCoordinates[0]+145, colorPickerViewCoordinates[1]+33);
		solo.clickOnScreen(colorPickerViewCoordinates[0]+200, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.TRANSPARENT), mainActivity.getCurrentSelectedColor());
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
		  .getDefaultDisplay().getWidth();
		
		ImageButton strokePickerButton = solo.getImageButton(STROKE);
		int[] locationstrokePickerButton = new int[2];
		strokePickerButton.getLocationOnScreen(locationstrokePickerButton);
		locationstrokePickerButton[0] += strokePickerButton.getMeasuredWidth();
		ImageButton handButton = solo.getImageButton(HAND);
		int[] locationHandButton = new int[2];
		handButton.getLocationOnScreen(locationHandButton);
		locationHandButton[1] -= handButton.getMeasuredHeight();
		
		actual_views = solo.getViews();
		View surfaceView = null;
		for (View view : actual_views) {
			if(view instanceof DrawingSurface)
			{
				surfaceView = view;
			}
		}
		assertNotNull(surfaceView);
		int[] coords = new int[2];
		surfaceView.getLocationOnScreen(coords);
		
		float min_x = locationstrokePickerButton[0];
		float min_y = coords[1];
		float max_x = screenWidth;
		float max_y = locationHandButton[1];
		// double tap
		solo.clickOnScreen(min_x, min_y);
		float[] coordinatesOfFirstClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfFirstClick);
		solo.drag(min_x, min_x+1, min_y, min_y, 50);
		Thread.sleep(400);
		assertEquals(ToolState.ACTIVE, mainActivity.getToolState());
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		Thread.sleep(400);
		assertEquals(ToolState.DRAW, mainActivity.getToolState());
		Thread.sleep(500);
		solo.drag(min_x, max_x, min_y, max_y, 50);
		float[] coordinatesOfLastClick = new float[2];
		mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
		
		//Change coordinates to real clicked ones
		min_x = coordinatesOfFirstClick[0];
		max_x = coordinatesOfLastClick[0];
		min_y = coordinatesOfFirstClick[1];
		max_y = coordinatesOfLastClick[1];
		
		float ratioYX = ((float)max_y-(float)min_y)/((float)max_x-(float)min_x);
		
		mainActivity = (MainActivity) solo.getCurrentActivity();

		int testPixel1 = mainActivity.getPixelFromScreenCoordinates(min_x+20, min_y+Math.round(20*ratioYX));
		int testPixel2 = mainActivity.getPixelFromScreenCoordinates(max_x-20, max_y-Math.round(20*ratioYX));
		int testPixel3 = mainActivity.getPixelFromScreenCoordinates(min_x+(max_x-min_x)/2, min_y+Math.round((max_x-min_x)/2*ratioYX));
		int testPixel4 = mainActivity.getPixelFromScreenCoordinates(min_x+20, min_y+max_y/2);
		int testPixel5 = mainActivity.getPixelFromScreenCoordinates(min_x+max_x/2, min_y+Math.round(20*ratioYX));

		assertEquals(testPixel1, Color.TRANSPARENT);
		assertEquals(testPixel2, Color.TRANSPARENT);
		assertEquals(testPixel3, Color.TRANSPARENT);
		assertNotSame(testPixel4, Color.TRANSPARENT);
		assertNotSame(testPixel5, Color.TRANSPARENT);
		
	}
	
	public void testCursorDrawAfterPaintChange() throws Exception {
    solo.clickOnImageButton(FILE);
    solo.clickOnButton("New Drawing");
    assertTrue(solo.waitForActivity("MainActivity", 500));
    mainActivity = (MainActivity) solo.getCurrentActivity();
    mainActivity.setAntiAliasing(false);
    solo.clickOnImageButton(BRUSH);
    solo.clickOnImageButton(STROKE);
    solo.clickOnImageButton(STROKECIRLCE);
    solo.clickOnImageButton(STROKE);
    solo.clickOnImageButton(STROKE3);
    // double tap
    solo.clickOnScreen(screenWidth/2, screenHeight/2);
    solo.drag(screenWidth/2, screenWidth/2+1, screenHeight/2, screenHeight/2, 50);
    Thread.sleep(400);
    assertEquals(ToolState.ACTIVE, mainActivity.getToolState());
    // single tap
    solo.clickOnScreen(screenWidth/2, screenHeight/2);
    float[] coordinatesOfLastClick = new float[2];
    mainActivity.getDrawingSurfaceListener().getLastClickCoordinates(coordinatesOfLastClick);
    Thread.sleep(400);
    assertEquals(ToolState.DRAW, mainActivity.getToolState());
    
    int testPixel1 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
    int testPixel2 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+30, coordinatesOfLastClick[1]);
    assertEquals(String.valueOf(Color.BLACK), mainActivity.getCurrentSelectedColor());
    assertEquals(mainActivity.getCurrentSelectedColor(), String.valueOf(testPixel1));
    assertEquals(Color.WHITE, testPixel2);
    
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
    solo.clickOnScreen(colorPickerViewCoordinates[0]+145, colorPickerViewCoordinates[1]+33);
    solo.clickOnScreen(colorPickerViewCoordinates[0]+200, colorPickerViewCoordinates[1]+340);
    Thread.sleep(500);
    assertEquals(String.valueOf(Color.TRANSPARENT), mainActivity.getCurrentSelectedColor());
    
    int testPixel3 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0], coordinatesOfLastClick[1]);
    assertEquals(mainActivity.getCurrentSelectedColor(), String.valueOf(testPixel3));
    
    int strokeWidth = mainActivity.getCurrentBrushWidth();
    
    int testPixel4 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+strokeWidth*3/4, coordinatesOfLastClick[1]+strokeWidth*3/4);
    assertEquals(Color.WHITE, testPixel4);
    
    solo.clickOnImageButton(STROKE);
    solo.clickOnImageButton(STROKERECT);
    Thread.sleep(500);
    int testPixel5 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+strokeWidth*3/4, coordinatesOfLastClick[1]+strokeWidth*3/4);
    assertEquals(mainActivity.getCurrentSelectedColor(), String.valueOf(testPixel5));
    
    int testPixel6 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+strokeWidth*3/4+1, coordinatesOfLastClick[1]+strokeWidth*3/4+1);
    assertEquals(Color.WHITE, testPixel6);
    
    solo.clickOnImageButton(STROKE);
    solo.clickOnImageButton(STROKE4);
    Thread.sleep(500);
    int testPixel7 = mainActivity.getPixelFromScreenCoordinates(coordinatesOfLastClick[0]+strokeWidth*3/4+1, coordinatesOfLastClick[1]+strokeWidth*3/4+1);
    assertEquals(mainActivity.getCurrentSelectedColor(), String.valueOf(testPixel7));
    
  }

	@Override
	public void tearDown() throws Exception {
	  solo.clickOnMenuItem("Quit");
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

}