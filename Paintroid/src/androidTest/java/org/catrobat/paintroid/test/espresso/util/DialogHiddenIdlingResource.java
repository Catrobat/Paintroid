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

package org.catrobat.paintroid.test.espresso.util;

import android.app.Dialog;
import android.support.test.espresso.IdlingResource;

/**
 * Waits until {@link Dialog#isShowing()} is false.
 */
public class DialogHiddenIdlingResource implements IdlingResource {

	private Dialog dialog;
	private ResourceCallback resourceCallback;

	public DialogHiddenIdlingResource(Dialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public String getName() {
		return DialogHiddenIdlingResource.class.getSimpleName();
	}

	@Override
	public boolean isIdleNow() {
		boolean isIdle = !dialog.isShowing();

		if (isIdle) {
			resourceCallback.onTransitionToIdle();
		}

		return isIdle;
	}

	@Override
	public void registerIdleTransitionCallback(ResourceCallback callback) {
		this.resourceCallback = callback;
	}
}
