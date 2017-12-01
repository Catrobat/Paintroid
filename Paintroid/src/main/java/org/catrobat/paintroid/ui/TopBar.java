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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

public class TopBar extends Observable implements OnTouchListener, OnUpdateTopBarListener {
	protected MainActivity mainActivity;
	private ImageButton undoButton;
	private ImageButton redoButton;
	private ColorButton colorButton;
	private ImageButton layerButton;
	private DrawerLayout layerDrawer;
	private boolean undoEnabled;
	private boolean redoEnabled;

	public TopBar(MainActivity mainActivity) {
		this.mainActivity = mainActivity;

		undoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_top_undo);
		undoButton.setOnTouchListener(this);

		redoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_top_redo);
		redoButton.setOnTouchListener(this);

		colorButton = (ColorButton) mainActivity
				.findViewById(R.id.btn_top_color);
		colorButton.setOnTouchListener(this);
		ColorPickerDialog.getInstance().addOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				colorButton.colorChanged(color);
			}
		});

		layerButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_top_layers);
		layerButton.setOnTouchListener(this);

		layerDrawer = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);

		int icon;
		if (PaintroidApplication.layerOperationsCommandList != null) {
			LayerBitmapCommand layerBitmapCommand = getCurrentLayerBitmapCommand();
			if (layerBitmapCommand != null) {
				icon = (layerBitmapCommand.moreCommands()) ? R.drawable.icon_menu_undo : R.drawable.icon_menu_undo_disabled;
				toggleUndo(icon);
				icon = (!layerBitmapCommand.getLayerUndoCommands().isEmpty()) ? R.drawable.icon_menu_redo : R.drawable.icon_menu_redo_disabled;
				toggleRedo(icon);
			}
		} else {
			onUndoEnabled(!PaintroidApplication.commandManager.isUndoCommandListEmpty());
			onRedoEnabled(!PaintroidApplication.commandManager.isRedoCommandListEmpty());
			icon = !(PaintroidApplication.commandManager.isUndoCommandListEmpty()) ? R.drawable.icon_menu_undo : R.drawable.icon_menu_undo_disabled;
			toggleUndo(icon);
			icon = !(PaintroidApplication.commandManager.isRedoCommandListEmpty()) ? R.drawable.icon_menu_redo : R.drawable.icon_menu_redo_disabled;
			toggleRedo(icon);
		}

		UndoRedoManager.getInstance().setTopBar(this);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {
			case R.id.btn_top_undo:
				onUndoTouch(event);
				return true;
			case R.id.btn_top_redo:
				onRedoTouch(event);
				return true;
			case R.id.btn_top_color:
				onColorTouch(event);
				return true;
			case R.id.btn_top_layers:
				layerDrawer.openDrawer(Gravity.END);
				return true;
			default:
				return false;
		}
	}

	private void onUndoTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
				PaintroidApplication.currentTool.hide();
				return;
			}
			UndoRedoManager.getInstance().performUndo();
		}
	}

	private void onRedoTouch(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (PaintroidApplication.currentTool.getToolOptionsAreShown()) {
				PaintroidApplication.currentTool.hide();
				return;
			}
			UndoRedoManager.getInstance().performRedo();
		}
	}

	private void onColorTouch(MotionEvent event) {
		Tool currentTool = PaintroidApplication.currentTool;
		if ((event.getAction() == MotionEvent.ACTION_DOWN)
				&& currentTool.getToolType().isColorChangeAllowed()) {
			ColorPickerDialog.getInstance().show();
			ColorPickerDialog.getInstance().setInitialColor(
					currentTool.getDrawPaint().getColor());
		}
	}

	public void toggleUndo(final int undoIcon) {
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				undoButton.setImageResource(undoIcon);
			}
		});
	}

	public void toggleRedo(final int redoIcon) {
		mainActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				redoButton.setImageResource(redoIcon);
			}
		});
	}

	@Override
	public void onUndoEnabled(boolean enabled) {
		if (undoEnabled != enabled) {
			undoEnabled = enabled;
			int icon = (undoEnabled) ? R.drawable.icon_menu_undo : R.drawable.icon_menu_undo_disabled;
			toggleUndo(icon);
		}
	}

	@Override
	public void onRedoEnabled(boolean enabled) {
		if (redoEnabled != enabled) {
			redoEnabled = enabled;
			int icon = (redoEnabled) ? R.drawable.icon_menu_redo : R.drawable.icon_menu_redo_disabled;
			toggleRedo(icon);
		}
	}

	private LayerBitmapCommand getCurrentLayerBitmapCommand() {
		Layer currentLayer = LayerListener.getInstance().getCurrentLayer();
		LayerCommand layerCommand = new LayerCommand(currentLayer);
		return PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
	}

	public ImageButton getUndoButton() {
		return undoButton;
	}

	public ImageButton getRedoButton() {
		return redoButton;
	}
}
