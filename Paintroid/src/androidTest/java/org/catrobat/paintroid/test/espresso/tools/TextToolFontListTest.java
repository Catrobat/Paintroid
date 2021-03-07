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
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.atPosition;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.hasTypeFace;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class TextToolFontListTest {
	private final int normalStyle = Typeface.NORMAL;
	private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
	private final Typeface sansSerifFontFace = Typeface.create(Typeface.SANS_SERIF, normalStyle);
	private final Typeface serifFontFace = Typeface.create(Typeface.SERIF, normalStyle);
	private final Typeface monospaceFontFace = Typeface.create(Typeface.MONOSPACE, normalStyle);
	private final Typeface stcFontFace = ResourcesCompat.getFont(context, R.font.stc_regular);
	private final Typeface dubaiFontFace = ResourcesCompat.getFont(context, R.font.dubai);

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Test
	public void testTextFontFaceOfFontSpinnerEnglish() {
		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
				.check(matches(atPosition(0, hasDescendant(hasTypeFace(sansSerifFontFace)))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font))
				.check(matches(atPosition(1, hasDescendant(hasTypeFace(monospaceFontFace)))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font)).perform(RecyclerViewActions.scrollToPosition(2))
				.check(matches(atPosition(2, hasDescendant(hasTypeFace(serifFontFace)))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font)).perform(RecyclerViewActions.scrollToPosition(3))
				.check(matches(atPosition(3, hasDescendant(hasTypeFace(dubaiFontFace)))));
		onView(withId(R.id.pocketpaint_text_tool_dialog_list_font)).perform(RecyclerViewActions.scrollToPosition(4))
				.check(matches(atPosition(4, hasDescendant(hasTypeFace(stcFontFace)))));
	}

	@Test
	public void checkIfSansSerifIsDefaultSpinnerFont() {
		onToolBarView()
				.performSelectTool(ToolType.TEXT);

		onView(withText(R.string.text_tool_dialog_font_sans_serif))
				.check(matches(isChecked()));
	}
}
