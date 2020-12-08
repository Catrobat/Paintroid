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

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;

import androidx.test.espresso.ViewInteraction;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getMainActivity;
import static org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.onBottomNavigationView;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public final class ToolBarViewInteraction extends CustomViewInteraction {

	private ToolBarViewInteraction() {
		super(onView(withId(R.id.pocketpaint_toolbar)));
	}

	public static ToolBarViewInteraction onToolBarView() {
		return new ToolBarViewInteraction();
	}

	public ViewInteraction onSelectedToolButton() {
		return onView(withId(getCurrentToolType().getToolButtonID()));
	}

	public ViewInteraction onToolOptionsView() {
		return onView(withId(R.id.pocketpaint_layout_tool_specific_options));
	}

	public ToolBarViewInteraction performClickSelectedToolButton() {
		onBottomNavigationView()
				.onToolsClicked();
		onSelectedToolButton()
				.perform(click());
		return this;
	}

	public ToolBarViewInteraction onToolsClicked() {
		onBottomNavigationView()
				.onToolsClicked();
		return this;
	}

	public ToolBarViewInteraction performSelectTool(ToolType toolType) {
		if (getCurrentToolType() != toolType) {
			onBottomNavigationView()
					.onToolsClicked();
			onView(withId(toolType.getToolButtonID()))
					.perform(click());
		}
		return this;
	}

	private ToolType getCurrentToolType() {
		return getMainActivity().toolReference.get().getToolType();
	}

	public ToolBarViewInteraction performOpenToolOptionsView() {
		onToolOptionsView()
				.check(matches(not(isDisplayed())));
		onBottomNavigationView()
				.onCurrentClicked();
		return this;
	}

	public ToolBarViewInteraction performCloseToolOptionsView() {
		onToolOptionsView()
				.check(matches(isDisplayed()));
		onBottomNavigationView()
				.onCurrentClicked();
		return this;
	}
}
