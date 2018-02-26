/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.intro;

import android.graphics.Typeface;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.content.ContextCompat;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.intro.TapTargetStyle;
import org.catrobat.paintroid.test.espresso.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class StyleFromXmlTest {

	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule(false);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Test
	public void testIntroHeaderStyle() {
		TapTargetStyle headerStyle = TapTargetStyle.HEADER_STYLE;
		assertEquals("Text Size not matching", headerStyle.getTextSize(), 24);

		int color = ContextCompat.getColor(activityRule.getActivity(), R.color.color_chooser_white);
		assertEquals("Header Text Color not matching", color, headerStyle.getTextColor());

		Typeface expectedTypeface = Typeface.create("sans-serif", Typeface.NORMAL);
		assertEquals("Typeface not matching", expectedTypeface, headerStyle.getTypeface());
	}

	@Test
	public void testIntroTextStyle() {
		TapTargetStyle textStyle = TapTargetStyle.TEXT_STYLE;
		assertEquals("Text Size not matching", textStyle.getTextSize(), 16);

		int color = ContextCompat.getColor(activityRule.getActivity(), R.color.color_chooser_white);
		assertEquals("Header Text Color not matching", color, textStyle.getTextColor());

		Typeface expectedTypeface = Typeface.create("sans-serif", Typeface.NORMAL);
		assertEquals("Typeface not matching", expectedTypeface, textStyle.getTypeface());
	}
}
