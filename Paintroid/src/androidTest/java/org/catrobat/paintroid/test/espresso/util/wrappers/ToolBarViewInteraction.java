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

import android.support.test.espresso.ViewInteraction;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.not;

public final class ToolBarViewInteraction extends CustomViewInteraction {
	private ToolBarViewInteraction() {
		super(onView(withId(R.id.pocketpaint_toolbar)));
	}

	public static ToolBarViewInteraction onToolBarView() {
		return new ToolBarViewInteraction();
	}

	public ViewInteraction onSelectedToolButton() {
		return onView(withId(PaintroidApplication.currentTool.getToolType().getToolButtonID()));
	}

	public ViewInteraction onToolOptions() {
		return onView(withId(R.id.pocketpaint_layout_tool_options));
	}

	public ToolBarViewInteraction performClickSelectedToolButton() {
		onSelectedToolButton()
				.perform(scrollTo(), click());
		return this;
	}

	public ToolBarViewInteraction performSelectTool(ToolType toolType) {
		onView(withId(toolType.getToolButtonID()))
				.perform(scrollTo());
		if (PaintroidApplication.currentTool.getToolType() != toolType) {
			onView(withId(toolType.getToolButtonID()))
					.perform(click());
		}
		return this;
	}

	public ToolBarViewInteraction performOpenToolOptions() {
		onToolOptions()
				.check(matches(not(isDisplayed())));
		return performClickSelectedToolButton();
	}

	public ToolBarViewInteraction performCloseToolOptions() {
		onToolOptions()
				.check(matches(isDisplayed()));
		return performClickSelectedToolButton();
	}
}
