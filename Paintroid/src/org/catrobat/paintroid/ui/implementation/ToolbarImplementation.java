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
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.DialogHelp;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Toolbar;
import org.catrobat.paintroid.ui.button.ToolbarButton;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;

public class ToolbarImplementation extends Observable implements Toolbar, OnClickListener, OnLongClickListener {

	private Button mUndoButton;
	private Button mRedoButton;
	private ToolbarButton mAttributeButton1;
	private ToolbarButton mAttributeButton2;
	private ToolbarButton mToolButton;

	protected DrawingSurface drawingSurface;
	protected Tool currentTool;
	protected MainActivity mainActivity;

	public ToolbarImplementation(MainActivity mainActivity, boolean openedFromCatroid) {
		this.mainActivity = mainActivity;
		currentTool = new DrawTool(mainActivity, ToolType.BRUSH);
		PaintroidApplication.CURRENT_TOOL = currentTool;

		mUndoButton = (Button) mainActivity.findViewById(R.id.btn_status_undo);
		mUndoButton.setOnClickListener(this);

		mRedoButton = (Button) mainActivity.findViewById(R.id.btn_status_redo);
		if (openedFromCatroid) {
			mRedoButton.setVisibility(Button.GONE);
		} else {
			mRedoButton.setOnClickListener(this);
		}

		mAttributeButton1 = (ToolbarButton) mainActivity.findViewById(R.id.btn_status_parameter1);
		mAttributeButton1.setToolbar(this);
		mAttributeButton2 = (ToolbarButton) mainActivity.findViewById(R.id.btn_status_parameter2);
		mAttributeButton2.setToolbar(this);

		mToolButton = (ToolbarButton) mainActivity.findViewById(R.id.btn_status_tool);
		mToolButton.setOnClickListener(this);
		mToolButton.setOnLongClickListener(this);
		mToolButton.setToolbar(this);

		drawingSurface = (DrawingSurfaceImplementation) mainActivity.findViewById(R.id.drawingSurfaceView);
	}

	@Override
	public boolean onLongClick(View view) {
		// ToolType type = PaintroidApplication.CURRENT_TOOL.getToolType();
		Dialog dialogHelp = new DialogHelp(mainActivity, R.id.btn_status_tool,
				PaintroidApplication.CURRENT_TOOL.getToolType());
		dialogHelp.show();
		return true;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_status_undo:
				PaintroidApplication.COMMAND_MANAGER.undo();
				break;
			case R.id.btn_status_redo:
				PaintroidApplication.COMMAND_MANAGER.redo();
				break;
			case R.id.btn_status_tool:
				mainActivity.openToolDialog();
				break;

			default:
				break;
		}
	}

	@Override
	public Tool getCurrentTool() {
		return this.currentTool;
	}

	@Override
	public void setTool(Tool tool) {
		this.currentTool = tool;
		super.setChanged();
		super.notifyObservers();
	}
}
