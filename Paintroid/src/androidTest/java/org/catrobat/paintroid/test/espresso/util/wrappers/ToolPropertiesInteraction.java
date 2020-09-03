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

package org.catrobat.paintroid.test.espresso.util.wrappers;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Cap;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Tool;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getMainActivity;
import static org.junit.Assert.assertEquals;

public final class ToolPropertiesInteraction extends CustomViewInteraction {
	private ToolPropertiesInteraction() {
		super(null);
	}
	public static ToolPropertiesInteraction onToolProperties() {
		return new ToolPropertiesInteraction();
	}

	public ToolPropertiesInteraction checkMatchesColor(@ColorInt int expectedColor) {
		assertEquals(expectedColor, getCurrentTool().getDrawPaint().getColor());
		return this;
	}

	public ToolPropertiesInteraction checkMatchesColorResource(@ColorRes int expectedColorRes) {
		int expectedColor = ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().getTargetContext(), expectedColorRes);
		return checkMatchesColor(expectedColor);
	}

	public ToolPropertiesInteraction checkCap(Cap expectedCap) {
		Paint strokePaint = getCurrentTool().getDrawPaint();
		assertEquals(expectedCap, strokePaint.getStrokeCap());
		return this;
	}

	public ToolPropertiesInteraction checkStrokeWidth(float expectedStrokeWidth) {
		Paint strokePaint = getCurrentTool().getDrawPaint();
		assertEquals(expectedStrokeWidth, strokePaint.getStrokeWidth(), Float.MIN_VALUE);
		return this;
	}

	public ToolPropertiesInteraction setColor(int color) {
		getCurrentTool().changePaintColor(color);
		return this;
	}

	public Tool getCurrentTool() {
		return getMainActivity().toolReference.get();
	}

	public ToolPropertiesInteraction setColorResource(@ColorRes int colorResource) {
		int color = ContextCompat.getColor(InstrumentationRegistry.getInstrumentation().getTargetContext(), colorResource);
		return setColor(color);
	}

	public ToolPropertiesInteraction setColorPreset(int colorPresetPosition) {
		Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		int[] presetColors = targetContext.getResources().getIntArray(R.array.pocketpaint_color_picker_preset_colors);
		return setColor(presetColors[colorPresetPosition]);
	}
}
