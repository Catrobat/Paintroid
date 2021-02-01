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

package org.catrobat.paintroid.listener;

import android.view.View;

import org.catrobat.paintroid.ui.BottomBarHorizontalScrollView;

public class BottomBarScrollListener implements BottomBarHorizontalScrollView.ScrollStateListener {
	private View next;
	private View previous;

	public BottomBarScrollListener(View previous, View next) {
		this.next = next;
		this.previous = previous;
	}

	public void onScrollMostRight() {
		next.setVisibility(View.GONE);
	}

	public void onScrollMostLeft() {
		previous.setVisibility(View.GONE);
	}

	public void onScrollFromMostLeft() {
		previous.setVisibility(View.VISIBLE);
	}

	public void onScrollFromMostRight() {
		next.setVisibility(View.VISIBLE);
	}
}
