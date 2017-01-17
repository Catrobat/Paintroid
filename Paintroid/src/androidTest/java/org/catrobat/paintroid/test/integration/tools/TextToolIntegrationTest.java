/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.ToggleButton;

import com.jaredrummler.materialspinner.MaterialSpinner;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.TextTool;
import org.catrobat.paintroid.ui.TopBar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class TextToolIntegrationTest extends BaseIntegrationTestClass {
	private static final String TEST_TEXT = "testing 123";
	private static final String TEST_TEXT_MULTILINE = "testing\nmultiline\ntext\n\n123";
	private static final String FONT_MONOSPACE = "Monospace";
	private static final String FONT_SERIF = "Serif";
	private static final String FONT_SANS_SERIF = "Sans Serif";
	private static final int TEXT_SIZE_20 = 20;
	private static final int TEXT_SIZE_30 = 30;
	private static final int TEXT_SIZE_40 = 40;
	private static final int TEXT_SIZE_60 = 60;
	private static final int SLEEP_WAIT_FOR_DIALOG_UPDATE_AND_LISTENER = 250;
	private static final int SLEEP_WAIT_FOR_EXECUTING_COMMAND = 250;

	private TextTool mTextTool;
	private EditText mTextEditText;
	private MaterialSpinner mFontSpinner;
	private ToggleButton mUnderlinedToggleButton;
	private ToggleButton mItalicToggleButton;
	private ToggleButton mBoldToggleButton;
	private MaterialSpinner mTextSizeSpinner;

	private enum FormattingOptions {
		UNDERLINE, ITALIC, BOLD, MONOSPACE, SERIF, SANS_SERIF, SIZE_20, SIZE_30, SIZE_40, SIZE_60
	}

	public TextToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testDialogKeyboardTextBoxAppearanceOnStartup() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();

		assertTrue("Text input should be focused", mTextEditText.hasFocus());

		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		assertTrue("Soft keyboard should be visible", imm.isAcceptingText());

		checkTextBoxDimensionsAndDefaultPosition();
	}

	@Test
	public void testDialogDefaultValues() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();

		String expectedHintText = mSolo.getString(R.string.text_tool_dialog_input_hint);
		String actualHintText = mTextEditText.getHint().toString();
		assertEquals("Wrong input hint text", expectedHintText, actualHintText);

		String expectedText = getToolMemberText();
		String actualText = mTextEditText.getText().toString();
		assertEquals("Wrong default input text", expectedText, actualText);

		String expectedFont = getToolMemberFont();
		String actualFont = getSelectedItemFromMaterialSpinner(mFontSpinner);
		assertEquals("Wrong default font selected", expectedFont, actualFont);

		boolean expectedUnderlined = getToolMemberUnderlined();
		boolean actualUnderlined = mUnderlinedToggleButton.isChecked();
		assertEquals("Wrong checked status of underline button", expectedUnderlined, actualUnderlined);

		boolean expectedItalic = getToolMemberItalic();
		boolean actualItalic = mItalicToggleButton.isChecked();
		assertEquals("Wrong checked status of italic button", expectedItalic, actualItalic);

		boolean expectedBold = getToolMemberBold();
		boolean actualBold = mBoldToggleButton.isChecked();
		assertEquals("Wrong checked status of bold button", expectedBold, actualBold);

		int expectedTextSize = getToolMemberTextSize();
		int actualTextSize = Integer.valueOf(getSelectedItemFromMaterialSpinner(mTextSizeSpinner));
		assertEquals("Wrong text size selected", expectedTextSize, actualTextSize);
	}

	@Test
	public void testDialogToolInteraction() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();

		enterTestText();
		assertEquals("Wrong input text", TEST_TEXT, getToolMemberText());

		selectFormatting(FormattingOptions.SERIF);
		assertEquals("Tool member has wrong value for font", FONT_SERIF, getToolMemberFont());
		assertEquals("Wrong current item of font spinner", FONT_SERIF, getSelectedItemFromMaterialSpinner(mFontSpinner));

		selectFormatting(FormattingOptions.UNDERLINE);
		assertTrue("Tool member value for underlined should be true", getToolMemberUnderlined());
		assertTrue("Toggle button for underline should be pressed", mUnderlinedToggleButton.isChecked());
		assertEquals("Wrong text for toggle button underline",
				getFontString(FormattingOptions.UNDERLINE), mUnderlinedToggleButton.getText().toString());
		selectFormatting(FormattingOptions.UNDERLINE);
		assertFalse("Tool member value for underlined should be false", getToolMemberUnderlined());
		assertFalse("Toggle button for underline should not be pressed", mUnderlinedToggleButton.isChecked());
		assertEquals("Wrong text for toggle button underline",
				getFontString(FormattingOptions.UNDERLINE), mUnderlinedToggleButton.getText().toString());

		selectFormatting(FormattingOptions.ITALIC);
		assertTrue("Tool member value for italic should be true", getToolMemberItalic());
		assertTrue("Toggle button for italic should be pressed", mItalicToggleButton.isChecked());
		assertEquals("Wrong text for toggle button italic",
				getFontString(FormattingOptions.ITALIC), mItalicToggleButton.getText().toString());
		selectFormatting(FormattingOptions.ITALIC);
		assertFalse("Tool member value for italic should be false", getToolMemberItalic());
		assertFalse("Toggle button for italic should not be pressed", mItalicToggleButton.isChecked());
		assertEquals("Wrong text for toggle button italic",
				getFontString(FormattingOptions.ITALIC), mItalicToggleButton.getText().toString());

		selectFormatting(FormattingOptions.BOLD);
		assertTrue("Tool member value for bold should be true", getToolMemberBold());
		assertTrue("Toggle button for bold should be pressed", mBoldToggleButton.isChecked());
		assertEquals("Wrong text for toggle button bold",
				getFontString(FormattingOptions.BOLD), mBoldToggleButton.getText().toString());
		selectFormatting(FormattingOptions.BOLD);
		assertFalse("Tool member value for bold should be false", getToolMemberBold());
		assertFalse("Toggle button for bold should not be pressed", mBoldToggleButton.isChecked());
		assertEquals("Wrong text for toggle button bold",
				getFontString(FormattingOptions.BOLD), mBoldToggleButton.getText().toString());

		selectFormatting(FormattingOptions.SIZE_30);
		assertEquals("Tool member has wrong value for text size", TEXT_SIZE_30, getToolMemberTextSize());
		assertEquals("Wrong current item of text size spinner",
				String.valueOf(TEXT_SIZE_30), getSelectedItemFromMaterialSpinner(mTextSizeSpinner));
	}

	@Test
	public void testDialogAndTextBoxAfterReopenDialog() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();

		enterTestText();
		selectFormatting(FormattingOptions.SANS_SERIF);
		selectFormatting(FormattingOptions.UNDERLINE);
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);
		selectFormatting(FormattingOptions.SIZE_40);

		closeToolOptionsForCurrentTool();

		PointF boxPosition = getToolMemberBoxPosition();
		PointF newBoxPosition = new PointF(boxPosition.x + 20, boxPosition.y + 20);
		setToolMemberBoxPosition(newBoxPosition);
		setToolMemberBoxHeight(50.0f);
		setToolMemberBoxWidth(50.0f);

		openToolOptionsForCurrentTool();
		mSolo.sleep(SLEEP_WAIT_FOR_DIALOG_UPDATE_AND_LISTENER);
		assertEquals("Wrong input text after reopen dialog", TEST_TEXT, mTextEditText.getText().toString());
		assertEquals("Wrong font selected after reopen dialog", FONT_SANS_SERIF, getSelectedItemFromMaterialSpinner(mFontSpinner));
		assertEquals("Wrong underline status after reopen dialog", true, mUnderlinedToggleButton.isChecked());
		assertEquals("Wrong italic status after reopen dialog", true, mItalicToggleButton.isChecked());
		assertEquals("Wrong bold status after reopen dialog", true, mBoldToggleButton.isChecked());
		assertEquals("Wrong text size selected after reopen dialog",
				String.valueOf(TEXT_SIZE_40), getSelectedItemFromMaterialSpinner(mTextSizeSpinner));
		checkTextBoxDimensions();
		assertEquals("Wrong text box position after reopen dialog", newBoxPosition, getToolMemberBoxPosition());
	}

	@Test
	public void testCheckBoxSizeAndContentAfterFormatting() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();
		enterTestText();

		assertFalse("Underline button should not be pressed", mUnderlinedToggleButton.isChecked());
		assertFalse("Italic button should not be pressed", mUnderlinedToggleButton.isChecked());
		assertFalse("Bold button should not be pressed", mUnderlinedToggleButton.isChecked());

		ArrayList<FormattingOptions> fonts = new ArrayList<FormattingOptions>();
		fonts.add(FormattingOptions.SERIF);
		fonts.add(FormattingOptions.SANS_SERIF);
		fonts.add(FormattingOptions.MONOSPACE);

		for (FormattingOptions font : fonts) {
			float boxWidth = getToolMemberBoxWidth();
			float boxHeight = getToolMemberBoxHeight();
			int[] pixelsBefore, pixelsAfter;

			selectFormatting(font);
			checkTextBoxDimensionsAndDefaultPosition();
			assertFalse("Box size should have changed",
					boxWidth == getToolMemberBoxWidth() && boxHeight == getToolMemberBoxHeight());

			Bitmap bitmap = getToolMemberDrawingBitmap();
			pixelsBefore = new int[bitmap.getHeight()];
			bitmap.getPixels(pixelsBefore, 0, 1, bitmap.getWidth() / 2, 0, 1, bitmap.getHeight());
			selectFormatting(FormattingOptions.UNDERLINE);
			assertTrue("Underline button should be pressed", mUnderlinedToggleButton.isChecked());
			bitmap = getToolMemberDrawingBitmap();
			pixelsAfter = new int[bitmap.getHeight()];
			bitmap.getPixels(pixelsAfter, 0, 1, bitmap.getWidth() / 2, 0, 1, bitmap.getHeight());
			assertTrue("Number of black Pixels should be higher when text is underlined",
					countPixelsWithColor(pixelsAfter, Color.BLACK) > countPixelsWithColor(pixelsBefore, Color.BLACK));


			boxWidth = getToolMemberBoxWidth();
			selectFormatting(FormattingOptions.ITALIC);
			assertTrue("Italic button should be pressed", mUnderlinedToggleButton.isChecked());
			if (font != FormattingOptions.MONOSPACE) {
				assertTrue("Text box width should be smaller when text is italic", getToolMemberBoxWidth() < boxWidth);
			} else {
				assertEquals("Wrong value of tool member italic", true, getToolMemberItalic());
			}

			pixelsBefore = new int[bitmap.getWidth()];
			bitmap.getPixels(pixelsBefore, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
			selectFormatting(FormattingOptions.BOLD);
			assertTrue("Bold button should be pressed", mUnderlinedToggleButton.isChecked());
			bitmap = getToolMemberDrawingBitmap();
			pixelsAfter = new int[bitmap.getWidth()];
			bitmap.getPixels(pixelsAfter, 0, bitmap.getWidth(), 0, bitmap.getHeight() / 2, bitmap.getWidth(), 1);
			assertTrue("Number of black Pixels should be higher when text is bold",
					countPixelsWithColor(pixelsAfter, Color.BLACK) > countPixelsWithColor(pixelsBefore, Color.BLACK));

			selectFormatting(FormattingOptions.UNDERLINE);
			assertFalse("Underline button should not be pressed", mUnderlinedToggleButton.isChecked());
			selectFormatting(FormattingOptions.ITALIC);
			assertFalse("Italic button should not be pressed", mUnderlinedToggleButton.isChecked());
			selectFormatting(FormattingOptions.BOLD);
			assertFalse("Bold button should not be pressed", mUnderlinedToggleButton.isChecked());
		}
	}

	@Test
	public void testInputTextAndFormatByTextSize() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();
		enterTestText();

		ArrayList<FormattingOptions> sizes = new ArrayList<FormattingOptions>();
		sizes.add(FormattingOptions.SIZE_30);
		sizes.add(FormattingOptions.SIZE_40);
		sizes.add(FormattingOptions.SIZE_60);

		for (FormattingOptions size : sizes) {
			float boxWidth = getToolMemberBoxWidth();
			float boxHeight = getToolMemberBoxHeight();
			selectFormatting(size);
			checkTextBoxDimensions();
			assertTrue("Text box width should be larger with bigger text size", getToolMemberBoxWidth() > boxWidth);
			assertTrue("Text box height should be larger with bigger text size", getToolMemberBoxHeight() > boxHeight);
		}
	}

	@Test
	public void testCommandUndoAndRedo() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();
		enterMultilineTestText();

		closeToolOptionsForCurrentTool();

		Bitmap bitmap = getToolMemberDrawingBitmap();
		int[] pixelsTool = new int[bitmap.getWidth()];
		int yPos = Math.round(bitmap.getHeight()/2.0f);
		bitmap.getPixels(pixelsTool, 0, bitmap.getWidth(), 0, yPos, bitmap.getWidth(), 1);
		int numberOfBlackPixels = countPixelsWithColor(pixelsTool, Color.BLACK);

		PointF screenPoint = new PointF(mScreenWidth/2.0f, mScreenHeight/2.0f);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);
		canvasPoint.x = (float) Math.round(canvasPoint.x);
		canvasPoint.y = (float) Math.round(canvasPoint.y);
		setToolMemberBoxPosition(canvasPoint);

		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SLEEP_WAIT_FOR_EXECUTING_COMMAND);
		mSolo.goBack();

		int surfaceBitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		int[] pixelsDrawingSurface = new int[surfaceBitmapWidth];
		PaintroidApplication.drawingSurface.getPixels(
				pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals("Amount of black pixels should be the same when drawing",
				numberOfBlackPixels, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));

		ImageButton undoButton = (ImageButton) getActivity().findViewById(R.id.btn_top_undo);
		mSolo.clickOnView(undoButton);
		assertTrue("Progress dialog did not close", mSolo.waitForDialogToClose());
		PaintroidApplication.drawingSurface.getPixels(
				pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals("There should not be black pixels after undo",
				0, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));

		ImageButton redoButton = (ImageButton) getActivity().findViewById(R.id.btn_top_redo);
		mSolo.clickOnView(redoButton);
		assertTrue("Progress dialog did not close", mSolo.waitForDialogToClose());
		PaintroidApplication.drawingSurface.getPixels(
				pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals("There should be black pixels again after redo",
				numberOfBlackPixels, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));
	}

	@Test
	public void testChangeTextColor() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();
		enterTestText();
		closeToolOptionsForCurrentTool();

		float newBoxWidth = getToolMemberBoxWidth()*1.5f;
		float newBoxHeight = getToolMemberBoxHeight()*1.5f;
		setToolMemberBoxWidth(newBoxWidth);
		setToolMemberBoxHeight(newBoxHeight);

		float boxPositionX = getToolMemberBoxPosition().x;
		float boxPositionY = getToolMemberBoxPosition().y;

		openColorChooserDialog();
		assertTrue("Color picker dialog should open", mSolo.waitForDialogToOpen());
		Button colorButton = mSolo.getButton(5);
		assertTrue(colorButton.getParent() instanceof TableRow);
		mSolo.clickOnButton(5);
		mSolo.sleep(SLEEP_WAIT_FOR_DIALOG_UPDATE_AND_LISTENER);
		closeColorChooserDialog();

		Paint paint = (Paint) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mTextPaint");
		int selectedColor = paint.getColor();
		assertFalse("Paint color should not be black", selectedColor == Color.BLACK);
		Bitmap bitmap = getToolMemberDrawingBitmap();
		int[] pixels = new int[bitmap.getWidth()];
		bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, bitmap.getHeight()/2, bitmap.getWidth(), 1);
		assertTrue("There should not be any black pixels", countPixelsWithColor(pixels, Color.BLACK) == 0);
		assertTrue("There should be some pixels with the selected color", countPixelsWithColor(pixels, selectedColor) > 0);

		assertEquals("Text box position x should stay the same after color change", boxPositionX, getToolMemberBoxPosition().x);
		assertEquals("Text box position y should stay the same after color change", boxPositionY, getToolMemberBoxPosition().y);
	}

	@Test
	public void testMultiLineText() throws NoSuchFieldException, IllegalAccessException {
		selectTextTool();
		enterMultilineTestText();

		closeToolOptionsForCurrentTool();

		String expectedTextSplitUp[] = { "testing", "multiline", "text", "", "123" };
		String actualTextSplitUp[] = getToolMemberMultilineText();
		assertTrue("Splitting text by newline failed", Arrays.equals(expectedTextSplitUp, actualTextSplitUp));
		checkTextBoxDimensionsAndDefaultPosition();
	}

	private void checkTextBoxDimensions() throws NoSuchFieldException, IllegalAccessException {
		int boxOffset = (Integer) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mBoxOffset");
		int textSizeMagnificationFactor = (Integer) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mTextSizeMagnificationFactor");
		float actualBoxWidth = getToolMemberBoxWidth();
		float actualBoxHeight = getToolMemberBoxHeight();

		boolean italic = mItalicToggleButton.isChecked();
		String font = getSelectedItemFromMaterialSpinner(mFontSpinner);
		float textSize = Float.valueOf(getSelectedItemFromMaterialSpinner(mTextSizeSpinner)) * textSizeMagnificationFactor;
		Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);

		textPaint.setTextSize(textSize);

		int style;
		if (italic) {
			style = Typeface.ITALIC;
		} else {
			style = Typeface.NORMAL;
		}

		if (font.equals("Sans Serif")) {
			textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, style));
		} else if (font.equals("Serif")) {
			textPaint.setTypeface(Typeface.create(Typeface.SERIF, style));
		} else {
			textPaint.setTypeface(Typeface.create(Typeface.MONOSPACE, style));
		}

		float textDescent = textPaint.descent();
		float textAscent = textPaint.ascent();

		String multilineText[] = getToolMemberMultilineText();

		float maxTextWidth = 0;
		for (String str : multilineText) {
			float textWidth = textPaint.measureText(str);
			if (textWidth > maxTextWidth) {
				maxTextWidth = textWidth;
			}
		}
		float expectedBoxWidth = maxTextWidth + 2*boxOffset;

		float textHeight = textDescent - textAscent;
		float expectedBoxHeight = textHeight * multilineText.length + 2*boxOffset;

		assertEquals("Wrong text box width", expectedBoxWidth, actualBoxWidth);
		assertEquals("Wrong text box height", expectedBoxHeight, actualBoxHeight);
	}

	private void checkTextBoxDefaultPosition() throws NoSuchFieldException, IllegalAccessException {
		float marginTop = (Float) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mMarginTop");
		PointF actualBoxPosition = getToolMemberBoxPosition();
		float boxHeight = getToolMemberBoxHeight();
		float expectedBoxPositionX = PaintroidApplication.drawingSurface.getBitmapWidth()/2.0f;
		float expectedBoxPositionY = boxHeight/2.0f + marginTop;
		assertEquals("Wrong text box x position", expectedBoxPositionX, actualBoxPosition.x);
		assertEquals("Wrong text box y position", expectedBoxPositionY, actualBoxPosition.y);
	}

	private void checkTextBoxDimensionsAndDefaultPosition () throws NoSuchFieldException, IllegalAccessException {
		checkTextBoxDimensions();
		checkTextBoxDefaultPosition();
	}

	private void selectTextTool() {
		selectTool(ToolType.TEXT);
		assertTrue("TextTool options should be shown", toolOptionsAreShown());

		mTextTool = (TextTool) PaintroidApplication.currentTool;

		mTextEditText = (EditText)mSolo.getView(R.id.text_tool_dialog_input_text);
		mFontSpinner = (MaterialSpinner)mSolo.getView(R.id.text_tool_dialog_spinner_font);
		mUnderlinedToggleButton = (ToggleButton)mSolo.getView(R.id.text_tool_dialog_toggle_underlined);
		mItalicToggleButton = (ToggleButton)mSolo.getView(R.id.text_tool_dialog_toggle_italic);
		mBoldToggleButton = (ToggleButton)mSolo.getView(R.id.text_tool_dialog_toggle_bold);
		mTextSizeSpinner = (MaterialSpinner)mSolo.getView(R.id.text_tool_dialog_spinner_text_size);
	}

	private void enterTestText() {
		getInstrumentation().sendStringSync(TEST_TEXT);
		mSolo.sleep(SHORT_SLEEP);
		assertEquals("Writing test text did not work", TEST_TEXT, mTextEditText.getText().toString());
	}

	private void enterMultilineTestText() {
		getInstrumentation().sendStringSync(TEST_TEXT_MULTILINE);
		mSolo.sleep(SHORT_SLEEP);
		assertEquals("Writing test text did not work", TEST_TEXT_MULTILINE, mTextEditText.getText().toString());
	}

	private void selectFormatting(FormattingOptions format) {
		switch (format) {
			case MONOSPACE:
			case SERIF:
			case SANS_SERIF:
				mSolo.clickOnView(mFontSpinner);
				mSolo.clickOnMenuItem(getFontString(format), true);
				break;
			case UNDERLINE:
			case ITALIC:
			case BOLD:
				mSolo.clickOnToggleButton(getFontString(format));
				break;
			case SIZE_20:
			case SIZE_30:
			case SIZE_40:
			case SIZE_60:
				mSolo.clickOnView(mTextSizeSpinner);
				mSolo.clickOnMenuItem(getFontString(format), true);
				break;
		}
		mSolo.sleep(SLEEP_WAIT_FOR_DIALOG_UPDATE_AND_LISTENER);
	}

	private String getFontString(FormattingOptions format) {
		switch (format) {
			case MONOSPACE:
				return FONT_MONOSPACE;
			case SERIF:
				return FONT_SERIF;
			case SANS_SERIF:
				return FONT_SANS_SERIF;
			case UNDERLINE:
				return mSolo.getString(R.string.text_tool_dialog_underline_shortcut);
			case ITALIC:
				return mSolo.getString(R.string.text_tool_dialog_italic_shortcut);
			case BOLD:
				return mSolo.getString(R.string.text_tool_dialog_bold_shortcut);
			case SIZE_20:
				return String.valueOf(TEXT_SIZE_20);
			case SIZE_30:
				return String.valueOf(TEXT_SIZE_30);
			case SIZE_40:
				return String.valueOf(TEXT_SIZE_40);
			case SIZE_60:
				return String.valueOf(TEXT_SIZE_60);

			default:
				return null;
		}
	}

	protected int countPixelsWithColor(int[] pixels, int color) {
		int count = 0;
		for (int pixel : pixels) {
			if (pixel == color) {
				count++;
			}
		}
		return count;
	}

	protected float getToolMemberBoxWidth() throws NoSuchFieldException, IllegalAccessException {
		return (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mTextTool, "mBoxWidth");
	}

	protected float getToolMemberBoxHeight() throws NoSuchFieldException, IllegalAccessException {
		return (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mTextTool, "mBoxHeight");
	}

	protected PointF getToolMemberBoxPosition() throws NoSuchFieldException, IllegalAccessException {
		return (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mTextTool, "mToolPosition");
	}

	protected String getToolMemberText() throws NoSuchFieldException, IllegalAccessException {
		return (String) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mText");
	}

	protected String getToolMemberFont() throws NoSuchFieldException, IllegalAccessException {
		return (String) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mFont");
	}

	protected boolean getToolMemberItalic() throws NoSuchFieldException, IllegalAccessException {
		return (Boolean) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mItalic");
	}

	protected boolean getToolMemberUnderlined() throws NoSuchFieldException, IllegalAccessException {
		return (Boolean) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mUnderlined");
	}

	protected boolean getToolMemberBold() throws NoSuchFieldException, IllegalAccessException {
		return (Boolean) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mBold");
	}

	protected int getToolMemberTextSize() throws NoSuchFieldException, IllegalAccessException {
		return (Integer) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mTextSize");
	}

	protected Bitmap getToolMemberDrawingBitmap() throws NoSuchFieldException, IllegalAccessException {
		return (Bitmap) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mTextTool, "mDrawingBitmap");
	}

	protected String[] getToolMemberMultilineText() throws NoSuchFieldException, IllegalAccessException {
		return (String[]) PrivateAccess.getMemberValue(TextTool.class, mTextTool, "mMultilineText");
	}

	protected void setToolMemberBoxPosition(PointF position) throws NoSuchFieldException, IllegalAccessException {
		PrivateAccess.setMemberValue(BaseToolWithShape.class, mTextTool, "mToolPosition", position);
	}

	protected void setToolMemberBoxHeight(float height) throws NoSuchFieldException, IllegalAccessException {
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, mTextTool, "mBoxHeight", height);
	}
	protected void setToolMemberBoxWidth(float width) throws NoSuchFieldException, IllegalAccessException {
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, mTextTool, "mBoxWidth", width);
	}

	protected String getSelectedItemFromMaterialSpinner(MaterialSpinner materialSpinner) {
		return materialSpinner.getItems().get(materialSpinner.getSelectedIndex()).toString();
	}
}