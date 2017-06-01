/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration;

import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;

import java.util.Arrays;
import java.util.Locale;

public class ArabicNumberFormatIntegrationTest extends BaseIntegrationTestClass {
	public ArabicNumberFormatIntegrationTest() throws Exception {
		super();
	}

	private final int RGB_TAB_INDEX = 2;

	private String[] HindiNumbers = new String[]{"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "١٠",
			"١١", "١٢", "١٣", "١٤", "١٥", "١٦", "١٧", "١٨", "١٩", "٢٠",
			"٢١", "٢٢", "٢٣", "٢٤", "٢٥", "٢٦", "٢٧", "٢٨", "٢٩", "٣٠",
			"٣١", "٣٢", "٣٣", "٣٤", "٣٥", "٣٦", "٣٧", "٣٨", "٣٩", "٤٠",
			"٤١", "٤٢", "٤٣", "٤٤", "٤٥", "٤٦", "٤٧", "٤٨", "٤٩", "٥٠",
			"٥١", "٥٢", "٥٣", "٥٤", "٥٥", "٥٦", "٥٧", "٥٨", "٥٩", "٦٠",
			"٦١", "٦٢", "٦٣", "٦٤", "٦٥", "٦٦", "٦٧", "٦٨", "٦٩", "٧٠",
			"٧١", "٧٢", "٧٣", "٧٤", "٧٥", "٧٦", "٧٧", "٧٨", "٧٩", "٨٠",
			"٨١", "٨٢", "٨٣", "٨٤", "٨٥", "٨٦", "٨٧", "٨٨", "٨٩", "٩٠",
			"٩١", "٩٢", "٩٣", "٩٤", "٩٥", "٩٦", "٩٧", "٩٨", "٩٩", "١٠٠"};

	public void testHindiNumberFormatInArabicLanguage() {
		// TODO: switch to RTL-language
		assertTrue("Your PhoneLanguage is not one of the RTL-languages", isRTL());

		//BrushStrokeWidthSizeText
		openToolOptionsForCurrentTool(ToolType.BRUSH);
		TextView text_OfStrokeWidth = (TextView) getActivity().findViewById(R.id.stroke_width_width_text);
		assertTrue(text_OfStrokeWidth.isShown());
		assertTrue(text_OfStrokeWidth.getVisibility() == View.VISIBLE);
		String valueOfStrokeWidth = String.valueOf(text_OfStrokeWidth.getText());
		assertTrue(Arrays.asList(HindiNumbers).contains(valueOfStrokeWidth));
		closeToolOptionsForCurrentTool();

		//LineStrokeWidthSizeText
		selectTool(ToolType.LINE);
		openToolOptionsForCurrentTool(ToolType.LINE);
		assertTrue(text_OfStrokeWidth.isShown());
		assertTrue(text_OfStrokeWidth.getVisibility() == View.VISIBLE);
		assertTrue(Arrays.asList(HindiNumbers).contains(valueOfStrokeWidth));
		closeToolOptionsForCurrentTool();

		//FillColorToleranceSizeText
		selectTool(ToolType.FILL);
		openToolOptionsForCurrentTool(ToolType.FILL);
		TextView text_ofColorTolerance = (TextView) getActivity().findViewById(R.id.fill_tool_dialog_color_tolerance_input);
		assertTrue(text_ofColorTolerance.isShown());
		assertTrue(text_ofColorTolerance.getVisibility() == View.VISIBLE);
		String valueOfColorTolerance = String.valueOf(text_ofColorTolerance.getText());
		assertTrue(Arrays.asList(HindiNumbers).contains(valueOfColorTolerance));
		closeToolOptionsForCurrentTool();

		//EraserStrokeWidthSizeText
		selectTool(ToolType.ERASER);
		openToolOptionsForCurrentTool(ToolType.ERASER);
		assertTrue(text_OfStrokeWidth.isShown());
		assertTrue(text_OfStrokeWidth.getVisibility() == View.VISIBLE);
		assertTrue(Arrays.asList(HindiNumbers).contains(valueOfStrokeWidth));
		closeToolOptionsForCurrentTool();

		//ColorChooserRgbValues
		selectTool(ToolType.BRUSH);
		openColorChooserDialog();
		TabHost tabHost = (TabHost) mSolo.getView(R.id.colorview_tabColors);
		TabWidget colorTabWidget = tabHost.getTabWidget();
		mSolo.clickOnView(colorTabWidget.getChildAt(RGB_TAB_INDEX), true);
		mSolo.waitForText(mSolo.getString(R.string.color_red));
		TextView red = (TextView) mSolo.getView(R.id.rgb_red_value);
		TextView green = (TextView) mSolo.getView(R.id.rgb_green_value);
		TextView blue = (TextView) mSolo.getView(R.id.rgb_blue_value);
		TextView alpha = (TextView) mSolo.getView(R.id.rgb_alpha_value);
		String redValue = String.valueOf(red.getText());
		String greenValue = String.valueOf(green.getText());
		String blueValue = String.valueOf(blue.getText());
		String alphaValue = String.valueOf(alpha.getText());
		assertTrue(Arrays.asList(HindiNumbers).contains(redValue));
		assertTrue(Arrays.asList(HindiNumbers).contains(greenValue));
		assertTrue(Arrays.asList(HindiNumbers).contains(blueValue));
		assertTrue(Arrays.asList(HindiNumbers).contains(alphaValue));
	}

	private static boolean isRTL() {
		return isRTL(Locale.getDefault());
	}

	private static boolean isRTL(Locale locale) {
		final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
				directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}
}
