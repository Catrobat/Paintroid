package at.tugraz.ist.paintroid.test.integration;

import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.ui.Toolbar;

public class ColorDialogIntegrationTest extends BaseIntegrationTestClass {

	protected Toolbar mToolbar;

	public ColorDialogIntegrationTest() throws Exception {
		super();
	}

	@Override
	protected void setUp() {
		super.setUp();
		try {
			mToolbar = (Toolbar) PrivateAccess.getMemberValue(MainActivity.class, getActivity(), "mToolbar");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public void testStandardTabSelected() throws Throwable {
	// int expectedIndexTab = 0;
	//
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// mSolo.clickOnView(mButtonParameterTop2);
	// mSolo.sleep(20000);
	// TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
	// assertEquals("After opening Color Picker Dialog, First tab should be the preselected-tab",
	// tabhost.getCurrentTab(), expectedIndexTab);
	// mSolo.goBack();
	// }
	//
	// public void testTabsAreSelectable() throws Throwable {
	// int indexTabHsv = 1;
	// int indexTabRgb = 2;
	//
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// mSolo.clickOnView(mButtonParameterTop2);
	// mSolo.sleep(20000);
	//
	// TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
	//
	// // Substring to click on the text. Only 5 pattern because this length is
	// // visible in the tab. Might need refactoring with other languages!
	// String tabHsvName = mSolo.getString(R.string.color_hsv).substring(0, 5);
	// String tabRgbName = mSolo.getString(R.string.color_rgb).substring(0, 5);
	//
	// mSolo.clickOnText(tabHsvName);
	// assertEquals(tabhost.getCurrentTab(), indexTabHsv);
	// mSolo.sleep(500);
	//
	// mSolo.clickOnText(tabRgbName);
	// assertEquals(tabhost.getCurrentTab(), indexTabRgb);
	// mSolo.sleep(500);
	// mSolo.goBack();
	// }
	//
	// public void testColorNewColorButtonChangesStandard() {
	// int numberOfColorsToTest = 6;
	//
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// mSolo.clickOnView(mButtonParameterTop2);
	// mSolo.sleep(20000);
	//
	// TypedArray presetColors = mMainActivity.getResources().obtainTypedArray(R.array.preset_colors);
	//
	// if (numberOfColorsToTest > presetColors.length()) {
	// numberOfColorsToTest = presetColors.length();
	// }
	//
	// for (int counterColors = 0; counterColors < numberOfColorsToTest; counterColors++) {
	// Log.d(PaintroidApplication.TAG, "test color # " + counterColors);
	// Button colorButton = mSolo.getButton(counterColors);
	//
	// if (!(colorButton.getParent() instanceof TableRow)) {
	// Log.d(PaintroidApplication.TAG, "button parent is no table row: " + colorButton.getParent());
	// continue;
	// }
	//
	// mSolo.clickOnButton(counterColors);
	// mSolo.sleep(500);
	// int colorColor = presetColors.getColor(counterColors, 0);
	//
	// String buttonNewColorName = mMainActivity.getResources().getString(R.string.color_new_color);
	// Button button = mSolo.getButton(buttonNewColorName);
	// Drawable drawable = button.getBackground();
	//
	// Bitmap bitmap = drawableToBitmap(drawable, button.getWidth(), button.getHeight());
	// int buttonColor = bitmap.getPixel(1, 1);
	// assertEquals("New Color button has unexpected color", colorColor, buttonColor);
	// }
	//
	// mSolo.goBack();
	// assertTrue("Waiting for Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
	// mSolo.clickOnButton(mSolo.getString(R.string.no));
	// }
	//
	// public void testColorPickerDialogOnBackPressed() {
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// mSolo.clickOnView(mButtonParameterTop2);
	// mSolo.sleep(20000);
	// mSolo.goBack();
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	//
	// int oldColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
	// mSolo.clickOnView(mButtonParameterTop2);
	// mSolo.sleep(20000);
	//
	// TypedArray presetColors = mMainActivity.getResources().obtainTypedArray(R.array.preset_colors);
	//
	// mSolo.clickOnButton(presetColors.length() / 2);
	// mSolo.goBack();
	// assertTrue("Waiting for Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
	// mSolo.clickOnButton(mSolo.getString(R.string.yes));
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// int newColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
	// assertFalse("After choosing new color, color should not be the same as before", oldColor == newColor);
	//
	// oldColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
	// mSolo.clickOnView(mButtonParameterTop2);
	// mSolo.sleep(20000);
	//
	// mSolo.clickOnButton(presetColors.length() / 4);
	// mSolo.goBack();
	// assertTrue("Waiting for Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
	// mSolo.clickOnButton(mSolo.getString(R.string.no));
	// assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));
	// newColor = mToolbar.getCurrentTool().getDrawPaint().getColor();
	// assertTrue("After dropping chosen color, current color should be as color before", oldColor == newColor);
	// }
	//
	// public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
	// if (drawable instanceof BitmapDrawable) {
	// return ((BitmapDrawable) drawable).getBitmap();
	// }
	//
	// int intrinsicWidth = width;
	// int intrinsicHeight = height;
	// Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Config.ARGB_8888);
	// Canvas canvas = new Canvas(bitmap);
	// drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	// drawable.draw(canvas);
	//
	// return bitmap;
	// }
}
