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

package org.catrobat.paintroid.test.espresso.tools;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.TextTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.hasFocus;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.BLACK_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TextToolIntegrationTest {
	private static final String TEST_TEXT = "testing 123";
	private static final String TEST_ARABIC_TEXT = "السلام عليكم 123";
	private static final String TEST_TEXT_MULTILINE = "testing\nmultiline\ntext\n\n123";

	private static final String FONT_MONOSPACE = "Monospace";
	private static final String FONT_SERIF = "Serif";
	private static final String FONT_SANS_SERIF = "Sans Serif";
	private static final String FONT_STC = "STC";
	private static final String FONT_DUBAI = "Dubai";

	private static final int DEFAULT_TEXT_SIZE_20 = 20;

	private static final double EQUALS_DELTA = 0.25d;
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	private ActivityHelper activityHelper;
	private TextTool textTool;
	private EditText textEditText;
	private Spinner fontSpinner;
	private ToggleButton underlinedToggleButton;
	private ToggleButton italicToggleButton;
	private ToggleButton boldToggleButton;

	@Before
	public void setUp() {

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		resetColorPicker();
		resetDrawPaintAndBrushPickerView();

		onToolBarView()
				.performSelectTool(ToolType.TEXT);
		textTool = (TextTool) PaintroidApplication.currentTool;

		textEditText = (EditText) activityHelper.findViewById(R.id.pocketpaint_text_tool_dialog_input_text);
		fontSpinner = (Spinner) activityHelper.findViewById(R.id.pocketpaint_text_tool_dialog_spinner_font);
		underlinedToggleButton = (ToggleButton) activityHelper.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined);
		italicToggleButton = (ToggleButton) activityHelper.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic);
		boldToggleButton = (ToggleButton) activityHelper.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold);

		textTool.resetBoxPosition();
	}

	@After
	public void tearDown() {
		activityHelper = null;
	}

	@Test
	public void testDialogKeyboardTextBoxAppearanceOnStartup() {
		onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).check(matches(hasFocus()));
		checkTextBoxDimensionsAndDefaultPosition();
	}

	@Test
	public void testDialogDefaultValues() {
		String expectedHintText = activityHelper.getString(R.string.text_tool_dialog_input_hint);
		String actualHintText = textEditText.getHint().toString();
		assertEquals(expectedHintText, actualHintText);

		String expectedText = getToolMemberText();
		String actualText = textEditText.getText().toString();
		assertEquals(expectedText, actualText);

		String expectedFont = getToolMemberFont();
		String actualFont = (String) fontSpinner.getSelectedItem();
		assertEquals(expectedFont, actualFont);

		boolean expectedUnderlined = getToolMemberUnderlined();
		boolean actualUnderlined = underlinedToggleButton.isChecked();
		assertEquals(expectedUnderlined, actualUnderlined);

		boolean expectedItalic = getToolMemberItalic();
		boolean actualItalic = italicToggleButton.isChecked();
		assertEquals(expectedItalic, actualItalic);

		boolean expectedBold = getToolMemberBold();
		boolean actualBold = boldToggleButton.isChecked();
		assertEquals(expectedBold, actualBold);

		int expectedTextSize = getToolMemberTextSize();
		assertEquals(expectedTextSize, DEFAULT_TEXT_SIZE_20);
	}

	@Test
	public void testDialogToolInteraction() {
		enterTestText();
		assertEquals(TEST_TEXT, getToolMemberText());

		selectFormatting(FormattingOptions.SERIF);
		assertEquals(FONT_SERIF, getToolMemberFont());
		assertEquals(FONT_SERIF, fontSpinner.getSelectedItem());

		selectFormatting(FormattingOptions.UNDERLINE);
		assertTrue(getToolMemberUnderlined());
		assertTrue(underlinedToggleButton.isChecked());
		assertEquals(getFontString(FormattingOptions.UNDERLINE), underlinedToggleButton.getText().toString());
		selectFormatting(FormattingOptions.UNDERLINE);
		assertFalse(getToolMemberUnderlined());
		assertFalse(underlinedToggleButton.isChecked());
		assertEquals(getFontString(FormattingOptions.UNDERLINE), underlinedToggleButton.getText().toString());

		selectFormatting(FormattingOptions.ITALIC);
		assertTrue(getToolMemberItalic());
		assertTrue(italicToggleButton.isChecked());
		assertEquals(getFontString(FormattingOptions.ITALIC), italicToggleButton.getText().toString());

		selectFormatting(FormattingOptions.ITALIC);
		assertFalse(getToolMemberItalic());
		assertFalse(italicToggleButton.isChecked());
		assertEquals(getFontString(FormattingOptions.ITALIC), italicToggleButton.getText().toString());

		selectFormatting(FormattingOptions.BOLD);
		assertTrue(getToolMemberBold());
		assertTrue(boldToggleButton.isChecked());
		assertEquals(getFontString(FormattingOptions.BOLD), boldToggleButton.getText().toString());

		selectFormatting(FormattingOptions.BOLD);
		assertFalse(getToolMemberBold());
		assertFalse(boldToggleButton.isChecked());
		assertEquals(getFontString(FormattingOptions.BOLD), boldToggleButton.getText().toString());
	}

	@Test
	public void testDialogAndTextBoxAfterReopenDialog() {
		enterTestText();
		selectFormatting(FormattingOptions.SANS_SERIF);
		selectFormatting(FormattingOptions.UNDERLINE);
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);

		onToolBarView()
				.performCloseToolOptions();

		PointF boxPosition = getToolMemberBoxPosition();
		PointF newBoxPosition = new PointF(boxPosition.x + 20, boxPosition.y + 20);
		setToolMemberBoxPosition(newBoxPosition);
		setToolMemberBoxHeight(50.0f);
		setToolMemberBoxWidth(50.0f);

		onToolBarView()
				.performOpenToolOptions();

		assertEquals(TEST_TEXT, textEditText.getText().toString());
		assertEquals(FONT_SANS_SERIF, fontSpinner.getSelectedItem());
		assertTrue(underlinedToggleButton.isChecked());
		assertTrue(italicToggleButton.isChecked());
		assertTrue(boldToggleButton.isChecked());
		checkTextBoxDimensions();
	}

	@Test
	public void testStateRestoredAfterOrientationChange() {
		enterTestText();
		selectFormatting(FormattingOptions.SANS_SERIF);
		selectFormatting(FormattingOptions.UNDERLINE);
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);

		final PointF toolMemberBoxPosition = getToolMemberBoxPosition();
		PointF expectedPosition = new PointF(toolMemberBoxPosition.x, toolMemberBoxPosition.y);

		launchActivityRule.getActivity()
				.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		textTool = (TextTool) PaintroidApplication.currentTool;

		assertEquals(TEST_TEXT, textEditText.getText().toString());
		assertEquals(FONT_SANS_SERIF, fontSpinner.getSelectedItem());
		assertTrue(underlinedToggleButton.isChecked());
		assertTrue(italicToggleButton.isChecked());
		assertTrue(boldToggleButton.isChecked());

		assertEquals(expectedPosition, getToolMemberBoxPosition());
		checkTextBoxDimensions();
	}

	@Test
	public void testCheckBoxSizeAndContentAfterFormatting() {
		enterTestText();

		assertFalse(underlinedToggleButton.isChecked());
		assertFalse(underlinedToggleButton.isChecked());
		assertFalse(underlinedToggleButton.isChecked());

		ArrayList<FormattingOptions> fonts = new ArrayList<>();
		fonts.add(FormattingOptions.SERIF);
		fonts.add(FormattingOptions.SANS_SERIF);
		fonts.add(FormattingOptions.MONOSPACE);

		for (FormattingOptions font : fonts) {
			float boxWidth = getToolMemberBoxWidth();
			float boxHeight = getToolMemberBoxHeight();
			int[] pixelsBefore;
			int[] pixelsAfter;

			selectFormatting(font);
			checkTextBoxDimensionsAndDefaultPosition();
			assertFalse(boxWidth == getToolMemberBoxWidth() && boxHeight == getToolMemberBoxHeight());

			Bitmap bitmap = getToolMemberDrawingBitmap();
			pixelsBefore = new int[bitmap.getHeight()];
			bitmap.getPixels(pixelsBefore, 0, 1, bitmap.getWidth() / 2, 0, 1, bitmap.getHeight());
			selectFormatting(FormattingOptions.UNDERLINE);
			assertTrue(underlinedToggleButton.isChecked());
			bitmap = getToolMemberDrawingBitmap();
			pixelsAfter = new int[bitmap.getHeight()];
			bitmap.getPixels(pixelsAfter, 0, 1, bitmap.getWidth() / 2, 0, 1, bitmap.getHeight());
			assertTrue(countPixelsWithColor(pixelsAfter, Color.BLACK) > countPixelsWithColor(pixelsBefore, Color.BLACK));

			boxWidth = getToolMemberBoxWidth();
			selectFormatting(FormattingOptions.ITALIC);
			assertTrue(underlinedToggleButton.isChecked());
			if (font != FormattingOptions.MONOSPACE) {
				assertTrue(getToolMemberBoxWidth() < boxWidth);
			} else {
				assertTrue(getToolMemberItalic());
			}

			pixelsBefore = new int[bitmap.getWidth()];
			bitmap.getPixels(pixelsBefore, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
			selectFormatting(FormattingOptions.BOLD);
			assertTrue(underlinedToggleButton.isChecked());
			bitmap = getToolMemberDrawingBitmap();
			pixelsAfter = new int[bitmap.getWidth()];
			bitmap.getPixels(pixelsAfter, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
			assertTrue(countPixelsWithColor(pixelsAfter, Color.BLACK) > countPixelsWithColor(pixelsBefore, Color.BLACK));

			selectFormatting(FormattingOptions.UNDERLINE);
			assertFalse(underlinedToggleButton.isChecked());
			selectFormatting(FormattingOptions.ITALIC);
			assertFalse(underlinedToggleButton.isChecked());
			selectFormatting(FormattingOptions.BOLD);
			assertFalse(underlinedToggleButton.isChecked());
		}
	}

	@Test
	public void testCheckBoxSizeAndContentAfterFormattingToDubaiAndStc() {
		enterArabicTestText();

		assertFalse(underlinedToggleButton.isChecked());
		assertFalse(underlinedToggleButton.isChecked());
		assertFalse(underlinedToggleButton.isChecked());

		List<FormattingOptions> fonts = Arrays.asList(FormattingOptions.STC, FormattingOptions.DUBAI);

		for (FormattingOptions font : fonts) {
			float boxWidth = getToolMemberBoxWidth();
			float boxHeight = getToolMemberBoxHeight();
			int[] pixelsBefore;
			int[] pixelsAfter;

			selectFormatting(font);
			checkTextBoxDimensionsAndDefaultPosition();
			assertFalse(boxWidth == getToolMemberBoxWidth() && boxHeight == getToolMemberBoxHeight());

			Bitmap bitmap = getToolMemberDrawingBitmap();
			pixelsBefore = new int[bitmap.getHeight()];
			bitmap.getPixels(pixelsBefore, 0, 1, bitmap.getWidth() / 2, 0, 1, bitmap.getHeight());

			selectFormatting(FormattingOptions.UNDERLINE);
			assertTrue(underlinedToggleButton.isChecked());
			bitmap = getToolMemberDrawingBitmap();
			pixelsAfter = new int[bitmap.getHeight()];
			bitmap.getPixels(pixelsAfter, 0, 1, bitmap.getWidth() / 2, 0, 1, bitmap.getHeight());
			assertTrue(countPixelsWithColor(pixelsAfter, Color.BLACK) > countPixelsWithColor(pixelsBefore, Color.BLACK));

			boxWidth = getToolMemberBoxWidth();
			selectFormatting(FormattingOptions.ITALIC);
			assertTrue(underlinedToggleButton.isChecked());
			if (font != FormattingOptions.DUBAI) {
				assertEquals(getToolMemberBoxWidth(), boxWidth, Float.MIN_VALUE);
			} else {
				assertTrue(getToolMemberItalic());
			}

			pixelsBefore = new int[bitmap.getWidth()];
			bitmap.getPixels(pixelsBefore, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
			selectFormatting(FormattingOptions.BOLD);
			assertTrue(underlinedToggleButton.isChecked());
			bitmap = getToolMemberDrawingBitmap();
			pixelsAfter = new int[bitmap.getWidth()];
			bitmap.getPixels(pixelsAfter, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
			assertTrue(countPixelsWithColor(pixelsAfter, Color.BLACK) > countPixelsWithColor(pixelsBefore, Color.BLACK));

			selectFormatting(FormattingOptions.UNDERLINE);
			assertFalse(underlinedToggleButton.isChecked());
			selectFormatting(FormattingOptions.ITALIC);
			assertFalse(underlinedToggleButton.isChecked());
			selectFormatting(FormattingOptions.BOLD);
			assertFalse(underlinedToggleButton.isChecked());
		}
	}

	@Test
	public void testCommandUndoAndRedo() {
		enterMultilineTestText();

		onToolBarView()
				.performCloseToolOptions();

		Bitmap bitmap = getToolMemberDrawingBitmap();
		int[] pixelsTool = new int[bitmap.getWidth()];
		int yPos = Math.round(bitmap.getHeight() / 2.0f);
		bitmap.getPixels(pixelsTool, 0, bitmap.getWidth(), 0, yPos, bitmap.getWidth(), 1);
		int numberOfBlackPixels = countPixelsWithColor(pixelsTool, Color.BLACK);

		PointF screenPoint = new PointF(activityHelper.getDisplayWidth() / 2.0f, activityHelper.getDisplayHeight() / 2.0f);
		PointF canvasPoint = getCanvasPointFromScreenPoint(screenPoint);
		canvasPoint.x = (float) Math.round(canvasPoint.x);
		canvasPoint.y = (float) Math.round(canvasPoint.y);
		setToolMemberBoxPosition(canvasPoint);

		onView(isRoot()).perform(touchAt(screenPoint));

		int surfaceBitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		int[] pixelsDrawingSurface = new int[surfaceBitmapWidth];
		PaintroidApplication.layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals(numberOfBlackPixels, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));

		onTopBarView()
				.performUndo();

		PaintroidApplication.layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals(0, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));

		onTopBarView()
				.performRedo();

		PaintroidApplication.layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals(numberOfBlackPixels, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));
	}

	@Test
	public void testChangeTextColor() {
		enterTestText();

		onToolBarView()
				.performCloseToolOptions();

		float newBoxWidth = getToolMemberBoxWidth() * 1.5f;
		float newBoxHeight = getToolMemberBoxHeight() * 1.5f;
		setToolMemberBoxWidth(newBoxWidth);
		setToolMemberBoxHeight(newBoxHeight);

		float boxPositionX = getToolMemberBoxPosition().x;
		float boxPositionY = getToolMemberBoxPosition().y;

		selectColorPickerPresetSelectorColor(5);

		Paint paint = textTool.textPaint;
		int selectedColor = paint.getColor();
		assertNotEquals(selectedColor, Color.BLACK);
		Bitmap bitmap = getToolMemberDrawingBitmap();
		int[] pixels = new int[bitmap.getWidth()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
		assertEquals(countPixelsWithColor(pixels, Color.BLACK), 0);
		assertTrue(countPixelsWithColor(pixels, selectedColor) > 0);

		assertEquals(boxPositionX, getToolMemberBoxPosition().x, EQUALS_DELTA);
		assertEquals(boxPositionY, getToolMemberBoxPosition().y, EQUALS_DELTA);

		selectColorPickerPresetSelectorColor(BLACK_COLOR_PICKER_BUTTON_POSITION);
	}

	@Test
	public void testChangeToolFromEraser() {

		int color = textTool.textPaint.getColor();

		onToolBarView()
				.performSelectTool(ToolType.ERASER)
				.performSelectTool(ToolType.TEXT);

		int newColor = textTool.textPaint.getColor();

		assertEquals(color, Color.BLACK);
		assertEquals(color, newColor);
	}

	@Test
	public void testMultiLineText() {
		enterMultilineTestText();

		onToolBarView()
				.performCloseToolOptions();

		String[] expectedTextSplitUp = {"testing", "multiline", "text", "", "123"};
		String[] actualTextSplitUp = getToolMemberMultilineText();

		assertArrayEquals(expectedTextSplitUp, actualTextSplitUp);

		checkTextBoxDimensionsAndDefaultPosition();
	}

	private void checkTextBoxDimensions() {
		int boxOffset = TextTool.BOX_OFFSET;
		int textSizeMagnificationFactor = TextTool.TEXT_SIZE_MAGNIFICATION_FACTOR;

		float actualBoxWidth = getToolMemberBoxWidth();
		float actualBoxHeight = getToolMemberBoxHeight();

		boolean italic = italicToggleButton.isChecked();

		String font = (String) fontSpinner.getSelectedItem();
		float textSize = (float) DEFAULT_TEXT_SIZE_20 * textSizeMagnificationFactor;
		Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);

		int style = italic ? Typeface.ITALIC : Typeface.NORMAL;

		switch (font) {
			case FONT_SANS_SERIF:
				textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, style));
				break;
			case FONT_SERIF:
				textPaint.setTypeface(Typeface.create(Typeface.SERIF, style));
				break;
			case FONT_STC:
				textPaint.setTypeface(ResourcesCompat.getFont(launchActivityRule.getActivity(), R.font.stc_regular));
				break;
			case FONT_DUBAI:
				textPaint.setTypeface(ResourcesCompat.getFont(launchActivityRule.getActivity(), R.font.dubai));
				break;
			default:
				textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, style));
				break;
		}

		float textDescent = textPaint.descent();
		float textAscent = textPaint.ascent();

		String[] multilineText = getToolMemberMultilineText();

		float maxTextWidth = 0;
		for (String str : multilineText) {
			float textWidth = textPaint.measureText(str);
			if (textWidth > maxTextWidth) {
				maxTextWidth = textWidth;
			}
		}
		float expectedBoxWidth = maxTextWidth + 2 * boxOffset;

		float textHeight = textDescent - textAscent;
		float expectedBoxHeight = textHeight * multilineText.length + 2 * boxOffset;

		assertEquals(expectedBoxWidth, actualBoxWidth, EQUALS_DELTA);
		assertEquals(expectedBoxHeight, actualBoxHeight, EQUALS_DELTA);
	}

	private void checkTextBoxDefaultPosition() {
		float marginTop = TextTool.MARGIN_TOP;
		PointF actualBoxPosition = getToolMemberBoxPosition();
		float boxHeight = getToolMemberBoxHeight();

		float expectedBoxPositionX = PaintroidApplication.drawingSurface.getBitmapWidth() / 2.0f;
		float expectedBoxPositionY = boxHeight / 2.0f + marginTop;

		assertEquals(expectedBoxPositionX, actualBoxPosition.x, EQUALS_DELTA);
		assertEquals(expectedBoxPositionY, actualBoxPosition.y, EQUALS_DELTA);
	}

	private void checkTextBoxDimensionsAndDefaultPosition() {
		checkTextBoxDimensions();
		checkTextBoxDefaultPosition();
	}

	private void enterTextInput(final String textToEnter) {
		/*
		 * Use replaceText instead of typeText to support the arabic input.
		 *
		 * See:
		 * java.ic_pocketpaint_menu_language.RuntimeException: Failed to get key events for string السلام عليكم 123 (i.e.
		 * current IME does not understand how to translate the string into key events). As a
		 * workaround, you can use replaceText action to set the text directly in the EditText field.
		 */
		onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).perform(replaceText(textToEnter));
		Espresso.closeSoftKeyboard();
		onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).check(matches(withText(textToEnter)));
	}

	private void enterTestText() {
		enterTextInput(TEST_TEXT);
	}

	private void enterArabicTestText() {
		enterTextInput(TEST_ARABIC_TEXT);
	}

	private void enterMultilineTestText() {
		enterTextInput(TEST_TEXT_MULTILINE);
	}

	private void selectFormatting(FormattingOptions format) {
		switch (format) {
			case MONOSPACE:
			case SERIF:
			case SANS_SERIF:
			case STC:
			case DUBAI:
				onView(withId(R.id.pocketpaint_text_tool_dialog_spinner_font)).perform(click());
				onData(allOf(is(instanceOf(String.class)), is(getFontString(format))))
						.inRoot(isPlatformPopup())
						.perform(click());
				break;
			case UNDERLINE:
			case ITALIC:
			case BOLD:
				onView(withText(getFontString(format))).perform(click());
				break;
			default:
				fail("Formatting option not supported.");
		}
	}

	private String getFontString(FormattingOptions format) {
		switch (format) {
			case MONOSPACE:
				return FONT_MONOSPACE;
			case SERIF:
				return FONT_SERIF;
			case SANS_SERIF:
				return FONT_SANS_SERIF;
			case STC:
				return FONT_STC;
			case DUBAI:
				return FONT_DUBAI;
			case UNDERLINE:
				return activityHelper.getString(R.string.text_tool_dialog_underline_shortcut);
			case ITALIC:
				return activityHelper.getString(R.string.text_tool_dialog_italic_shortcut);
			case BOLD:
				return activityHelper.getString(R.string.text_tool_dialog_bold_shortcut);

			default:
				return null;
		}
	}

	private int countPixelsWithColor(int[] pixels, int color) {
		int count = 0;
		for (int pixel : pixels) {
			if (pixel == color) {
				count++;
			}
		}
		return count;
	}

	private float getToolMemberBoxWidth() {
		return textTool.boxWidth;
	}

	private void setToolMemberBoxWidth(float width) {
		textTool.boxWidth = width;
	}

	private float getToolMemberBoxHeight() {
		return textTool.boxHeight;
	}

	private void setToolMemberBoxHeight(float height) {
		textTool.boxHeight = height;
	}

	private PointF getToolMemberBoxPosition() {
		return textTool.toolPosition;
	}

	private void setToolMemberBoxPosition(PointF position) {
		textTool.toolPosition.set(position);
	}

	private String getToolMemberText() {
		return textTool.text;
	}

	private String getToolMemberFont() {
		return textTool.font;
	}

	private boolean getToolMemberItalic() {
		return textTool.italic;
	}

	private boolean getToolMemberUnderlined() {
		return textTool.underlined;
	}

	private boolean getToolMemberBold() {
		return textTool.bold;
	}

	private int getToolMemberTextSize() {
		return textTool.textSize;
	}

	private Bitmap getToolMemberDrawingBitmap() {
		return textTool.drawingBitmap;
	}

	private String[] getToolMemberMultilineText() {
		return textTool.getMultilineText();
	}

	private enum FormattingOptions {
		UNDERLINE, ITALIC, BOLD, MONOSPACE, SERIF, SANS_SERIF, STC, DUBAI
	}
}
