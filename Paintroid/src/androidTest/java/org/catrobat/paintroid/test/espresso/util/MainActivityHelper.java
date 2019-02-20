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

package org.catrobat.paintroid.test.espresso.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Point;
import android.view.View;

import org.catrobat.paintroid.MainActivity;

public class MainActivityHelper {
	private final MainActivity activity;

	public MainActivityHelper(MainActivity activity) {
		this.activity = activity;
	}

	public Point getDisplaySize() {
		Point displaySize = new Point();
		activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
		return displaySize;
	}

	public int getDisplayWidth() {
		return getDisplaySize().x;
	}

	public int getDisplayHeight() {
		return getDisplaySize().y;
	}

	public int getScreenOrientation() {
		return activity.getRequestedOrientation();
	}

	public void setScreenOrientation(int orientation) {
		activity.setRequestedOrientation(orientation);
	}

	public static MainActivity getMainActivityFromView(View view) {
		Context context = view.getContext();
		while (context instanceof ContextWrapper) {
			if (context instanceof MainActivity) {
				return (MainActivity) context;
			}
			context = ((ContextWrapper) context).getBaseContext();
		}
		throw new RuntimeException("View context does not implement MainActivity");
	}
}
