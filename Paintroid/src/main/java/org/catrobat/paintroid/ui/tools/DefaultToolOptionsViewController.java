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

package org.catrobat.paintroid.ui.tools;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;

import androidx.annotation.Nullable;

public class DefaultToolOptionsViewController implements ToolOptionsViewController {
	private final ViewGroup toolSpecificOptionsLayout;
	private final ViewGroup bottomNavigation;
	private final ViewGroup mainToolOptions;
	private final View topBarSpecificViewCheckmark;

	private boolean toolOptionsShown;
	private boolean enabled = true;
	private Callback callback;

	public DefaultToolOptionsViewController(Activity activity) {
		bottomNavigation = activity.findViewById(R.id.pocketpaint_main_bottom_navigation);
		mainToolOptions = activity.findViewById(R.id.pocketpaint_main_tool_options);
		toolSpecificOptionsLayout = activity.findViewById(R.id.pocketpaint_layout_tool_specific_options);
		topBarSpecificViewCheckmark = activity.findViewById(R.id.pocketpaint_btn_top_checkmark);

		mainToolOptions.setVisibility(View.INVISIBLE);
	}

	@Override
	public void resetToOrigin() {
		toolOptionsShown = false;
		mainToolOptions.setVisibility(View.INVISIBLE);
		mainToolOptions.setY(bottomNavigation.getY() + bottomNavigation.getHeight());
	}

	@Override
	public void hide() {
		if (!enabled) {
			return;
		}

		toolOptionsShown = false;
		mainToolOptions.animate().y(bottomNavigation.getY() + bottomNavigation.getHeight());
		notifyHide();
	}

	@Override
	public void disable() {
		enabled = false;

		if (isVisible()) {
			resetToOrigin();
		}
	}

	@Override
	public void enable() {
		enabled = true;
	}

	@Override
	public void show() {
		if (!enabled) {
			return;
		}

		toolOptionsShown = true;
		mainToolOptions.setVisibility(View.INVISIBLE);
		mainToolOptions.post(new Runnable() {
			@Override
			public void run() {
				float yPos = bottomNavigation.getY() - mainToolOptions.getHeight();
				mainToolOptions.animate().y(yPos);
				mainToolOptions.setVisibility(View.VISIBLE);
			}
		});

		notifyShow();
	}

	@Override
	public void showDelayed() {
		toolSpecificOptionsLayout.post(new Runnable() {
			@Override
			public void run() {
				show();
			}
		});
	}

	private void notifyHide() {
		if (callback != null) {
			callback.onHide();
		}
	}

	private void notifyShow() {
		if (callback != null) {
			callback.onShow();
		}
	}

	@Override
	public void removeToolViews() {
		toolSpecificOptionsLayout.removeAllViews();
		callback = null;
	}

	@Override
	public boolean isVisible() {
		return toolOptionsShown;
	}

	@Override
	public void setCallback(@Nullable Callback callback) {
		this.callback = callback;
	}

	@Override
	public ViewGroup getToolSpecificOptionsLayout() {
		return toolSpecificOptionsLayout;
	}

	@Override
	public void showCheckmark() {
		topBarSpecificViewCheckmark.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideCheckmark() {
		topBarSpecificViewCheckmark.setVisibility(View.GONE);
	}
}
