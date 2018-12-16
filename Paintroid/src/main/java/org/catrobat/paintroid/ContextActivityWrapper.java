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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.view.View;

public class ContextActivityWrapper {
	private Context context;

	public ContextActivityWrapper(Context context) {
		this.context = context;
	}

	public <T extends View> T getMainToolOptions() {
		return ((Activity) context).findViewById(R.id.pocketpaint_main_tool_options);
	}

	public <T extends View> T getMainBottomBar() {
		return ((Activity) context).findViewById(R.id.pocketpaint_main_bottom_bar);
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

	public <T extends View> T getDrawingSurfaceView() {
		return ((Activity) context).findViewById(R.id.pocketpaint_drawing_surface_view);
	}

	public Resources getResources() {
		return context.getResources();
	}

	public int getColor(boolean darken) {
		return ContextCompat.getColor(context, darken
				? R.color.pocketpaint_main_drawing_surface_inactive
				: R.color.pocketpaint_main_drawing_surface_active);
	}

	public Activity getContextActivity() {
		return ((Activity) (context));
	}
}
