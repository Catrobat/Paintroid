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

package org.catrobat.paintroid;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.paintroid.tools.ToolType;

public class ContextActivityWrapper {
	private Context context;

	public ContextActivityWrapper(Context context) {
		this.context = context;
	}

	public <T extends View> T getMainToolOptions() {
		return ((Activity) context).findViewById(R.id.pocketpaint_main_tool_options);
	}

	public <T extends View> T getToolbar() {
		return ((Activity) context).findViewById(R.id.pocketpaint_toolbar);
	}

	public <T extends View> T getLayoutToolOptions() {
		return ((Activity) context).findViewById(R.id.pocketpaint_layout_tool_options);
	}

	public <T extends View> T getLayoutToolSpecificOptions() {
		return ((Activity) context).findViewById(R.id.pocketpaint_layout_tool_specific_options);
	}

	public Resources getResources() {
		return context.getResources();
	}

	public boolean toggleShowToolOptions(boolean toggleOptions, boolean toolOptionsShown) {
		if (!toggleOptions) {
			LinearLayout mainToolOptions = ((Activity) context).findViewById(R.id.pocketpaint_main_tool_options);
			LinearLayout mainBottomBar = ((Activity) context).findViewById(R.id.pocketpaint_main_bottom_bar);
			int orientation = getResources().getConfiguration().orientation;

			if (!toolOptionsShown) {
				mainToolOptions.setY(mainBottomBar.getY() + mainBottomBar.getHeight());
				mainToolOptions.setVisibility(View.VISIBLE);
				float yPos = 0;
				if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					yPos = mainBottomBar.getY() - mainToolOptions.getHeight();
				} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
					yPos = mainBottomBar.getHeight() - mainToolOptions.getHeight();
				}
				mainToolOptions.animate().y(yPos);
				dimBackground(true);

				return true;
			} else {
				mainToolOptions.animate().y(mainBottomBar.getY() + mainBottomBar.getHeight());
				dimBackground(false);

				return false;
			}
		}
		return toolOptionsShown;
	}

	public void dimBackground(boolean darken) {
		View drawingSurfaceView = ((Activity) context).findViewById(R.id.pocketpaint_drawing_surface_view);
		int colorFrom = ((ColorDrawable) drawingSurfaceView.getBackground()).getColor();

		int colorTo = ContextCompat.getColor(context, darken
				? R.color.pocketpaint_main_drawing_surface_inactive
				: R.color.pocketpaint_main_drawing_surface_active);

		ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(
				drawingSurfaceView, "backgroundColor", new ArgbEvaluator(), colorFrom, colorTo);
		backgroundColorAnimator.setDuration(250);
		backgroundColorAnimator.start();
	}

	public void hide() {
		LinearLayout mainToolOptions = getMainToolOptions();
		mainToolOptions.setVisibility(View.INVISIBLE);
		dimBackground(false);
	}

	public void resetAndInitializeToolOptions(final LinearLayout toolSpecificOptionsLayout,
											final LinearLayout toolOptionsLayout, final ToolType toolType) {
		getMainToolOptions().setVisibility(View.INVISIBLE);
		dimBackground(false);

		((Activity) (context)).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				toolSpecificOptionsLayout.removeAllViews();
				TextView toolOptionsName = toolOptionsLayout.findViewById(R.id.pocketpaint_layout_tool_options_name);
				toolOptionsName.setText(getResources().getString(toolType.getNameResource()));
			}
		});
	}
}
