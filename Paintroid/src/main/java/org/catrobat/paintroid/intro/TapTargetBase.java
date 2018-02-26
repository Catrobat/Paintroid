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

package org.catrobat.paintroid.intro;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.listener.TapTargetListener;
import org.catrobat.paintroid.listener.BottomBarScrollListener;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.BottomBarHorizontalScrollView;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.catrobat.paintroid.intro.helper.IntroAnimationHelper.fadeOut;
import static org.catrobat.paintroid.intro.helper.WelcomeActivityHelper.calculateTapTargetRadius;
import static org.catrobat.paintroid.intro.helper.WelcomeActivityHelper.isRTL;

public abstract class TapTargetBase {
	protected static final String TAG = "TapTarget";
	@VisibleForTesting
	public static final int RADIUS_OFFSET = 2;
	protected final Context context;
	protected final WelcomeActivity activity;
	@VisibleForTesting
	public final HashMap<ToolType, TapTarget> tapTargetMap = new LinkedHashMap<>();
	final View fadeView;
	private final LinearLayout targetView;
	protected BottomBarHorizontalScrollView bottomScrollBar;
	@VisibleForTesting
	public int radius;
	private View bottomBarView;

	TapTargetBase(LinearLayout tapTargetView, View fadeView, WelcomeActivity activity,
			int bottomBarResourceId) {
		this.targetView = tapTargetView;
		this.fadeView = fadeView;
		this.activity = activity;
		this.context = activity.getBaseContext();
		final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		this.radius = calculateTapTargetRadius(targetView.getHeight(), metrics, RADIUS_OFFSET);
		bottomBarView = activity.findViewById(bottomBarResourceId);
		bottomScrollBar = (BottomBarHorizontalScrollView)
				bottomBarView.findViewById(R.id.bottom_bar_scroll_view);
	}

	private static ToolTypeViewTuple getToolTypeFromView(View view) {
		for (ToolType type : ToolType.values()) {
			if (view.getId() == type.getToolButtonID() && view.getVisibility() == View.VISIBLE) {
				return new ToolTypeViewTuple(type, view);
			}
		}

		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup) view;

			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				ToolTypeViewTuple tuple = getToolTypeFromView(viewGroup.getChildAt(i));
				if (tuple != null) {
					return tuple;
				}
			}
		}

		return null;
	}

	private void addClickListener(View view, final ToolType toolType) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				performClick(toolType);
			}
		});
	}

	private void performClick(ToolType toolType) {
		fadeOut(fadeView);
		TapTarget tapTarget = tapTargetMap.get(toolType);

		TapTargetView.showFor(activity, tapTarget, new TapTargetListener(fadeView));
	}

	public void initTargetView() {
		for (int i = 0; i < targetView.getChildCount(); i++) {
			View view = targetView.getChildAt(i);
			ToolTypeViewTuple tuple = getToolTypeFromView(view);
			if (tuple == null) {
				continue;
			}
			tapTargetMap.put(tuple.toolType, createTapTarget(tuple.toolType, view));
			addClickListener(tuple.view, tuple.toolType);
		}

		setBottomBarListener();
		startBottomBarAnimation();
	}

	private void setBottomBarListener() {
		final View previous = bottomBarView.findViewById(R.id.bottom_previous);
		final View next = bottomBarView.findViewById(R.id.bottom_next);
		bottomScrollBar.setScrollStateListener(new BottomBarScrollListener(previous, next));
	}

	private void startBottomBarAnimation() {
		final boolean isRtl = isRTL(activity);
		bottomScrollBar.post(new Runnable() {
			public void run() {
				int scrollToX = isRtl ? bottomScrollBar.getWidth() : 0;
				int scrollFromX = isRtl ? 0 : bottomScrollBar.getWidth();
				bottomScrollBar.setScrollX(scrollFromX);
				ObjectAnimator.ofInt(bottomScrollBar, "scrollX", scrollToX).setDuration(1000).start();
			}
		});
	}

	private TapTarget createTapTarget(ToolType toolType, View targetView) {
		return TapTarget
				.forView(targetView, context.getResources().getString(toolType.getNameResource()),
						context.getResources().getString(toolType.getHelpTextResource()))
				.targetRadius(radius)
				.titleTextSize(TapTargetStyle.HEADER_STYLE.getTextSize())
				.titleTextColorInt(TapTargetStyle.HEADER_STYLE.getTextColor())
				.descriptionTextColorInt(TapTargetStyle.TEXT_STYLE.getTextColor())
				.descriptionTextSize(TapTargetStyle.TEXT_STYLE.getTextSize())
				.textTypeface(TapTargetStyle.TEXT_STYLE.getTypeface())
				.cancelable(true)
				.outerCircleColor(R.color.custom_background_color)
				.targetCircleColor(R.color.color_chooser_white);
	}

	static final class ToolTypeViewTuple {
		public final ToolType toolType;
		public final View view;

		ToolTypeViewTuple(ToolType toolType, View view) {
			this.toolType = toolType;
			this.view = view;
		}
	}
}
