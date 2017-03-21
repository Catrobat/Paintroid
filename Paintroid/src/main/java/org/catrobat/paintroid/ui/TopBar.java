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

import android.content.res.Configuration;
import android.os.AsyncTask;
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
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.eventlistener.OnUpdateTopBarListener;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;

import java.util.Observable;

public class TopBar extends Observable implements OnTouchListener, OnUpdateTopBarListener {

	public static enum ToolButtonIDs {
		BUTTON_ID_TOOL, BUTTON_ID_PARAMETER_TOP
	}

	private ImageButton mUndoButton;
	private ImageButton mRedoButton;
	private ImageButton mColorButton;
	private ImageButton mLayerButton;
	private DrawerLayout mLayerDrawer;

	protected MainActivity mainActivity;

	private boolean mUndoEnabled;
	private boolean mRedoEnabled;

	public TopBar(MainActivity mainActivity, boolean openedFromCatroid) {
		this.mainActivity = mainActivity;

		mUndoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_top_undo);
		mUndoButton.setOnTouchListener(this);

		mRedoButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_top_redo);
		mRedoButton.setOnTouchListener(this);

		mColorButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_top_color);
		mColorButton.setOnTouchListener(this);

		mLayerButton = (ImageButton) mainActivity
				.findViewById(R.id.btn_top_layers);
		mLayerButton.setOnTouchListener(this);

		mLayerDrawer = (DrawerLayout) mainActivity.findViewById(R.id.drawer_layout);


		int icon;
		if(PaintroidApplication.layerOperationsCommandList != null) {
			Layer currentLayer = LayerListener.getInstance().getCurrentLayer();
			LayerCommand layerCommand = new LayerCommand(currentLayer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);

			icon = (layerBitmapCommand.moreCommands()) ? R.drawable.icon_menu_undo : R.drawable.icon_menu_undo_disabled;
			toggleUndo(icon);
			icon = (!layerBitmapCommand.getLayerUndoCommands().isEmpty()) ? R.drawable.icon_menu_redo : R.drawable.icon_menu_redo_disabled;
			toggleRedo(icon);
		}
		else {
			onUndoEnabled(!((CommandManagerImplementation) PaintroidApplication.commandManager).isUndoCommandListEmpty());
			onRedoEnabled(!((CommandManagerImplementation) PaintroidApplication.commandManager).isRedoCommandListEmpty());
			icon = !(((CommandManagerImplementation) PaintroidApplication.commandManager).isUndoCommandListEmpty()) ? R.drawable.icon_menu_undo : R.drawable.icon_menu_undo_disabled;
			toggleUndo(icon);
			icon = !(((CommandManagerImplementation) PaintroidApplication.commandManager).isRedoCommandListEmpty()) ? R.drawable.icon_menu_redo : R.drawable.icon_menu_redo_disabled;
			toggleRedo(icon);
		}



		UndoRedoManager.getInstance().setStatusbar(this);

	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {
			case R.id.btn_top_undo:
				PaintroidApplication.currentTool = ToolFactory.createTool(mainActivity, ToolType.UNDO);
				//onUndoTouch(event);
				return true;
			case R.id.btn_top_redo:
				PaintroidApplication.currentTool = ToolFactory.createTool(mainActivity, ToolType.REDO);
				//onRedoTouch(event);
				return true;
			case R.id.btn_top_color:
				onColorTouch(event);
				return true;
			case R.id.btn_top_layers:
				mLayerDrawer.openDrawer(Gravity.RIGHT);
				return true;
			default:
				return false;
		}
	}

	private void onUndoTouch(MotionEvent event) {
		/*if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (!mUndoEnabled) {
				mUndoButton.setBackgroundResource(R.color.holo_blue_bright);
			}
			PaintroidApplication.commandManager.undo();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			mUndoButton.setBackgroundResource(0);
		} */
	}

	private void onRedoTouch(MotionEvent event) {
	/*	if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (!mRedoEnabled) {
				mRedoButton.setBackgroundResource(R.color.holo_blue_bright);
			}
			PaintroidApplication.commandManager.redo();
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			mRedoButton.setBackgroundResource(0);
		} */
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
		mUndoEnabled = false;
	}

	public void disableUndo() {
		mUndoEnabled = true;
	}

	public void enableRedo() {
		mRedoEnabled = false;
	}

	public void disableRedo() {
		mRedoEnabled = true;
	}

	@Override
	public void onUndoEnabled(boolean enabled) {
		if (mUndoEnabled != enabled) {
			mUndoEnabled = enabled;
			int icon = (mUndoEnabled) ? R.drawable.icon_menu_undo : R.drawable.icon_menu_undo_disabled;
			toggleUndo(icon);
		}
	}

	@Override
	public void onRedoEnabled(boolean enabled) {
		if (mRedoEnabled != enabled) {
			mRedoEnabled = enabled;
			int icon = (mRedoEnabled) ? R.drawable.icon_menu_redo : R.drawable.icon_menu_redo_disabled;
			toggleRedo(icon);
		}
	}
}
