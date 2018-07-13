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

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.ui.button.ColorButton;

public class TopBar implements View.OnClickListener, ColorPickerDialog.OnColorPickedListener {
	private ImageButton undoButton;
	private ImageButton redoButton;
	private ColorButton colorButton;
	private DrawerLayout layerDrawer;

	public TopBar(MainActivity mainActivity) {
		undoButton = mainActivity.findViewById(R.id.btn_top_undo);
		redoButton = mainActivity.findViewById(R.id.btn_top_redo);
		colorButton = mainActivity.findViewById(R.id.btn_top_color);
		ImageButton layerButton = mainActivity.findViewById(R.id.btn_top_layers);
		layerDrawer = mainActivity.findViewById(R.id.drawer_layout);

		undoButton.setOnClickListener(this);
		redoButton.setOnClickListener(this);
		colorButton.setOnClickListener(this);
		ColorPickerDialog.getInstance().addOnColorPickedListener(this);
		layerButton.setOnClickListener(this);

		refreshButtons();

		colorButton.colorChanged(BaseTool.BITMAP_PAINT.getColor());
	}

	private void onUndoClick() {
		if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.hide();
			return;
		}
		PaintroidApplication.commandManager.undo();
	}

	private void onRedoClick() {
		if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.hide();
			return;
		}
		PaintroidApplication.commandManager.redo();
	}

	private void onColorClick() {
		Tool currentTool = PaintroidApplication.currentTool;
		if (!currentTool.getToolType().isColorChangeAllowed()) {
			return;
		}
		ColorPickerDialog.getInstance().show();
	}

	public void refreshButtons() {
		undoButton.setEnabled(PaintroidApplication.commandManager.isUndoAvailable());
		redoButton.setEnabled(PaintroidApplication.commandManager.isRedoAvailable());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_top_undo:
				onUndoClick();
				break;
			case R.id.btn_top_redo:
				onRedoClick();
				break;
			case R.id.btn_top_color:
				onColorClick();
				break;
			case R.id.btn_top_layers:
				layerDrawer.openDrawer(Gravity.END);
				break;
			default:
				break;
		}
	}

	@Override
	public void colorChanged(int color) {
		colorButton.colorChanged(color);
	}
}
