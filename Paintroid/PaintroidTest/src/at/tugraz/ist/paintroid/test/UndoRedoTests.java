package at.tugraz.ist.paintroid.test;

import java.util.ArrayList;
import java.util.Locale;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.dialog.DialogColorPicker;

import com.jayway.android.robotium.solo.Solo;


public class UndoRedoTests extends ActivityInstrumentationTestCase2<MainActivity>{
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
	final int REDO = 7;
	final int FILE = 8;
	
	final int STROKERECT = 0;
	final int STROKECIRLCE = 1;
	final int STROKE1 = 2;
	final int STROKE2 = 3;
	final int STROKE3 = 4;
	final int STROKE4 = 5;

	public UndoRedoTests() {
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
	}

	public void testUndoPath() throws Exception{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.drag(screenWidth/2-100, screenWidth/2+100, screenHeight/2-100, screenHeight/2+100, 20);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));
		
		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));
	}
	
	public void testUndoPoint() throws Exception{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		//Choosing color red
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
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(500);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.clickOnScreen(screenWidth/2, screenWidth/2);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));
		
		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));
	}
	
	public void testUndoMagicWand() throws Exception{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth/2, screenWidth/2);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));
		
		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));
	}
	
	public void testRedo() throws Exception{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.drag(screenWidth/2-100, screenWidth/2+100, screenHeight/2-100, screenHeight/2+100, 20);
		Bitmap testBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		//Check if undo worked
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap2));
		
		//Check if redo worked
		assertTrue(bitmapIsEqual(testBitmap, testBitmap3));
		
		//Check if something has been drawn on the picture
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap));
	}
	
	public void testUndoRedoPathPointAndWand() throws Exception{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.drag(screenWidth/2-100, screenWidth/2+100, screenHeight/2-100, screenHeight/2+100, 20);
		Bitmap testBitmap1 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnScreen(screenWidth/2, screenWidth/2+100);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth/2+100, screenWidth/2);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(BRUSH);
		
		//Choosing color red
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
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(500);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		solo.drag(screenWidth/2-100, screenWidth/2+100, screenHeight/2-100, screenHeight/2+100, 20);
		Bitmap testBitmap4 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap5 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap6 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap7 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap8 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap9 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap10 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap11 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap12 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap13 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap14 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		assertTrue(bitmapIsEqual(testBitmap3, testBitmap5));
		assertTrue(bitmapIsEqual(testBitmap2, testBitmap6));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap7));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap8));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap9));
		
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap10));
		assertTrue(bitmapIsEqual(testBitmap2, testBitmap11));
		assertTrue(bitmapIsEqual(testBitmap3, testBitmap12));
		assertTrue(bitmapIsEqual(testBitmap4, testBitmap13));
		assertTrue(bitmapIsEqual(testBitmap4, testBitmap14));
		
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap1));
		assertFalse(bitmapIsEqual(testBitmap1, testBitmap2));
		assertFalse(bitmapIsEqual(testBitmap2, testBitmap3));
		assertFalse(bitmapIsEqual(testBitmap3, testBitmap4));
	}
	
	public void testNoRedoAfterDraw() throws Exception{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.drag(screenWidth/2-100, screenWidth/2+100, screenHeight/2-100, screenHeight/2+100, 20);
		Bitmap testBitmap1 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.drag(screenWidth/2+100, screenWidth/2-100, screenHeight/2-100, screenHeight/2+100, 20);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap4 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap5 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnScreen(screenWidth/2+200, screenWidth/2+100);
		Bitmap testBitmap6 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap7 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap8 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap3));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap4));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap5));
		assertTrue(bitmapIsEqual(testBitmap6, testBitmap7));
		assertTrue(bitmapIsEqual(testBitmap5, testBitmap8));
		
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap1));
		assertFalse(bitmapIsEqual(testBitmap1, testBitmap2));
		assertFalse(bitmapIsEqual(testBitmap5, testBitmap6));
	}
	
	public void testIfCacheFilesAreDeleted() throws Exception
	{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		assertFalse(mainActivity.cachFilesExist());
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth/2, screenHeight/2);

		//Choosing color red
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
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(500);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2);	
		solo.clickOnImageButton(UNDO);
		solo.clickOnImageButton(UNDO);
		solo.clickOnImageButton(REDO);
		solo.clickOnImageButton(REDO);
		solo.clickOnImageButton(UNDO);
		mainActivity.deleteCachFiles();
		assertFalse(mainActivity.cachFilesExist());
	}
	
	public void testIfUndoRedoWorksIfCacheFilesAreMissing() throws Exception
	{
		mainActivity = (MainActivity) solo.getCurrentActivity();
		
		Bitmap initialBitmap = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		int screenWidth = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getWidth();
		int screenHeight = solo.getCurrentActivity().getWindowManager()
			.getDefaultDisplay().getHeight();
		solo.clickOnImageButton(WAND);
		solo.clickOnScreen(screenWidth/2, screenHeight/2);
		Thread.sleep(500);
		Bitmap testBitmap1 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		//Choosing color red
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
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+2, colorPickerViewCoordinates[1]+10);
		Thread.sleep(500);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+257, colorPickerViewCoordinates[1]+42);
		solo.clickLongOnScreen(colorPickerViewCoordinates[0]+20, colorPickerViewCoordinates[1]+340);
		assertEquals(String.valueOf(Color.RED), mainActivity.getCurrentSelectedColor());
		
		solo.clickOnScreen(screenWidth/2, screenHeight/2);	
		Thread.sleep(500);
		Bitmap testBitmap2 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap3 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap4 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap5 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap6 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap7 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		mainActivity.deleteCachFiles();
		assertFalse(mainActivity.cachFilesExist());
		
		solo.clickOnImageButton(REDO);
		Thread.sleep(500);
		Bitmap testBitmap8 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		solo.clickOnImageButton(UNDO);
		Thread.sleep(500);
		Bitmap testBitmap9 = mainActivity.getCurrentImage().copy(Bitmap.Config.ARGB_8888, false);
		
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap3));
		assertTrue(bitmapIsEqual(initialBitmap, testBitmap4));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap5));
		assertTrue(bitmapIsEqual(testBitmap2, testBitmap6));
		assertTrue(bitmapIsEqual(testBitmap1, testBitmap7));
		
		assertTrue(bitmapIsEqual(testBitmap7, testBitmap8));
		assertTrue(bitmapIsEqual(testBitmap7, testBitmap9));
		
		assertFalse(bitmapIsEqual(initialBitmap, testBitmap1));
		assertFalse(bitmapIsEqual(testBitmap1, testBitmap2));
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
	
	private boolean bitmapIsEqual(Bitmap bitmap1, Bitmap bitmap2)
	{
		for(int x = 0; x < bitmap1.getWidth(); x++)
		{
			for(int y = 0; y < bitmap1.getHeight(); y++)
			{
				if(bitmap1.getPixel(x, y) != bitmap2.getPixel(x, y))
				{
					return false;
				}
			}
		}
		return true;
	}
}
