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

import org.hamcrest.Matcher;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.Root;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;

public abstract class CustomViewInteraction {
	protected ViewInteraction viewInteraction;

	protected CustomViewInteraction(ViewInteraction viewInteraction) {
		this.viewInteraction = viewInteraction;
	}

	public final ViewInteraction perform(final ViewAction... viewActions) {
		return viewInteraction.perform(viewActions);
	}

	public final ViewInteraction withFailureHandler(FailureHandler var1) {
		return viewInteraction.withFailureHandler(var1);
	}

	public final ViewInteraction inRoot(Matcher<Root> var1) {
		return viewInteraction.inRoot(var1);
	}

	public final ViewInteraction noActivity() {
		return viewInteraction.noActivity();
	}

	public final ViewInteraction check(final ViewAssertion viewAssert) {
		return viewInteraction.check(viewAssert);
	}
}
