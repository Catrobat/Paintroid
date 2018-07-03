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

package org.catrobat.paintroid.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.common.Constants;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.listener.BottomBarScrollListener;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;

public class BottomBar implements View.OnClickListener, View.OnLongClickListener {
	private BottomBarCallback callback;
	private float screenDensity;
	private int orientation;
	private View bottomBarLayout;
	private LinearLayout toolsLayout;
	private ToolType currentToolType;
	private View currentToolButton;
	private View scrollView;

	private AnimatorSet initialAnimation = null;

	public BottomBar(BottomBarCallback callback, float screenDensity, int orientation, View bottomBarLayout, LinearLayout toolsLayout, View scrollView) {
		this.callback = callback;
		this.screenDensity = screenDensity;
		this.orientation = orientation;
		this.bottomBarLayout = bottomBarLayout;
		this.toolsLayout = toolsLayout;
		this.scrollView = scrollView;

		currentToolType = PaintroidApplication.currentTool.getToolType();
		currentToolButton = getToolButtonByToolType(currentToolType);
		currentToolButton.setSelected(true);

		setBottomBarListener();
	}

	public void startAnimation() {
		if (initialAnimation == null) {
			initialAnimation = new AnimatorSet();
			initialAnimation.playSequentially(
					createScrollViewAnimator(),
					createButtonHighlightAnimator());
			initialAnimation.start();
		}
	}

	private static int viewGetEnd(View view) {
		int layoutDirection = view.getLayoutDirection();
		switch (layoutDirection) {
			case View.LAYOUT_DIRECTION_RTL:
				return view.getLeft();
			case View.LAYOUT_DIRECTION_LTR:
			default:
				return view.getRight();
		}
	}

	private static int getViewCenterYInScrollView(View scrollView, View view) {
		return (int) (view.getY() + (view.getHeight() - scrollView.getHeight()) / 2.0f);
	}

	private static int getViewCenterXInScrollView(View scrollView, View view) {
		return (int) (view.getX() + (view.getWidth() - scrollView.getWidth()) / 2.0f);
	}

	private ValueAnimator createScrollViewAnimator() {
		String propertyName;
		int scrollFrom;
		int scrollTo;
		switch (orientation) {
			case Configuration.ORIENTATION_PORTRAIT:
				propertyName = "scrollX";
				scrollFrom = viewGetEnd(scrollView);
				scrollTo = getViewCenterXInScrollView(scrollView, currentToolButton);
				break;
			default:
				propertyName = "scrollY";
				scrollFrom = scrollView.getHeight();
				scrollTo = getViewCenterYInScrollView(scrollView, currentToolButton);
				break;
		}
		ValueAnimator animator = ObjectAnimator.ofInt(scrollView, propertyName, scrollFrom, scrollTo);
		animator.setDuration(1000);
		return animator;
	}

	private ValueAnimator createButtonHighlightAnimator() {
		final View toolButton = currentToolButton;
		final Drawable backgroundDrawable = toolButton.getBackground();

		int color = ContextCompat.getColor(toolButton.getContext(), R.color.bottom_bar_button_activated);
		int fadedColor = color & 0x00ffffff;

		ValueAnimator animator = ObjectAnimator.ofInt(toolButton, "backgroundColor", color, fadedColor);
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
				toolButton.setBackground(backgroundDrawable);
				setActivatedToolButton(toolButton);
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

	private void setBottomBarListener() {
		for (int i = 0; i < toolsLayout.getChildCount(); i++) {
			toolsLayout.getChildAt(i).setOnClickListener(this);
			toolsLayout.getChildAt(i).setOnLongClickListener(this);
		}

		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			View next = bottomBarLayout.findViewById(R.id.bottom_next);
			View previous = bottomBarLayout.findViewById(R.id.bottom_previous);
			BottomBarHorizontalScrollView horizontalScrollView = (BottomBarHorizontalScrollView) scrollView;
			horizontalScrollView.setScrollStateListener(new BottomBarScrollListener(previous, next));
		}
	}

	public void setTool(Tool tool) {
		currentToolType = tool.getToolType();
		currentToolButton = getToolButtonByToolType(currentToolType);
		showToolChangeToast();
		setActivatedToolButton(currentToolButton);
		scrollToSelectedTool(currentToolButton);
	}

	private void scrollToSelectedTool(View toolButton) {
		switch (orientation) {
			case Configuration.ORIENTATION_LANDSCAPE:
				ScrollView verticalScrollView = (ScrollView) scrollView;
				verticalScrollView.smoothScrollTo(0, getViewCenterYInScrollView(scrollView, toolButton));
				break;
			case Configuration.ORIENTATION_PORTRAIT:
			default:
				HorizontalScrollView horizontalScrollView = (HorizontalScrollView) scrollView;
				horizontalScrollView.smoothScrollTo(getViewCenterXInScrollView(scrollView, toolButton), 0);
				break;
		}
	}

	private void showToolChangeToast() {
		Toast toolNameToast = ToastFactory.makeText(currentToolButton.getContext(), currentToolType.getNameResource(), Toast.LENGTH_SHORT);
		int gravity = Gravity.TOP | Gravity.CENTER;
		int yOffset = 0;
		if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
			yOffset = (int) (Constants.ACTION_BAR_HEIGHT * screenDensity);
		}
		toolNameToast.setGravity(gravity, 0, yOffset);
		toolNameToast.show();
	}

	@Override
	public void onClick(View view) {
		if (initialAnimation != null) {
			initialAnimation.cancel();
		}

		ToolType toolType = findToolByView(view);
		if (toolType != null) {
			onToolClick(toolType);
		}
	}

	private void onToolClick(ToolType toolType) {
		if (PaintroidApplication.currentTool.getToolType() != toolType) {
			if (callback.isKeyboardShown()) {
				callback.hideKeyboard();
			} else {
				callback.switchTool(toolType);
			}
		} else {
			PaintroidApplication.currentTool.toggleShowToolOptions();
		}
	}

	@Override
	public boolean onLongClick(View view) {
		ToolType toolType = findToolByView(view);
		return (toolType != null) && onToolLongClick(toolType);
	}

	private boolean onToolLongClick(ToolType toolType) {
		InfoDialog.newInstance(InfoDialog.DialogType.INFO, toolType.getHelpTextResource(),
				toolType.getNameResource()).show(
				callback.getSupportFragmentManager(),
				Constants.HELP_DIALOG_FRAGMENT_TAG);
		return true;
	}

	@Nullable
	private ToolType findToolByView(View view) {
		for (ToolType type : ToolType.values()) {
			if (view.getId() == type.getToolButtonID()) {
				return type;
			}
		}
		return null;
	}

	private View getToolButtonByToolType(ToolType toolType) {
		return toolsLayout.findViewById(toolType.getToolButtonID());
	}

	private void setActivatedToolButton(View toolButton) {
		for (int i = 0; i < toolsLayout.getChildCount(); i++) {
			toolsLayout.getChildAt(i).setSelected(false);
		}

		toolButton.setSelected(true);
	}

	public interface BottomBarCallback {
		FragmentManager getSupportFragmentManager();
		void switchTool(ToolType type);
		void hideKeyboard();
		boolean isKeyboardShown();
	}
}

