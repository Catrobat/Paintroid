/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.ui.implementation;

import java.util.Observable;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.ui.Toolbar;
import at.tugraz.ist.paintroid.ui.button.AttributeButton;

public class ToolbarImplementation extends Observable implements Toolbar, OnClickListener, OnLongClickListener {

	protected TextView toolButton;
	protected AttributeButton attributeButton1;
	protected AttributeButton attributeButton2;
	protected Button undoButton;
	protected DrawingSurfaceView drawingSurface;
	protected Tool currentTool;
	protected MainActivity mainActivity;

	public ToolbarImplementation(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		currentTool = new DrawTool(mainActivity);

		toolButton = (TextView) mainActivity.findViewById(R.id.btn_Tool);
		toolButton.setOnClickListener(this);
		toolButton.setOnLongClickListener(this);
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_menu_more_brush_64);
		toolButton.setBackgroundResource(R.drawable.attribute_button_selector);

		attributeButton1 = (AttributeButton) mainActivity.findViewById(R.id.btn_Parameter1);
		attributeButton1.setToolbar(this);
		attributeButton2 = (AttributeButton) mainActivity.findViewById(R.id.btn_Parameter2);
		attributeButton2.setToolbar(this);

		undoButton = (Button) mainActivity.findViewById(R.id.btn_Undo);
		undoButton.setOnClickListener(this);
		undoButton.setOnLongClickListener(this);
		undoButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.undo64);
		undoButton.setBackgroundResource(R.drawable.attribute_button_selector);

		drawingSurface = (DrawingSurfaceView) mainActivity.findViewById(R.id.drawingSurfaceView);
	}

	@Override
	public boolean onLongClick(View view) {
		// TODO
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_Tool:
			mainActivity.callToolMenu();
			break;
		case R.id.btn_Undo:
			drawingSurface.undo();
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
