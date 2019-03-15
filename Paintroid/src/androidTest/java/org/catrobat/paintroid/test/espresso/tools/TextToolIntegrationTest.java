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
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.TextTool;
import org.catrobat.paintroid.ui.Perspective;
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
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfacePointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

	private static final int TEXT_SIZE_30 = 30;

	private static final String TEXT_SIZE_20_STRING = "20px";
	private static final String TEXT_SIZE_30_STRING = "30px";
	private static final String TEXT_SIZE_40_STRING = "40px";
	private static final String TEXT_SIZE_60_STRING = "60px";

	private static final double EQUALS_DELTA = 0.25d;
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	private MainActivityHelper activityHelper;
	private TextTool textTool;
	private EditText textEditText;
	private Spinner fontSpinner;
	private ToggleButton underlinedToggleButton;
	private ToggleButton italicToggleButton;
	private ToggleButton boldToggleButton;
	private Spinner textSizeSpinner;
	private Perspective perspective;
	private LayerContracts.Model layerModel;
	private MainActivity activity;
	private ToolReference currentTool;

	@Before
	public void setUp() {
		activity = launchActivityRule.getActivity();
		activityHelper = new MainActivityHelper(activity);
		perspective = activity.perspective;
		layerModel = activity.layerModel;
		currentTool = activity.currentTool;

		onToolBarView()
				.performSelectTool(ToolType.TEXT);
		textTool = (TextTool) currentTool.get();

		textEditText = activity.findViewById(R.id.pocketpaint_text_tool_dialog_input_text);
		fontSpinner = activity.findViewById(R.id.pocketpaint_text_tool_dialog_spinner_font);
		underlinedToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined);
		italicToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic);
		boldToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold);
		textSizeSpinner = activity.findViewById(R.id.pocketpaint_text_tool_dialog_spinner_text_size);

		textTool.resetBoxPosition();
	}

	@Test
	public void testDialogDefaultValues() {
		onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
				.check(matches(withHint(R.string.text_tool_dialog_input_hint)))
				.check(matches(withText(textTool.text)));

		onView(withId(R.id.pocketpaint_text_tool_dialog_spinner_font))
				.check(matches(withSpinnerText(textTool.font)));

		onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_underlined))
				.check(matches(isNotChecked()));
		onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_bold))
				.check(matches(isNotChecked()));
		onView(withId(R.id.pocketpaint_text_tool_dialog_toggle_italic))
				.check(matches(isNotChecked()));

		assertFalse(textTool.underlined);
		assertFalse(textTool.italic);
		assertFalse(textTool.bold);

		onView(withId(R.id.pocketpaint_text_tool_dialog_spinner_text_size))
				.check(matches(withSpinnerText(TEXT_SIZE_20_STRING)));
	}

	@Test
	public void testDialogToolInteraction() {
		enterTestText();
		assertEquals(TEST_TEXT, textTool.text);

		selectFormatting(FormattingOptions.SERIF);
		assertEquals(FONT_SERIF, textTool.font);
		assertEquals(FONT_SERIF, fontSpinner.getSelectedItem());

		selectFormatting(FormattingOptions.UNDERLINE);
		assertTrue(textTool.underlined);
		assertTrue(underlinedToggleButton.isChecked());
		assertEquals(getFontString(FormattingOptions.UNDERLINE), underlinedToggleButton.getText().toString());
		selectFormatting(FormattingOptions.UNDERLINE);
		assertFalse(textTool.underlined);
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

		selectFormatting(FormattingOptions.SIZE_30);
		assertEquals("Tool member has wrong value for text size", TEXT_SIZE_30, getToolMemberTextSize());
		assertEquals("Wrong current item of text size spinner", TEXT_SIZE_30_STRING, textSizeSpinner.getSelectedItem());
	}

	@Test
	public void testDialogAndTextBoxAfterReopenDialog() {
		enterTestText();
		selectFormatting(FormattingOptions.SANS_SERIF);
		selectFormatting(FormattingOptions.UNDERLINE);
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);
		selectFormatting(FormattingOptions.SIZE_40);

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
		assertEquals("Wrong text size selected after reopen dialog",
				String.valueOf(TEXT_SIZE_40_STRING), textSizeSpinner.getSelectedItem());
	}

	@Test
	public void testStateRestoredAfterOrientationChange() {
		enterTestText();
		selectFormatting(FormattingOptions.SANS_SERIF);
		selectFormatting(FormattingOptions.UNDERLINE);
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);
		selectFormatting(FormattingOptions.SIZE_40);

		final PointF toolMemberBoxPosition = getToolMemberBoxPosition();
		PointF expectedPosition = new PointF(toolMemberBoxPosition.x, toolMemberBoxPosition.y);

		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		textTool = (TextTool) currentTool.get();

		assertEquals(TEST_TEXT, textEditText.getText().toString());
		assertEquals(FONT_SANS_SERIF, fontSpinner.getSelectedItem());
		assertTrue(underlinedToggleButton.isChecked());
		assertTrue(italicToggleButton.isChecked());
		assertTrue(boldToggleButton.isChecked());
		assertEquals("Wrong text size selected after reopen dialog", TEXT_SIZE_40_STRING, textSizeSpinner.getSelectedItem());

		assertEquals(expectedPosition, getToolMemberBoxPosition());
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
	public void testInputTextAndFormatForTextSize30() {
		enterTestText();

		float boxWidth = getToolMemberBoxWidth();
		float boxHeight = getToolMemberBoxHeight();
		selectFormatting(FormattingOptions.SIZE_30);
		checkTextBoxDimensions();
		assertTrue("Text box width should be larger with bigger text size", getToolMemberBoxWidth() > boxWidth);
		assertTrue("Text box height should be larger with bigger text size", getToolMemberBoxHeight() > boxHeight);
	}

	@Test
	public void testInputTextAndFormatForTextSize40() {
		enterTestText();

		float boxWidth = getToolMemberBoxWidth();
		float boxHeight = getToolMemberBoxHeight();
		selectFormatting(FormattingOptions.SIZE_40);
		checkTextBoxDimensions();
		assertTrue("Text box width should be larger with bigger text size", getToolMemberBoxWidth() > boxWidth);
		assertTrue("Text box height should be larger with bigger text size", getToolMemberBoxHeight() > boxHeight);
	}

	@Test
	public void testInputTextAndFormatForTextSize60() {
		enterTestText();

		float boxWidth = getToolMemberBoxWidth();
		float boxHeight = getToolMemberBoxHeight();
		selectFormatting(FormattingOptions.SIZE_60);
		checkTextBoxDimensions();
		assertTrue("Text box width should be larger with bigger text size", getToolMemberBoxWidth() > boxWidth);
		assertTrue("Text box height should be larger with bigger text size", getToolMemberBoxHeight() > boxHeight);
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
		PointF canvasPoint = perspective.getCanvasPointFromSurfacePoint(getSurfacePointFromScreenPoint(screenPoint));
		canvasPoint.x = (float) Math.round(canvasPoint.x);
		canvasPoint.y = (float) Math.round(canvasPoint.y);
		setToolMemberBoxPosition(canvasPoint);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION));

		int surfaceBitmapWidth = layerModel.getWidth();
		int[] pixelsDrawingSurface = new int[surfaceBitmapWidth];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals(numberOfBlackPixels, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));

		onTopBarView()
				.performUndo();

		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals(0, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));

		onTopBarView()
				.performRedo();

		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
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

		onToolProperties()
				.setColor(Color.WHITE);

		Paint paint = textTool.textPaint;
		int selectedColor = paint.getColor();
		assertEquals(Color.WHITE, selectedColor);
		Bitmap bitmap = getToolMemberDrawingBitmap();
		int[] pixels = new int[bitmap.getWidth()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
		assertEquals(countPixelsWithColor(pixels, Color.BLACK), 0);
		assertTrue(countPixelsWithColor(pixels, selectedColor) > 0);

		assertEquals(boxPositionX, getToolMemberBoxPosition().x, EQUALS_DELTA);
		assertEquals(boxPositionY, getToolMemberBoxPosition().y, EQUALS_DELTA);
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

		String stringTextSize = ((String) textSizeSpinner.getSelectedItem());
		stringTextSize = stringTextSize.substring(0, stringTextSize.indexOf("px"));
		float textSize = Float.valueOf(stringTextSize) * textSizeMagnificationFactor;

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

		float expectedBoxPositionX = layerModel.getWidth() / 2.0f;
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
		 * current IME does not understand how to translatePerspective the string into key events). As a
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
			case SIZE_20:
			case SIZE_30:
			case SIZE_40:
			case SIZE_60:
				onView(withId(R.id.pocketpaint_text_tool_dialog_spinner_text_size)).perform(click());
				onData(allOf(is(instanceOf(String.class)), is(getFontString(format))))
						.inRoot(isPlatformPopup())
						.perform(click());
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
				return activity.getString(R.string.text_tool_dialog_underline_shortcut);
			case ITALIC:
				return activity.getString(R.string.text_tool_dialog_italic_shortcut);
			case BOLD:
				return activity.getString(R.string.text_tool_dialog_bold_shortcut);
			case SIZE_20:
				return String.valueOf(TEXT_SIZE_20_STRING);
			case SIZE_30:
				return String.valueOf(TEXT_SIZE_30_STRING);
			case SIZE_40:
				return String.valueOf(TEXT_SIZE_40_STRING);
			case SIZE_60:
				return String.valueOf(TEXT_SIZE_60_STRING);

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

	private boolean getToolMemberItalic() {
		return textTool.italic;
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
		UNDERLINE, ITALIC, BOLD, MONOSPACE, SERIF, SANS_SERIF, STC, DUBAI, SIZE_20, SIZE_30, SIZE_40, SIZE_60
	}
}
