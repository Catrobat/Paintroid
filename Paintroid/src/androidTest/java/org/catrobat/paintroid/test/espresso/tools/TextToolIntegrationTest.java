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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.FontType;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.TextTool;
import org.catrobat.paintroid.ui.tools.FontListAdapter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.atPosition;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.catrobat.paintroid.tools.implementation.TextToolKt.BOX_OFFSET;
import static org.catrobat.paintroid.tools.implementation.TextToolKt.MARGIN_TOP;
import static org.catrobat.paintroid.tools.implementation.TextToolKt.TEXT_SIZE_MAGNIFICATION_FACTOR;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TextToolIntegrationTest {
	private static final String TEST_TEXT = "123 www 123";
	private static final String TEST_TEXT_ADVANCED = "testing 123 new";
	private static final String TEST_TEXT_MULTILINE = "testing\nmultiline\ntext\n\n123";

	private static final double EQUALS_DELTA = 0.25d;
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	private MainActivityHelper activityHelper;
	private TextTool textTool;
	private TextTool textToolAfterZoom;
	private EditText textEditText;
	private RecyclerView fontList;
	private MaterialButton underlinedToggleButton;
	private MaterialButton italicToggleButton;
	private MaterialButton boldToggleButton;
	private EditText textSize;
	private LayerContracts.Model layerModel;
	private MainActivity activity;
	private ToolReference toolReference;

	@Before
	public void setUp() {
		activity = launchActivityRule.getActivity();
		activityHelper = new MainActivityHelper(activity);
		layerModel = activity.layerModel;
		toolReference = activity.toolReference;

		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		textTool = (TextTool) toolReference.getTool();

		textEditText = activity.findViewById(R.id.pocketpaint_text_tool_dialog_input_text);
		fontList = activity.findViewById(R.id.pocketpaint_text_tool_dialog_list_font);
		underlinedToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_underlined);
		italicToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_italic);
		boldToggleButton = activity.findViewById(R.id.pocketpaint_text_tool_dialog_toggle_bold);
		textSize = activity.findViewById(R.id.pocketpaint_font_size_text);
		textTool.resetBoxPosition();
	}

	@Test
	public void testTextToolStillEditableAfterClosingTextTool() {
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);
		selectFormatting(FormattingOptions.UNDERLINE);
		enterTestText();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));

		onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).perform(replaceText(TEST_TEXT_ADVANCED));

		assertTrue(italicToggleButton.isChecked());
		assertTrue(boldToggleButton.isChecked());
		assertTrue(underlinedToggleButton.isChecked());
		assertEquals(TEST_TEXT_ADVANCED, textEditText.getText().toString());
	}

	@Ignore("Fix bug in own ticket , focus is not correctly implemented in google play either")
	@Test
	public void testDialogKeyboardTextBoxAppearanceOnStartup() {
		onView(withId(R.id.pocketpaint_text_tool_dialog_input_text)).check(matches(hasFocus()));
		checkTextBoxDimensionsAndDefaultPosition();
	}

	@Test
	public void testDialogDefaultValues() {
		onView(withId(R.id.pocketpaint_text_tool_dialog_input_text))
				.check(matches(withHint(R.string.text_tool_dialog_input_hint)))
				.check(matches(withText(textTool.text)));

		onToolBarView()
				.performSelectTool(ToolType.TEXT);
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
				.check(matches(atPosition(0, hasDescendant(isChecked()))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
				.check(matches(atPosition(1, hasDescendant(isNotChecked()))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font)).perform(RecyclerViewActions.scrollToPosition(2))
				.check(matches(atPosition(2, hasDescendant(isNotChecked()))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font)).perform(RecyclerViewActions.scrollToPosition(3))
				.check(matches(atPosition(3, hasDescendant(isNotChecked()))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font)).perform(RecyclerViewActions.scrollToPosition(4))
				.check(matches(atPosition(4, hasDescendant(isNotChecked()))));

		assertFalse(textTool.underlined);
		assertFalse(textTool.italic);
		assertFalse(textTool.bold);
	}

	@Test
	public void testDialogToolInteraction() {
		enterTestText();
		assertEquals(TEST_TEXT, textTool.text);

		selectFontType(FontType.SERIF);
		assertEquals(FontType.SERIF, textTool.font);
		assertEquals(FontType.SERIF, ((FontListAdapter) fontList.getAdapter()).getSelectedItem());

		selectFormatting(FormattingOptions.UNDERLINE);
		assertTrue(textTool.underlined);
		assertTrue(underlinedToggleButton.isChecked());
		assertEquals(getFormattingOptionAsString(FormattingOptions.UNDERLINE), underlinedToggleButton.getText().toString());

		selectFormatting(FormattingOptions.UNDERLINE);
		assertFalse(textTool.underlined);
		assertFalse(underlinedToggleButton.isChecked());
		assertEquals(getFormattingOptionAsString(FormattingOptions.UNDERLINE), underlinedToggleButton.getText().toString());

		selectFormatting(FormattingOptions.ITALIC);
		assertTrue(getToolMemberItalic());
		assertTrue(italicToggleButton.isChecked());
		assertEquals(getFormattingOptionAsString(FormattingOptions.ITALIC), italicToggleButton.getText().toString());

		selectFormatting(FormattingOptions.ITALIC);
		assertFalse(getToolMemberItalic());
		assertFalse(italicToggleButton.isChecked());
		assertEquals(getFormattingOptionAsString(FormattingOptions.ITALIC), italicToggleButton.getText().toString());

		selectFormatting(FormattingOptions.BOLD);
		assertTrue(getToolMemberBold());
		assertTrue(boldToggleButton.isChecked());
		assertEquals(getFormattingOptionAsString(FormattingOptions.BOLD), boldToggleButton.getText().toString());

		selectFormatting(FormattingOptions.BOLD);
		assertFalse(getToolMemberBold());
		assertFalse(boldToggleButton.isChecked());
		assertEquals(getFormattingOptionAsString(FormattingOptions.BOLD), boldToggleButton.getText().toString());
	}

	@Test
	public void testDialogAndTextBoxAfterReopenDialog() {
		enterTestText();
		selectFontType(FontType.SANS_SERIF);

		selectFormatting(FormattingOptions.UNDERLINE);
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);

		onToolBarView()
				.performCloseToolOptionsView();

		float oldBoxWidth = getToolMemberBoxWidth();
		float oldBoxHeight = getToolMemberBoxHeight();

		PointF boxPosition = getToolMemberBoxPosition();
		PointF newBoxPosition = new PointF(boxPosition.x + 100, boxPosition.y + 200);
		setToolMemberBoxPosition(newBoxPosition);

		onToolBarView()
				.performOpenToolOptionsView();

		assertEquals(TEST_TEXT, textEditText.getText().toString());
		assertEquals(FontType.SANS_SERIF, ((FontListAdapter) fontList.getAdapter()).getSelectedItem());
		assertTrue(underlinedToggleButton.isChecked());
		assertTrue(italicToggleButton.isChecked());
		assertTrue(boldToggleButton.isChecked());
		assertTrue(oldBoxWidth == getToolMemberBoxWidth() && oldBoxHeight == getToolMemberBoxHeight());
	}

	@Test
	public void testStateRestoredAfterOrientationChange() {
		enterTestText();
		selectFontType(FontType.SANS_SERIF);
		selectFormatting(FormattingOptions.UNDERLINE);
		selectFormatting(FormattingOptions.ITALIC);
		selectFormatting(FormattingOptions.BOLD);

		final PointF toolMemberBoxPosition = getToolMemberBoxPosition();
		PointF expectedPosition = new PointF(toolMemberBoxPosition.x, toolMemberBoxPosition.y);

		textTool = (TextTool) toolReference.getTool();
		float oldBoxWidth = getToolMemberBoxWidth();
		float oldBoxHeight = getToolMemberBoxHeight();

		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(TEST_TEXT, textEditText.getText().toString());
		assertEquals(FontType.SANS_SERIF, ((FontListAdapter) fontList.getAdapter()).getSelectedItem());
		assertTrue(underlinedToggleButton.isChecked());
		assertTrue(italicToggleButton.isChecked());
		assertTrue(boldToggleButton.isChecked());

		assertEquals(expectedPosition, getToolMemberBoxPosition());
		assertEquals(oldBoxWidth, getToolMemberBoxWidth(), EQUALS_DELTA);
		assertEquals(oldBoxHeight, getToolMemberBoxHeight(), EQUALS_DELTA);
	}

	@Test
	public void testCheckBoxSizeAndContentAfterFormatting() {
		enterTestText();

		assertFalse(underlinedToggleButton.isChecked());
		assertFalse(boldToggleButton.isChecked());
		assertFalse(italicToggleButton.isChecked());

		ArrayList<FontType> fonts = new ArrayList<>();
		fonts.add(FontType.SERIF);
		fonts.add(FontType.SANS_SERIF);
		fonts.add(FontType.MONOSPACE);
		fonts.add(FontType.DUBAI);
		fonts.add(FontType.STC);

		checkTextBoxDimensionsAndDefaultPosition();

		for (FontType font : fonts) {
			layerModel.getCurrentLayer().getBitmap().eraseColor(Color.TRANSPARENT);

			float boxWidth = getToolMemberBoxWidth();
			float boxHeight = getToolMemberBoxHeight();

			selectFontType(font);
			assertTrue(boxWidth == getToolMemberBoxWidth() && boxHeight == getToolMemberBoxHeight());

			PointF canvasPoint = centerBox();

			layerModel.getCurrentLayer().getBitmap().eraseColor(Color.TRANSPARENT);
			onTopBarView()
					.performClickCheckmark();

			int surfaceBitmapHeight = layerModel.getHeight();
			int[] pixelsDrawingSurface = new int[surfaceBitmapHeight];
			layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, 1, (int) canvasPoint.x, 0, 1, surfaceBitmapHeight);
			int pixelAmountBefore = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
			assert pixelAmountBefore > 0;

			selectFormatting(FormattingOptions.UNDERLINE);
			assertTrue(underlinedToggleButton.isChecked());

			layerModel.getCurrentLayer().getBitmap().eraseColor(Color.TRANSPARENT);
			onTopBarView()
					.performClickCheckmark();

			layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, 1, (int) canvasPoint.x, 0, 1, surfaceBitmapHeight);
			int pixelAmountAfter = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
			assert pixelAmountAfter > 0;

			assertTrue(pixelAmountBefore < pixelAmountAfter);

			selectFormatting(FormattingOptions.ITALIC);
			assertTrue(italicToggleButton.isChecked());
			assertTrue(getToolMemberItalic());

			layerModel.getCurrentLayer().getBitmap().eraseColor(Color.TRANSPARENT);
			onTopBarView()
					.performClickCheckmark();

			layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, 1, (int) canvasPoint.x, 0, 1, surfaceBitmapHeight);
			pixelAmountBefore = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
			assert pixelAmountBefore > 0;

			selectFormatting(FormattingOptions.BOLD);
			assertTrue(boldToggleButton.isChecked());

			layerModel.getCurrentLayer().getBitmap().eraseColor(Color.TRANSPARENT);
			onTopBarView()
					.performClickCheckmark();

			layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, 1, (int) canvasPoint.x, 0, 1, surfaceBitmapHeight);
			pixelAmountAfter = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
			assert pixelAmountAfter > 0;

			assertTrue(pixelAmountAfter > pixelAmountBefore);

			selectFormatting(FormattingOptions.UNDERLINE);
			assertFalse(underlinedToggleButton.isChecked());
			selectFormatting(FormattingOptions.ITALIC);
			assertFalse(italicToggleButton.isChecked());
			selectFormatting(FormattingOptions.BOLD);
			assertFalse(boldToggleButton.isChecked());
		}
	}

	@Test
	public void testInputTextAndFormatForTextSize50() {
		enterTestText();

		float boxWidth = getToolMemberBoxWidth();
		float boxHeight = getToolMemberBoxHeight();
		onView(withId(R.id.pocketpaint_font_size_text)).perform(replaceText("50"));
		checkTextBoxDimensions();
		assertTrue("Text box width should be larger with bigger text size", getToolMemberBoxWidth() > boxWidth);
		assertTrue("Text box height should be larger with bigger text size", getToolMemberBoxHeight() > boxHeight);
	}

	@Test
	public void testInputTextAndFormatForTextSize100() {
		enterTestText();

		float boxWidth = getToolMemberBoxWidth();
		float boxHeight = getToolMemberBoxHeight();
		onView(withId(R.id.pocketpaint_font_size_text)).perform(replaceText("100"));
		checkTextBoxDimensions();
		assertTrue("Text box width should be larger with bigger text size", getToolMemberBoxWidth() > boxWidth);
		assertTrue("Text box height should be larger with bigger text size", getToolMemberBoxHeight() > boxHeight);
	}

	@Test
	public void testInputTextAndFormatForTextSize300() {
		enterTestText();

		float boxWidth = getToolMemberBoxWidth();
		float boxHeight = getToolMemberBoxHeight();
		onView(withId(R.id.pocketpaint_font_size_text)).perform(replaceText("300"));
		checkTextBoxDimensions();
		assertTrue("Text box width should be larger with bigger text size", getToolMemberBoxWidth() > boxWidth);
		assertTrue("Text box height should be larger with bigger text size", getToolMemberBoxHeight() > boxHeight);
	}

	@Test
	public void testCommandUndoAndRedo() {
		enterMultilineTestText();

		onToolBarView()
				.performCloseToolOptionsView();

		PointF canvasPoint = centerBox();

		onTopBarView()
				.performClickCheckmark();

		int surfaceBitmapWidth = layerModel.getWidth();
		int[] pixelsDrawingSurface = new int[surfaceBitmapWidth];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		int pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
		assert pixelAmount > 0;

		onTopBarView()
				.performUndo();

		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		assertEquals(0, countPixelsWithColor(pixelsDrawingSurface, Color.BLACK));

		onTopBarView()
				.performRedo();

		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
		assert pixelAmount > 0;
	}

	@Test
	public void testChangeTextColor() {
		enterTestText();

		onToolBarView()
				.performCloseToolOptionsView();

		PointF canvasPoint = centerBox();

		onToolProperties()
				.setColor(Color.WHITE);

		Paint paint = textTool.textPaint;
		int selectedColor = paint.getColor();
		assertEquals(Color.WHITE, selectedColor);

		onTopBarView()
				.performClickCheckmark();

		int surfaceBitmapWidth = layerModel.getWidth();
		int[] pixelsDrawingSurface = new int[surfaceBitmapWidth];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		int pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.WHITE);
		assert pixelAmount > 0;

		onToolProperties()
				.setColor(Color.BLACK);

		selectedColor = paint.getColor();
		assertEquals(Color.BLACK, selectedColor);

		onTopBarView()
				.performClickCheckmark();

		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) canvasPoint.y, surfaceBitmapWidth, 1);
		pixelAmount = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
		assert pixelAmount > 0;
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
		checkTextBoxDimensionsAndDefaultPosition();

		enterMultilineTestText();

		onToolBarView()
				.performCloseToolOptionsView();

		String[] expectedTextSplitUp = {"testing", "multiline", "text", "", "123"};
		String[] actualTextSplitUp = getToolMemberMultilineText();

		assertArrayEquals(expectedTextSplitUp, actualTextSplitUp);
	}

	@Test
	public void testTextToolAppliedWhenSelectingOtherTool() {
		enterTestText();

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		int surfaceBitmapWidth = layerModel.getWidth();
		int[] pixelsDrawingSurface = new int[surfaceBitmapWidth];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) textTool.toolPosition.y, surfaceBitmapWidth, 1);
		int numberOfBlackPixels = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
		assertTrue(numberOfBlackPixels > 0);
	}

	@Test
	public void testTextToolNotAppliedWhenPressingBack() {
		enterTestText();

		onToolBarView()
				.performCloseToolOptionsView();

		pressBack();

		int surfaceBitmapWidth = layerModel.getWidth();
		int[] pixelsDrawingSurface = new int[surfaceBitmapWidth];
		layerModel.getCurrentLayer().getBitmap().getPixels(pixelsDrawingSurface, 0, surfaceBitmapWidth, 0, (int) textTool.toolPosition.y, surfaceBitmapWidth, 1);
		int numberOfBlackPixels = countPixelsWithColor(pixelsDrawingSurface, Color.BLACK);
		assertEquals(0, numberOfBlackPixels);
	}

	@Test
	public void testTextToolDoesNotResetPerspectiveScale() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		float scale = 2.0f;

		activity.perspective.setScale(scale);
		activity.perspective.surfaceTranslationY = 200;
		activity.perspective.surfaceTranslationX = 50;
		activity.refreshDrawingSurface();

		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		enterTestText();

		assertEquals(scale, activity.perspective.getScale(), 0.0001f);
	}

	@Test
	public void testTextToolBoxIsPlacedCorrectlyWhenZoomedIn() {
		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		enterTestText();

		PointF initialPosition = getToolMemberBoxPosition();

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		float scale = 2.0f;

		activity.perspective.setScale(scale);
		activity.perspective.surfaceTranslationY = 200;
		activity.perspective.surfaceTranslationX = 50;
		activity.refreshDrawingSurface();

		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		enterTestText();

		textToolAfterZoom = (TextTool) activity.toolReference.getTool();

		PointF positionAfterZoom = getToolMemberBoxPosition();

		assertEquals(scale, activity.perspective.getScale(), 0.0001f);

		onTopBarView()
				.performClickCheckmark();

		assertNotEquals(initialPosition, positionAfterZoom);
	}

	@Test
	public void testSettingFontAndFontStyleDoesNotResetBox() {
		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		enterTestText();

		ArrayList<FontType> fonts = new ArrayList<>();
		fonts.add(FontType.SANS_SERIF);
		fonts.add(FontType.MONOSPACE);
		fonts.add(FontType.DUBAI);
		fonts.add(FontType.STC);

		for (FontType font : fonts) {
			if (italicToggleButton.isChecked()) {
				selectFormatting(FormattingOptions.ITALIC);
				selectFormatting(FormattingOptions.BOLD);
				selectFormatting(FormattingOptions.UNDERLINE);
			}

			float boxWidth = getToolMemberBoxWidth();
			float boxHeight = getToolMemberBoxHeight();

			setToolMemberBoxWidth(boxWidth + 100);
			setToolMemberBoxHeight(boxHeight + 100);

			selectFontType(font);
			selectFormatting(FormattingOptions.ITALIC);
			selectFormatting(FormattingOptions.BOLD);
			selectFormatting(FormattingOptions.UNDERLINE);

			assertTrue(boxWidth < getToolMemberBoxWidth() && boxHeight < getToolMemberBoxHeight());
		}
	}

	private PointF centerBox() {
		PointF screenPoint = new PointF(activityHelper.getDisplayWidth() / 2.0f, activityHelper.getDisplayHeight() / 2.0f);
		PointF canvasPoint = new PointF(screenPoint.x, screenPoint.y);
		canvasPoint.x = (float) Math.round(canvasPoint.x);
		canvasPoint.y = (float) Math.round(canvasPoint.y);
		setToolMemberBoxPosition(canvasPoint);
		return canvasPoint;
	}

	private void checkTextBoxDimensions() {
		float actualBoxWidth = getToolMemberBoxWidth();
		float actualBoxHeight = getToolMemberBoxHeight();

		boolean italic = italicToggleButton.isChecked();

		FontType font = ((FontListAdapter) fontList.getAdapter()).getSelectedItem();

		String stringTextSize = textSize.getText().toString();
		float textSize = Float.parseFloat(stringTextSize) * TEXT_SIZE_MAGNIFICATION_FACTOR;

		Paint textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);

		int style = italic ? Typeface.ITALIC : Typeface.NORMAL;

		switch (font) {
			case SANS_SERIF:
				textPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, style));
				break;
			case SERIF:
				textPaint.setTypeface(Typeface.create(Typeface.SERIF, style));
				break;
			case STC:
				textPaint.setTypeface(ResourcesCompat.getFont(launchActivityRule.getActivity(), R.font.stc_regular));
				break;
			case DUBAI:
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
		float expectedBoxWidth = maxTextWidth + 2 * BOX_OFFSET;

		float textHeight = textDescent - textAscent;
		float expectedBoxHeight = textHeight * multilineText.length + 2 * BOX_OFFSET;

		assertEquals(expectedBoxWidth, actualBoxWidth, EQUALS_DELTA);
		assertEquals(expectedBoxHeight, actualBoxHeight, EQUALS_DELTA);
	}

	private void checkTextBoxDefaultPosition() {
		float marginTop = MARGIN_TOP;
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

	private void enterMultilineTestText() {
		enterTextInput(TEST_TEXT_MULTILINE);
	}

	private void selectFormatting(FormattingOptions format) {
		onView(withText(getFormattingOptionAsString(format))).perform(click());
	}

	private void selectFontType(FontType fontType) {
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
				.perform(RecyclerViewActions.scrollTo(hasDescendant(withText(getFontTypeAsString(fontType)))));
		onView(withText(getFontTypeAsString(fontType)))
				.perform(click());
	}

	private String getFontTypeAsString(FontType fontType) {
		switch (fontType) {
			case SANS_SERIF:
				return activity.getString(R.string.text_tool_dialog_font_sans_serif);
			case SERIF:
				return activity.getString(R.string.text_tool_dialog_font_serif);
			case MONOSPACE:
				return activity.getString(R.string.text_tool_dialog_font_monospace);
			case STC:
				return activity.getString(R.string.text_tool_dialog_font_arabic_stc);
			case DUBAI:
				return activity.getString(R.string.text_tool_dialog_font_dubai);
			default:
				return null;
		}
	}

	private String getFormattingOptionAsString(FormattingOptions format) {
		switch (format) {
			case UNDERLINE:
				return activity.getString(R.string.text_tool_dialog_underline_shortcut);
			case ITALIC:
				return activity.getString(R.string.text_tool_dialog_italic_shortcut);
			case BOLD:
				return activity.getString(R.string.text_tool_dialog_bold_shortcut);
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

	private void setToolMemberBoxWidth(float boxWidth) {
		textTool.boxWidth = boxWidth;
	}

	private void setToolMemberBoxHeight(float boxHeight) {
		textTool.boxHeight = boxHeight;
	}

	private float getToolMemberBoxHeight() {
		return textTool.boxHeight;
	}

	private PointF getToolMemberBoxPosition() {
		if (textToolAfterZoom != null) {
			return textToolAfterZoom.toolPosition;
		} else {
			return textTool.toolPosition;
		}
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

	private String[] getToolMemberMultilineText() {
		return textTool.getMultilineText();
	}

	private enum FormattingOptions {
		UNDERLINE, ITALIC, BOLD
	}
}
