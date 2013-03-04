/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.implementation;

import java.util.Observable;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.MenuFileActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Statusbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

public class StatusbarImplementation extends Observable implements Statusbar,
		OnTouchListener {

	public static enum ToolButtonIDs {
		BUTTON_ID_TOOL, BUTTON_ID_PARAMETER_TOP, BUTTON_ID_PARAMETER_BOTTOM_1, BUTTON_ID_PARAMETER_BOTTOM_2
	}

	private static final int SWITCH_TOOL_TOAST_Y_OFFSET = (int) MenuFileActivity.ACTION_BAR_HEIGHT + 25;
	private static final int SWITCH_TOOL_BACKGROUND_ALPHA = 50;

	private ImageButton mUndoButton;
	private ImageButton mRedoButton;
	private ImageButton mColorButton;
	private ImageButton mToolButton;

	protected DrawingSurface drawingSurface;
	protected Tool mCurrentTool;
	private Tool mPreviousTool;
	protected MainActivity mainActivity;

	private Toast mToolNameToast;
	private boolean mUndoDisabled;
	private boolean mRedoDisabled;

	public StatusbarImplementation(MainActivity mainActivity,
			boolean openedFromCatroid) {
		this.mainActivity = mainActivity;
		mCurrentTool = new DrawTool(mainActivity, ToolType.BRUSH);
		PaintroidApplication.CURRENT_TOOL = mCurrentTool;

		mUndoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_status_undo);
		mUndoButton.setOnTouchListener(this);

		mRedoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_status_redo);
		mRedoButton.setOnTouchListener(this);

		mColorButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_status_color);
		mColorButton.setOnTouchListener(this);

		mToolButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_status_tool);
		mToolButton.setOnTouchListener(this);

		setToolSwitchBackground(R.drawable.icon_menu_move);
		drawingSurface = (DrawingSurfaceImplementation) mainActivity
				.findViewById(R.id.drawingSurfaceView);

		UndoRedoManager.getInstance().setStatusbar(this);
	}

	@Override
	public Tool getCurrentTool() {
		return this.mCurrentTool;
	}

	@Override
	public void setTool(Tool tool) {

		// ignore to set the same tool again. except stamptool -> reselect =
		// reset selection.
		if ((tool.getToolType() == mCurrentTool.getToolType())
				&& (tool.getToolType() != ToolType.STAMP)) {
			return;
		}

		if (((tool.getToolType() == ToolType.MOVE) || (tool.getToolType() == ToolType.ZOOM))
				&& (!((mCurrentTool.getToolType() == ToolType.MOVE) || (mCurrentTool
						.getToolType() == ToolType.ZOOM)))) {
			mPreviousTool = mCurrentTool;
			setToolSwitchBackground(mPreviousTool
					.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL));

		} else {
			mPreviousTool = null;
			setToolSwitchBackground(R.drawable.icon_menu_move);
		}
		this.mCurrentTool = tool;

		Animation switchAnimation = AnimationUtils.loadAnimation(mainActivity,
				R.anim.fade_in);
		mToolButton.setAnimation(switchAnimation);
		mToolButton.setImageResource(mCurrentTool
				.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL));

		showToolChangeToast();

		super.setChanged();
		super.notifyObservers();
	}

	private void setToolSwitchBackground(int backgroundResource) {
		Bitmap bitmap = BitmapFactory.decodeResource(
				mainActivity.getResources(), backgroundResource);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(
				mainActivity.getResources(), bitmap);
		bitmapDrawable.setAlpha(SWITCH_TOOL_BACKGROUND_ALPHA);
		mToolButton.setBackgroundDrawable(bitmapDrawable);
	}

	private void showToolChangeToast() {
		if (mToolNameToast != null) {
			mToolNameToast.cancel();
		}

		mToolNameToast = Toast.makeText(mainActivity, mainActivity
				.getString(mCurrentTool.getToolType().getNameResource()),
				Toast.LENGTH_SHORT);
		mToolNameToast.setGravity(Gravity.TOP | Gravity.RIGHT, 0,
				SWITCH_TOOL_TOAST_Y_OFFSET);
		mToolNameToast.show();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {
		case R.id.btn_status_undo:
			onUndoTouch(event);
			return true;
		case R.id.btn_status_redo:
			onRedoTouch(event);
			return true;
		case R.id.btn_status_tool:
			onToolSwitchTouch(event);
			return true;
		case R.id.btn_status_color:
			onColorTouch(event);
			return true;
		default:
			return false;
		}
	}

	private void onUndoTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (!mUndoDisabled) {
				mUndoButton.setBackgroundResource(R.color.abs__holo_blue_light);
			}
			PaintroidApplication.COMMAND_MANAGER.undo();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			mUndoButton.setBackgroundResource(0);
		}
	}

	private void onRedoTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (!mRedoDisabled) {
				mRedoButton.setBackgroundResource(R.color.abs__holo_blue_light);
			}
			PaintroidApplication.COMMAND_MANAGER.redo();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			mRedoButton.setBackgroundResource(0);
		}
	}

	private void onToolSwitchTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			ToolType nextTool = (mPreviousTool == null) ? ToolType.MOVE
					: mPreviousTool.getToolType();
			mainActivity.switchTool(nextTool);
		}
	}

	private void onColorTouch(MotionEvent event) {
		if ((event.getAction() == MotionEvent.ACTION_DOWN)
				&& mCurrentTool.getToolType().isColorChangeAllowed()) {
			ColorPickerDialog.getInstance().show();
			ColorPickerDialog.getInstance().setInitialColor(
					mCurrentTool.getDrawPaint().getColor());
		}
	}

	public void toggleUndo(final int undoIcon) {
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mUndoButton.setImageResource(undoIcon);
			}
		});
	}

	public void toggleRedo(final int redoIcon) {
		mainActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mRedoButton.setImageResource(redoIcon);
			}
		});

	}

	public void enableUndo() {
		mUndoDisabled = false;
	}

	public void disableUndo() {
		mUndoDisabled = true;
	}

	public void enableRedo() {
		mRedoDisabled = false;
	}

	public void disableRedo() {
		mRedoDisabled = true;
	}
}
