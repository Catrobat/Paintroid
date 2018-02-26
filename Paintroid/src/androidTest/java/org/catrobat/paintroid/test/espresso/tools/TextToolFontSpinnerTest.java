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

package org.catrobat.paintroid.test.espresso.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.PaintroidApplication.defaultSystemLanguage;
import static org.catrobat.paintroid.test.espresso.rtl.util.RtlUiTestUtils.checkTextDirection;
import static org.catrobat.paintroid.test.espresso.rtl.util.RtlUiTestUtils.openMultilingualActivity;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getConfiguration;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.hasTypeFace;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withIndex;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TextToolFontSpinnerTest {
	private static final Locale ARABICLOCALE = new Locale("ar");
	private int normalStyle = Typeface.NORMAL;
	private Context context = InstrumentationRegistry.getTargetContext();
	private Typeface sansSerifFontFace = Typeface.create(Typeface.SANS_SERIF, normalStyle);
	private Typeface serifFontFace = Typeface.create(Typeface.SERIF, normalStyle);
	private Typeface defaultFontFace = Typeface.create(Typeface.MONOSPACE, normalStyle);
	private Typeface stcFontFace = Typeface.createFromAsset(context.getResources().getAssets(), "STC.otf");
	private Typeface dubaiFontFace = Typeface.createFromAsset(context.getResources().getAssets(), "Dubai.TTF");
	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();
	private IdlingResource dialogWait;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new RtlActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() throws Exception {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		Espresso.registerIdlingResources(dialogWait);
	}

	@Test
	public void testTextFontFaceOfFontSpinnerArabic() throws Exception {
		openMultilingualActivity();
		onData(hasToString(startsWith(ARABICLOCALE.getDisplayName(ARABICLOCALE)))).perform(click());
		assertEquals(Locale.getDefault().getDisplayLanguage(), ARABICLOCALE.getDisplayLanguage());
		assertEquals(View.LAYOUT_DIRECTION_RTL, getConfiguration().getLayoutDirection());
		assertTrue(checkTextDirection(Locale.getDefault().getDisplayName()));
		selectTool(ToolType.TEXT);
		onView(withId(R.id.text_tool_dialog_spinner_font))
				.perform(click());
		onView(withIndex(withId(android.R.id.text1), 0))
				.check(matches(hasTypeFace(defaultFontFace)));

		onView(withIndex(withId(android.R.id.text1), 1))
				.check(matches(hasTypeFace(serifFontFace)));

		onView(withIndex(withId(android.R.id.text1), 2))
				.check(matches(hasTypeFace(sansSerifFontFace)));

		onView(withIndex(withId(android.R.id.text1), 3))
				.check(matches(hasTypeFace(dubaiFontFace)));

		onView(withIndex(withId(android.R.id.text1), 4))
				.check(matches(hasTypeFace(stcFontFace)));
	}

	@Test
	public void testTextFontFaceOfFontSpinnerEnglish() throws Exception {
		openMultilingualActivity();
		onData(hasToString(startsWith(context.getResources().getString(R.string.device_language))))
				.perform(click());
		assertEquals(Locale.getDefault().getLanguage(), defaultSystemLanguage);
		selectTool(ToolType.TEXT);
		onView(withId(R.id.text_tool_dialog_spinner_font))
				.perform(click());
		onView(withIndex(withId(android.R.id.text1), 0))
				.check(matches(hasTypeFace(defaultFontFace)));

		onView(withIndex(withId(android.R.id.text1), 1))
				.check(matches(hasTypeFace(serifFontFace)));

		onView(withIndex(withId(android.R.id.text1), 2))
				.check(matches(hasTypeFace(sansSerifFontFace)));

		onView(withIndex(withId(android.R.id.text1), 3))
				.check(matches(hasTypeFace(dubaiFontFace)));

		onView(withIndex(withId(android.R.id.text1), 4))
				.check(matches(hasTypeFace(stcFontFace)));
	}
}
