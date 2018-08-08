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

package org.catrobat.paintroid.ui.viewholder;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.tools.ToolType;

public class BottomBarViewHolder implements MainActivityContracts.BottomBarViewHolder {
	public final View layout;
	public final View scrollView;
	public final int orientation;

	private AnimatorSet initialAnimation = null;

	public BottomBarViewHolder(View layout) {
		this.layout = layout;
		this.scrollView = layout.findViewById(R.id.pocketpaint_bottom_bar_scroll_view);
		this.orientation = layout.getResources().getConfiguration().orientation;
	}

	@Override
	public void show() {
		layout.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		layout.setVisibility(View.GONE);
	}

	@Override
	public void startAnimation(ToolType toolType) {
		if (initialAnimation != null) {
			initialAnimation.cancel();
		}
		initialAnimation = new AnimatorSet();
		initialAnimation.playSequentially(
				createScrollViewAnimator(toolType),
				createButtonHighlightAnimator(toolType));
		initialAnimation.start();
	}

	@Override
	public void selectToolButton(ToolType toolType) {
		View buttonView = layout.findViewById(toolType.getToolButtonID());
		buttonView.setSelected(true);
	}

	@Override
	public void deSelectToolButton(ToolType toolType) {
		View buttonView = layout.findViewById(toolType.getToolButtonID());
		buttonView.setSelected(false);
	}

	@Override
	public void cancelAnimation() {
		if (initialAnimation != null) {
			initialAnimation.cancel();
		}
	}

	@Override
	public void scrollToButton(ToolType toolType, boolean animate) {
		View buttonView = layout.findViewById(toolType.getToolButtonID());
		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				ScrollView verticalScrollView = (ScrollView) scrollView;
				int destinationY = getViewCenterYInScrollView(scrollView, buttonView);
				if (animate) {
					verticalScrollView.smoothScrollTo(0, destinationY);
				} else {
					verticalScrollView.scrollTo(0, destinationY);
				}
				break;
			case Configuration.ORIENTATION_PORTRAIT:
			default:
				HorizontalScrollView horizontalScrollView = (HorizontalScrollView) scrollView;
				int destinationX = getViewCenterXInScrollView(scrollView, buttonView);
				if (animate) {
					horizontalScrollView.smoothScrollTo(destinationX, 0);
				} else {
					horizontalScrollView.scrollTo(destinationX, 0);
				}
				break;
		}
	}

	private int viewGetEnd(View view) {
		int layoutDirection = view.getLayoutDirection();
		switch (layoutDirection) {
			case View.LAYOUT_DIRECTION_RTL:
				return view.getLeft();
			case View.LAYOUT_DIRECTION_LTR:
			default:
				return view.getRight();
		}
	}

	private int getViewCenterYInScrollView(View scrollView, View view) {
		return (int) (view.getY() + (view.getHeight() - scrollView.getHeight()) / 2.0f);
	}

	private int getViewCenterXInScrollView(View scrollView, View view) {
		return (int) (view.getX() + (view.getWidth() - scrollView.getWidth()) / 2.0f);
	}

	private ValueAnimator createScrollViewAnimator(ToolType toolType) {
		View toolView = layout.findViewById(toolType.getToolButtonID());
		String propertyName;
		int scrollFrom;
		int scrollTo;
		switch (orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				propertyName = "scrollX";
				scrollFrom = viewGetEnd(scrollView);
				scrollTo = getViewCenterXInScrollView(scrollView, toolView);
				break;
			default:
				propertyName = "scrollY";
				scrollFrom = scrollView.getHeight();
				scrollTo = getViewCenterYInScrollView(scrollView, toolView);
				break;
		}
		ValueAnimator animator = ObjectAnimator.ofInt(scrollView, propertyName, scrollFrom, scrollTo);
		animator.setDuration(1000);
		return animator;
	}

	private ValueAnimator createButtonHighlightAnimator(ToolType toolType) {
		final View toolView = layout.findViewById(toolType.getToolButtonID());
		final Drawable backgroundDrawable = toolView.getBackground();

		int color = ContextCompat.getColor(toolView.getContext(), R.color.pocketpaint_main_bottom_bar_selected);
		int fadedColor = color & 0x00ffffff;

		ValueAnimator animator = ObjectAnimator.ofInt(toolView, "backgroundColor", color, fadedColor);
		animator.setEvaluator(new ArgbEvaluator());
		animator.setInterpolator(new LinearInterpolator());
		animator.setDuration(500);
		animator.setRepeatCount(5);
		animator.setRepeatMode(ValueAnimator.REVERSE);
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			private void onEnd() {
				toolView.setBackground(backgroundDrawable);
				toolView.setSelected(true);
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				onEnd();
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				onEnd();
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		return animator;
	}
}

