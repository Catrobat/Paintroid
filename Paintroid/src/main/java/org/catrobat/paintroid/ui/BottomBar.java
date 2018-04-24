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

package org.catrobat.paintroid.ui;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.InfoDialog;
import org.catrobat.paintroid.listener.BottomBarScrollListener;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;

public class BottomBar implements View.OnClickListener, View.OnLongClickListener {

	private static final int SWITCH_TOOL_TOAST_Y_OFFSET = (int) NavigationDrawerMenuActivity.ACTION_BAR_HEIGHT + 25;
	private static final boolean ENABLE_CENTER_SELECTED_TOOL = true;
	private static final boolean ENABLE_START_SCROLL_ANIMATION = true;

	private MainActivity mainActivity;
	private LinearLayout toolsLayout;
	private ToolType currentToolType;
	private Toast toolNameToast;

	public BottomBar(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		toolsLayout = (LinearLayout) mainActivity.findViewById(R.id.tools_layout);

		Bundle bundle = new Bundle();
		if (PaintroidApplication.currentTool == null) {
			currentToolType = ToolType.BRUSH;
			PaintroidApplication.currentTool = ToolFactory.createTool(mainActivity, currentToolType);
			PaintroidApplication.currentTool.startTool();
		} else {
			currentToolType = PaintroidApplication.currentTool.getToolType();
			Paint paint = PaintroidApplication.currentTool.getDrawPaint();
			PaintroidApplication.currentTool.leaveTool();
			PaintroidApplication.currentTool.onSaveInstanceState(bundle);
			PaintroidApplication.currentTool = ToolFactory.createTool(mainActivity, currentToolType);
			PaintroidApplication.currentTool.onRestoreInstanceState(bundle);
			PaintroidApplication.currentTool.startTool();
			PaintroidApplication.currentTool.setDrawPaint(paint);
		}

		getToolButtonByToolType(currentToolType).setSelected(true);
		setBottomBarListener();

		if (ENABLE_START_SCROLL_ANIMATION) {
			startBottomBarAnimation();
		}
	}

