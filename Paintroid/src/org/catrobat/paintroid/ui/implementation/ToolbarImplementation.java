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
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Toolbar;

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

public class ToolbarImplementation extends Observable implements Toolbar,
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
	protected Tool currentTool;
	private Tool mPreviousTool;
	protected MainActivity mainActivity;

	private Toast mToast;

	public ToolbarImplementation(MainActivity mainActivity,
			boolean openedFromCatroid) {
		this.mainActivity = mainActivity;
		currentTool = new DrawTool(mainActivity, ToolType.BRUSH);
		PaintroidApplication.CURRENT_TOOL = currentTool;

		mUndoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_status_undo);
		mUndoButton.setOnTouchListener(this);

		mRedoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_status_redo);
		mRedoButton.setOnTouchListener(this);

		mColorButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_color_parameter);
		mColorButton.setOnTouchListener(this);

		mToolButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_status_tool);
		mToolButton.setOnTouchListener(this);

		setToolSwitchBackground(R.drawable.icon_menu_move);
		drawingSurface = (DrawingSurfaceImplementation) mainActivity
				.findViewById(R.id.drawingSurfaceView);
	}

	@Override
	public Tool getCurrentTool() {
		return this.currentTool;
	}

	@Override
	public void setTool(Tool tool) {

		// ignore to set the same tool again. except stamptool -> reselect =
		// reset selection.
		if ((tool.getToolType() == currentTool.getToolType())
				&& (tool.getToolType() != ToolType.STAMP)) {
			return;
		}

		if (((tool.getToolType() == ToolType.MOVE) || (tool.getToolType() == ToolType.ZOOM))
				&& (!((currentTool.getToolType() == ToolType.MOVE) || (currentTool
						.getToolType() == ToolType.ZOOM)))) {
			mPreviousTool = currentTool;
			setToolSwitchBackground(mPreviousTool
					.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL));

		} else {
			mPreviousTool = null;
			setToolSwitchBackground(R.drawable.icon_menu_move);
		}
		this.currentTool = tool;

		Animation switchAnimation = AnimationUtils.loadAnimation(mainActivity,
				R.anim.fade_in);
		mToolButton.setAnimation(switchAnimation);
		mToolButton.setImageResource(currentTool
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
		if (mToast != null) {
			mToast.cancel();
		}

		mToast = Toast.makeText(mainActivity, mainActivity
				.getString(currentTool.getToolType().getNameResource()),
				Toast.LENGTH_SHORT);
		mToast.setGravity(Gravity.TOP | Gravity.RIGHT, 0,
				SWITCH_TOOL_TOAST_Y_OFFSET);
		mToast.show();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {

		case R.id.btn_status_undo:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mUndoButton.setBackgroundResource(R.color.abs__holo_blue_light);
				PaintroidApplication.COMMAND_MANAGER.undo();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mUndoButton.setBackgroundResource(0);
			}

			return true;

		case R.id.btn_status_redo:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				mRedoButton.setBackgroundResource(R.color.abs__holo_blue_light);
				PaintroidApplication.COMMAND_MANAGER.redo();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				mRedoButton.setBackgroundResource(0);
			}
			return true;

		case R.id.btn_status_tool:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ToolType nextTool = (mPreviousTool == null) ? ToolType.MOVE
						: mPreviousTool.getToolType();
				mainActivity.switchTool(nextTool);
			}
			return true;
		case R.id.btn_color_parameter:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				ColorPickerDialog.getInstance().show();
				ColorPickerDialog.getInstance().setInitialColor(
						currentTool.getDrawPaint().getColor());
			}
			return true;

		default:
			return false;
		}
	}
}
