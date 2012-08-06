package at.tugraz.ist.paintroid.test.integration;

import java.util.ArrayList;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TableRow;
import at.tugraz.ist.paintroid.R;

public class ColorDialogIntegrationTest extends BaseIntegrationTestClass {

	public ColorDialogIntegrationTest() throws Exception {
		super();
	}

	public void testStandardTabSelected() throws Throwable {
		mSolo.clickOnView(mToolBarButtonOne);
		TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
		assertEquals(tabhost.getCurrentTab(), 0);
		mSolo.sleep(2000);
		mSolo.goBack();
	}

	public void testTabsAreSelectable() throws Throwable {
		mSolo.clickOnView(mToolBarButtonOne);
		mSolo.sleep(1000);

		TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);

		// Substring to click on the text. Only 5 pattern because this length is
		// visible in the tab. Might need refactoring with other languages!
		String tabHsvName = mSolo.getString(R.string.color_hsv).substring(0, 5);
		String tabRgbName = mSolo.getString(R.string.color_rgb).substring(0, 5);

		mSolo.clickOnText(tabHsvName);
		assertEquals(tabhost.getCurrentTab(), 1);
		mSolo.sleep(500);

		mSolo.clickOnText(tabRgbName);
		assertEquals(tabhost.getCurrentTab(), 2);
		mSolo.sleep(500);
		mSolo.goBack();
	}

	public void testProgressBarsInRgbView() throws Throwable {
		mSolo.clickOnView(mToolBarButtonOne);
		mSolo.sleep(1000);

		TabHost tabhost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
		String tabRgbName = mSolo.getString(R.string.color_rgb).substring(0, 5);

		mSolo.clickOnText(tabRgbName);
		assertEquals(tabhost.getCurrentTab(), 2);
		mSolo.sleep(500);

		ArrayList<ProgressBar> progressBars = mSolo.getCurrentProgressBars();
		assertEquals(progressBars.size(), 4);

		SeekBar redBar = (SeekBar) progressBars.get(0);
		assertEquals(redBar.getProgress(), 0);
		mSolo.sleep(500);
		SeekBar greenBar = (SeekBar) progressBars.get(1);
		assertEquals(greenBar.getProgress(), 0);
		mSolo.sleep(500);
		SeekBar blueBar = (SeekBar) progressBars.get(2);
		assertEquals(blueBar.getProgress(), 0);
		mSolo.sleep(500);
		SeekBar alphaBar = (SeekBar) progressBars.get(3);
		assertEquals(alphaBar.getProgress(), alphaBar.getMax());
		mSolo.sleep(500);

		redBar.setProgress(redBar.getMax() / 2);
		assertEquals(redBar.getProgress(), redBar.getMax() / 2);
		mSolo.sleep(500);
		greenBar.setProgress(greenBar.getMax() / 2);
		assertEquals(greenBar.getProgress(), greenBar.getMax() / 2);
		mSolo.sleep(500);
		blueBar.setProgress(blueBar.getMax() / 2);
		assertEquals(blueBar.getProgress(), blueBar.getMax() / 2);
		mSolo.sleep(500);
		alphaBar.setProgress(alphaBar.getMax() / 2);
		assertEquals(alphaBar.getProgress(), alphaBar.getMax() / 2);
		mSolo.sleep(500);

		redBar.setProgress(redBar.getMax());
		assertEquals(redBar.getProgress(), redBar.getMax());
		mSolo.sleep(500);
		greenBar.setProgress(greenBar.getMax());
		assertEquals(greenBar.getProgress(), greenBar.getMax());
		mSolo.sleep(500);
		blueBar.setProgress(blueBar.getMax());
		assertEquals(blueBar.getProgress(), blueBar.getMax());
		mSolo.sleep(500);
		alphaBar.setProgress(alphaBar.getMax());
		assertEquals(alphaBar.getProgress(), alphaBar.getMax());
		mSolo.sleep(1000);
		mSolo.goBack();
		assertTrue("Waiting for Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.no));
	}

	public void testColorNewColorButtonChangesStandard() {
		mSolo.clickOnView(mToolBarButtonOne);
		mSolo.sleep(1500);

		TypedArray presetColors = mMainActivity.getResources().obtainTypedArray(R.array.preset_colors);
		for (int i = 0; i < presetColors.length(); i++) {
			Button colorButton = mSolo.getButton(i);
			if (!(colorButton.getParent() instanceof TableRow)) {
				break;
			}

			mSolo.clickOnButton(i);
			mSolo.sleep(50);
			int colorColor = presetColors.getColor(i, 0);

			String buttonNewColorName = mMainActivity.getResources().getString(R.string.color_new_color);
			Button button = mSolo.getButton(buttonNewColorName);
			Drawable drawable = button.getBackground();

			Bitmap bitmap = drawableToBitmap(drawable, button.getWidth(), button.getHeight());
			int buttonColor = bitmap.getPixel(1, 1);
			assertEquals("New Color button has unexpected color", colorColor, buttonColor);
		}

		mSolo.goBack();
		assertTrue("Waiting for Dialog", mSolo.waitForView(LinearLayout.class, 1, TIMEOUT));
		mSolo.clickOnButton(mSolo.getString(R.string.no));
	}

	public static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		int intrinsicWidth = width;
		int intrinsicHeight = height;
		Bitmap bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
}
