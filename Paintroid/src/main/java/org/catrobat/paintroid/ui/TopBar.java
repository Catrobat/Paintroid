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

package org.catrobat.paintroid.ui;

import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.eventlistener.OnUpdateTopBarListener;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.button.ColorButton;

import java.util.Observable;

public class TopBar extends Observable implements View.OnClickListener, OnUpdateTopBarListener, ColorPickerDialog.OnColorPickedListener {
	private ImageButton undoButton;
	private ImageButton redoButton;
	private ColorButton colorButton;
	private DrawerLayout layerDrawer;

	public TopBar(MainActivity mainActivity) {
		undoButton = (ImageButton) mainActivity.findViewById(R.id.btn_top_undo);
		redoButton = (ImageButton) mainActivity.findViewById(R.id.btn_top_redo);
		colorButton = (ColorButton) mainActivity.findViewById(R.id.btn_top_color);
		ImageButton layerButton = (ImageButton) mainActivity.findViewById(R.id.btn_top_layers);
		layerDrawer = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);

		undoButton.setOnClickListener(this);
		redoButton.setOnClickListener(this);
		colorButton.setOnClickListener(this);
		ColorPickerDialog.getInstance().addOnColorPickedListener(this);
		layerButton.setOnClickListener(this);

		if (PaintroidApplication.layerOperationsCommandList != null) {
			LayerBitmapCommand layerBitmapCommand = getCurrentLayerBitmapCommand();
			if (layerBitmapCommand != null) {
				onUndoEnabled(layerBitmapCommand.moreCommands());
				onRedoEnabled(!layerBitmapCommand.getLayerUndoCommands().isEmpty());
			}
		} else {
			onUndoEnabled(!PaintroidApplication.commandManager.isUndoCommandListEmpty());
			onRedoEnabled(!PaintroidApplication.commandManager.isRedoCommandListEmpty());
		}
	}

	private void onUndoClick() {
		if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.hide();
			return;
		}
		UndoRedoManager.getInstance().performUndo();
	}

	private void onRedoClick() {
		if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
			PaintroidApplication.currentTool.hide();
			return;
		}
		UndoRedoManager.getInstance().performRedo();
	}

	private void onColorClick() {
		Tool currentTool = PaintroidApplication.currentTool;
		if (!currentTool.getToolType().isColorChangeAllowed()) {
			return;
		}
		ColorPickerDialog.getInstance().show();
		ColorPickerDialog.getInstance().setInitialColor(currentTool.getDrawPaint().getColor());
	}

	@Override
	public void onUndoEnabled(boolean enabled) {
		undoButton.setEnabled(enabled);
	}

	@Override
	public void onRedoEnabled(boolean enabled) {
		redoButton.setEnabled(enabled);
	}

	private LayerBitmapCommand getCurrentLayerBitmapCommand() {
		Layer currentLayer = LayerListener.getInstance().getCurrentLayer();
		LayerCommand layerCommand = new LayerCommand(currentLayer);
		return PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
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