	private void delayedAnimateSelectedTool(int startDelay) {
		final ImageButton button = getToolButtonByToolType(currentToolType);
		final Drawable backgroundDrawable = button.getBackground();
		int color = ContextCompat.getColor(button.getContext(), R.color.bottom_bar_button_activated);
		int fadedColor = color & 0x00ffffff;
		ValueAnimator valueAnimator = ObjectAnimator.ofInt(button, "backgroundColor", color, fadedColor);
		valueAnimator.setEvaluator(new ArgbEvaluator());
		valueAnimator.setInterpolator(new LinearInterpolator());
		valueAnimator.setDuration(500);
		valueAnimator.setRepeatCount(5);
		valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
		valueAnimator.setStartDelay(startDelay);
		valueAnimator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				button.setBackground(backgroundDrawable);
				if (PaintroidApplication.currentTool != null) {
					setActivatedToolButton(PaintroidApplication.currentTool.getToolType());
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}
		});
		valueAnimator.start();
	}

	private void startBottomBarAnimation() {
		final ViewGroup scrollView = (ViewGroup) mainActivity.findViewById(R.id.bottom_bar_scroll_view);
		final int animationDuration = 1000;
		final Resources resources = mainActivity.getResources();
		final Configuration configuration = resources.getConfiguration();
		final int orientation = configuration.orientation;
		final float buttonHeight = resources.getDimension(R.dimen.bottom_bar_landscape_button_height);
		final float buttonWidth = resources.getDimension(R.dimen.bottom_bar_button_width);
		final boolean isRTL = (configuration.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL);

		scrollView.post(new Runnable() {
			public void run() {
				ObjectAnimator animator;
				if (orientation == Configuration.ORIENTATION_PORTRAIT) {
					int scrollToX = (int) (getToolButtonByToolType(currentToolType).getX()
							- scrollView.getWidth() / 2.0f
							+ buttonWidth / 2.0f);
					int scrollFromX = isRTL
							? scrollView.getChildAt(0).getLeft()
							: scrollView.getChildAt(0).getRight();
					scrollView.setScrollX(scrollFromX);
					animator = ObjectAnimator.ofInt(scrollView, "scrollX", scrollToX);
				} else {
					int scrollToY = (int) (getToolButtonByToolType(currentToolType).getY()
							- scrollView.getHeight() / 2.0f
							+ buttonHeight / 2.0f);
					int scrollFromY = scrollView.getChildAt(0).getBottom();
					scrollView.setScrollY(scrollFromY);
					animator = ObjectAnimator.ofInt(scrollView, "scrollY", scrollToY);
				}
				animator.setDuration(animationDuration).start();
			}
		});

		delayedAnimateSelectedTool(animationDuration);
	}

	private void setBottomBarListener() {
		for (int i = 0; i < toolsLayout.getChildCount(); i++) {
			toolsLayout.getChildAt(i).setOnClickListener(this);
			toolsLayout.getChildAt(i).setOnLongClickListener(this);
		}

		setBottomBarScrollerListener();
	}

	private void setBottomBarScrollerListener() {
		final View next = mainActivity.findViewById(R.id.bottom_next);
		final View previous = mainActivity.findViewById(R.id.bottom_previous);

		if (mainActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			BottomBarHorizontalScrollView mScrollView = ((BottomBarHorizontalScrollView) mainActivity.findViewById(R.id.bottom_bar_scroll_view));
			mScrollView.setScrollStateListener(new BottomBarScrollListener(previous, next));
		}
	}

	public void setTool(Tool tool) {
		currentToolType = tool.getToolType();
		showToolChangeToast();
		setActivatedToolButton(currentToolType);

		if (ENABLE_CENTER_SELECTED_TOOL) {
			scrollToSelectedTool(currentToolType);
		}
	}

	private void scrollToSelectedTool(ToolType toolType) {
		int orientation = mainActivity.getResources().getConfiguration().orientation;
		View toolButton = getToolButtonByToolType(toolType);
		View scrollView = mainActivity.findViewById(R.id.bottom_bar_scroll_view);

		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			HorizontalScrollView horizontalScrollView = (HorizontalScrollView) scrollView;
			horizontalScrollView.smoothScrollTo(
					(int) (toolButton.getX()
							- scrollView.getWidth() / 2.0f
							+ toolButton.getWidth() / 2.0f),
					(int) toolButton.getY());
		} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			ScrollView verticalScrollView = (ScrollView) scrollView;
			verticalScrollView.smoothScrollTo(
					(int) (toolButton.getX()),
					(int) (toolButton.getY()
							- scrollView.getHeight() / 2.0f
							+ toolButton.getHeight() / 2.0f));
		}
	}

	private void showToolChangeToast() {
		toolNameToast = ToastFactory.makeText(mainActivity, currentToolType.getNameResource(), Toast.LENGTH_SHORT);
		toolNameToast.setGravity(Gravity.TOP | Gravity.END, 0, SWITCH_TOOL_TOAST_Y_OFFSET);
		toolNameToast.show();
	}

	@Override
	public void onClick(View view) {
		ToolType toolType = findToolByView(view);
		if (toolType != null) {
			onToolClick(toolType);
		}
	}

	private void onToolClick(ToolType toolType) {
		if (PaintroidApplication.currentTool.getToolType() != toolType) {
			if (mainActivity.isKeyboardShown()) {
				mainActivity.hideKeyboard();
			} else {
				mainActivity.switchTool(toolType);
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
				mainActivity.getSupportFragmentManager(),
				"helpdialogfragmenttag");
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

	private ImageButton getToolButtonByToolType(ToolType toolType) {
		return (ImageButton) mainActivity.findViewById(toolType.getToolButtonID());
	}

	private void setActivatedToolButton(ToolType toolType) {
		for (int i = 0; i < toolsLayout.getChildCount(); i++) {
			toolsLayout.getChildAt(i).setSelected(false);
		}

		getToolButtonByToolType(toolType).setSelected(true);
	}
}

