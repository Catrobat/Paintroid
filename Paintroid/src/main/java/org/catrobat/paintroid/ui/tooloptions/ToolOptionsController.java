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

package org.catrobat.paintroid.ui.tooloptions;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.common.Constants;
import org.catrobat.paintroid.tools.options.FillToolOptionsContract;
import org.catrobat.paintroid.tools.options.ShapeToolOptionsContract;
import org.catrobat.paintroid.tools.options.TextToolOptionsContract;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;
import org.catrobat.paintroid.tools.options.TransformToolOptionsContract;

public class ToolOptionsController implements ToolOptionsControllerContract {
	private final Activity activity;

	private final TextView toolOptionsTextView;
	private final ViewGroup toolSpecificOptionsLayout;
	private final ViewGroup mainBottomBar;
	private final ViewGroup mainToolOptions;
	private final View drawingSurfaceView;

	private final int colorActive;
	private final int colorInactive;

	private boolean toolOptionsShown;
	private Callback callback;

	public ToolOptionsController(Activity activity) {
		this.activity = activity;

		drawingSurfaceView = activity.findViewById(R.id.pocketpaint_drawing_surface_view);
		mainBottomBar = activity.findViewById(R.id.pocketpaint_main_bottom_bar);
		mainToolOptions = activity.findViewById(R.id.pocketpaint_main_tool_options);
		toolOptionsTextView = activity.findViewById(R.id.pocketpaint_layout_tool_options_name);
		toolSpecificOptionsLayout = activity.findViewById(R.id.pocketpaint_layout_tool_specific_options);

		colorActive = ContextCompat.getColor(activity, R.color.pocketpaint_main_drawing_surface_active);
		colorInactive = ContextCompat.getColor(activity, R.color.pocketpaint_main_drawing_surface_inactive);

		mainToolOptions.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mainToolOptions.setVisibility(View.INVISIBLE);
	}

	@Override
	public void hide() {
		toolOptionsShown = false;
		mainToolOptions.setVisibility(View.INVISIBLE);
		drawingSurfaceView.setBackgroundColor(colorActive);
		notifyHide();
	}

	@Override
	public void hideAnimated() {
		toolOptionsShown = false;
		mainToolOptions.animate().y(mainBottomBar.getY() + mainBottomBar.getHeight());
		animateBackgroundToColor(colorActive);
		notifyHide();
	}

	@Override
	public void showAnimated() {
		toolOptionsShown = true;
		mainToolOptions.setVisibility(View.INVISIBLE);
		mainToolOptions.post(new Runnable() {
			@Override
			public void run() {
				int orientation = activity.getResources().getConfiguration().orientation;
				mainToolOptions.setY(mainBottomBar.getY() + mainBottomBar.getHeight());
				float yPos = orientation == Configuration.ORIENTATION_PORTRAIT
						? mainBottomBar.getY() - mainToolOptions.getHeight()
						: mainBottomBar.getHeight() - mainToolOptions.getHeight();
				mainToolOptions.animate().y(yPos);
				mainToolOptions.setVisibility(View.VISIBLE);
			}
		});

		animateBackgroundToColor(colorInactive);
		notifyShow();
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
	public void setToolName(@StringRes int id) {
		toolOptionsTextView.setText(id);
	}

	@Override
	public boolean isVisible() {
		return toolOptionsShown;
	}

	private void animateBackgroundToColor(int color) {
		int colorFrom = ((ColorDrawable) drawingSurfaceView.getBackground()).getColor();

		ObjectAnimator backgroundColorAnimator = ObjectAnimator.ofObject(
				drawingSurfaceView, "backgroundColor", new ArgbEvaluator(), colorFrom, color);
		backgroundColorAnimator.setDuration(250);
		backgroundColorAnimator.start();
	}

	@Override
	public int getScrollTolerance() {
		return (int) (activity.getResources().getDisplayMetrics().widthPixels
				* Constants.SCROLL_TOLERANCE_PERCENTAGE);
	}

	@Override
	public BrushToolOptions createBrushPickerView() {
		return new BrushToolOptions(toolSpecificOptionsLayout);
	}

	@Override
	public FillToolOptionsContract createFillToolOptions() {
		return new FillToolOptions(activity, toolSpecificOptionsLayout);
	}

	@Override
	public TransformToolOptionsContract createTransformToolOptions() {
		return new TransformToolOptions(activity, toolSpecificOptionsLayout);
	}

	@Override
	public TextToolOptionsContract createTextToolOptions() {
		return new TextToolOptions(activity, toolSpecificOptionsLayout);
	}

	@Override
	public ShapeToolOptionsContract createShapeToolOptions() {
		return new ShapeToolOptions(activity, toolSpecificOptionsLayout);
	}

	@Override
	public void setCallback(@Nullable Callback callback) {
		this.callback = callback;
	}
}
