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

import android.content.Context;
import android.graphics.Typeface;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.core.content.res.ResourcesCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.hasTypeFace;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withIndex;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;

@RunWith(AndroidJUnit4.class)
public class TextToolFontSpinnerTest {
	private int normalStyle = Typeface.NORMAL;
	private Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
	private Typeface sansSerifFontFace = Typeface.create(Typeface.SANS_SERIF, normalStyle);
	private Typeface serifFontFace = Typeface.create(Typeface.SERIF, normalStyle);
	private Typeface monospaceFontFace = Typeface.create(Typeface.MONOSPACE, normalStyle);
	private Typeface stcFontFace = ResourcesCompat.getFont(context, R.font.stc_regular);
	private Typeface dubaiFontFace = ResourcesCompat.getFont(context, R.font.dubai);

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void testTextFontFaceOfFontSpinnerEnglish() {
		onToolBarView()
				.performSelectTool(ToolType.TEXT);
		onView(withId(R.id.pocketpaint_text_tool_dialog_spinner_font))
				.perform(click());
		onView(withIndex(withId(android.R.id.text1), 0))
				.check(matches(hasTypeFace(sansSerifFontFace)));

		onView(withIndex(withId(android.R.id.text1), 1))
				.check(matches(hasTypeFace(monospaceFontFace)));

		onView(withIndex(withId(android.R.id.text1), 2))
				.check(matches(hasTypeFace(serifFontFace)));

		onView(withIndex(withId(android.R.id.text1), 3))
				.check(matches(hasTypeFace(dubaiFontFace)));

		onView(withIndex(withId(android.R.id.text1), 4))
				.check(matches(hasTypeFace(stcFontFace)));
	}

	@Test
	public void checkIfSansSerifIsDefaultSpinnerFont() {
		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		onView(withId(R.id.pocketpaint_text_tool_dialog_spinner_font))
				.check(matches(withSpinnerText("Sans Serif")));
	}
}
