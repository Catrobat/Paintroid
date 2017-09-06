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
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
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

	private MainActivity mMainActivity;
	private LinearLayout mToolsLayout;
	private ToolType mCurrentToolType;
	private Toast mToolNameToast;

	public BottomBar(MainActivity mainActivity) {
		mMainActivity = mainActivity;

		if (PaintroidApplication.currentTool == null) {
			mCurrentToolType = ToolType.BRUSH;
			PaintroidApplication.currentTool = ToolFactory.createTool(mainActivity, mCurrentToolType);
		} else {
			mCurrentToolType = PaintroidApplication.currentTool.getToolType();
			Paint paint = PaintroidApplication.currentTool.getDrawPaint();
			PaintroidApplication.currentTool = ToolFactory.createTool(mainActivity, mCurrentToolType);
			PaintroidApplication.currentTool.setDrawPaint(paint);
		}


		getToolButtonByToolType(mCurrentToolType).setBackgroundResource(R.color.bottom_bar_button_activated);
		mToolsLayout = (LinearLayout) mainActivity.findViewById(R.id.tools_layout);

		setBottomBarListener();

		if (ENABLE_START_SCROLL_ANIMATION) {
			startBottomBarAnimation();
		}
	}

	private void delayedAnimateSelectedTool(int startDelay) {
		ImageButton button = getToolButtonByToolType(mCurrentToolType);
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
		final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) mMainActivity.findViewById(R.id.bottom_bar_scroll_view);
		final ScrollView verticalScrollView = (ScrollView) mMainActivity.findViewById(R.id.bottom_bar_landscape_scroll_view);
		final int animationDuration = 1000;
		int orientation = mMainActivity.getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT) {
			horizontalScrollView.post(new Runnable() {
				public void run() {
					int scrollToX = (int) (getToolButtonByToolType(mCurrentToolType).getX() - horizontalScrollView.getWidth() / 2.0f
							+ mMainActivity.getResources().getDimension(R.dimen.bottom_bar_button_width) / 2.0f);
					int scrollFromX = PaintroidApplication.isRTL ?
							horizontalScrollView.getChildAt(0).getLeft() :
							horizontalScrollView.getChildAt(0).getRight();
					horizontalScrollView.setScrollX(scrollFromX);
					ObjectAnimator.ofInt(horizontalScrollView, "scrollX", scrollToX).setDuration(animationDuration).start();
				}
			});
		} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			verticalScrollView.post(new Runnable() {
				public void run() {
					int scrollToY = (int) (getToolButtonByToolType(mCurrentToolType).getY() - verticalScrollView.getHeight() / 2.0f
							+ mMainActivity.getResources().getDimension(R.dimen.bottom_bar_landscape_button_height) / 2.0f);
					int scrollFromY = verticalScrollView.getChildAt(0).getBottom();
					verticalScrollView.setScrollY(scrollFromY);
					ObjectAnimator.ofInt(verticalScrollView, "scrollY", scrollToY).setDuration(animationDuration).start();
				}
			});
		}

		delayedAnimateSelectedTool(animationDuration);
	}

	private void setBottomBarListener() {
		for (int i = 0; i < mToolsLayout.getChildCount(); i++) {
			mToolsLayout.getChildAt(i).setOnClickListener(this);
			mToolsLayout.getChildAt(i).setOnLongClickListener(this);
		}

		setBottomBarScrollerListener();
	}

	private void setBottomBarScrollerListener() {
		final ImageView next = (ImageView) mMainActivity.findViewById(R.id.bottom_next);
		final ImageView previous = (ImageView) mMainActivity.findViewById(R.id.bottom_previous);

		BottomBarHorizontalScrollView mScrollView = ((BottomBarHorizontalScrollView) mMainActivity.findViewById(R.id.bottom_bar_scroll_view));
		if(mScrollView == null )
			return;
		mScrollView.setScrollStateListener(new BottomBarScrollListener(previous, next));
	}

	public void setTool(Tool tool) {
		mCurrentToolType = tool.getToolType();
		showToolChangeToast();
		setActivatedToolButton(mCurrentToolType);

		if (ENABLE_CENTER_SELECTED_TOOL) {
			scrollToSelectedTool(mCurrentToolType);
		}
	}

	private void scrollToSelectedTool(ToolType toolType) {
		int orientation = mMainActivity.getResources().getConfiguration().orientation;
		View toolButton = getToolButtonByToolType(toolType);

		if(orientation == Configuration.ORIENTATION_PORTRAIT) {
			HorizontalScrollView scrollView = (HorizontalScrollView) mMainActivity.findViewById(R.id.bottom_bar_scroll_view);
			scrollView.smoothScrollTo(
					(int) (toolButton.getX() - scrollView.getWidth() / 2.0f + toolButton.getWidth() / 2.0f),
					(int) toolButton.getY());
		}
		else if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
			ScrollView scrollView = (ScrollView)mMainActivity.findViewById(R.id.bottom_bar_landscape_scroll_view);
			scrollView.smoothScrollTo(
					(int) (toolButton.getX()),
					(int) (toolButton.getY() - scrollView.getHeight() / 2.0f + toolButton.getHeight() / 2.0f));
		}
	}

	private void showToolChangeToast() {
		if (mToolNameToast != null) {
			mToolNameToast.cancel();
		}

		mToolNameToast = Toast.makeText(mMainActivity, mMainActivity.getString(mCurrentToolType.getNameResource()), Toast.LENGTH_SHORT);
		mToolNameToast.setGravity(Gravity.TOP | Gravity.END, 0, SWITCH_TOOL_TOAST_Y_OFFSET);
		mToolNameToast.show();
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
			if(mMainActivity.isKeyboardShown()) {
				mMainActivity.hideKeyboard();
			} else {
				mMainActivity.switchTool(toolType);
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
				mMainActivity.getSupportFragmentManager(),
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
		return (ImageButton) mMainActivity.findViewById(toolType.getToolButtonID());
	}

	private void setActivatedToolButton(ToolType toolType) {
		for (int i = 0; i < mToolsLayout.getChildCount(); i++) {
			mToolsLayout.getChildAt(i).setBackgroundResource(R.color.transparent);
		}

		getToolButtonByToolType(toolType).setBackgroundResource(R.color.bottom_bar_button_activated);
	}


}

