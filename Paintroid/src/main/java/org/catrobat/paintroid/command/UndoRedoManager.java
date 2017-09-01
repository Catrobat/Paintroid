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

package org.catrobat.paintroid.command;

import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Looper;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.implementation.TransformTool;
import org.catrobat.paintroid.ui.TopBar;

public final class UndoRedoManager {

	private static UndoRedoManager mInstance;
	private TopBar mTopBar;

	private UndoRedoManager() {

	}

	public static UndoRedoManager getInstance() {
		if (mInstance == null) {
			mInstance = new UndoRedoManager();
		}
		return mInstance;
	}

	public void setTopBar(TopBar topBar) {
		mTopBar = topBar;
	}

	public TopBar getTopBar() {
		return mTopBar;
	}

	public void performUndo() {
		final Layer layer = LayerListener.getInstance().getCurrentLayer();
		LayerCommand layerCommand = new LayerCommand(layer);
		final LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);

		float scale = PaintroidApplication.perspective.getScale();
		float surfaceTranslationX = PaintroidApplication.perspective.getSurfaceTranslationX();
		float surfaceTranslationY = PaintroidApplication.perspective.getSurfaceTranslationY();

		layerBitmapCommand.clearLayerBitmap();
		layerBitmapCommand.addCommandToUndoList();
		UndoRedoManager.getInstance().update();

		final Canvas canvas = PaintroidApplication.drawingSurface.getCanvas();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
					IndeterminateProgressDialog.getInstance().show();
				}
			}

			@Override
			protected Void doInBackground(Void... params) {
				for (Command command : layerBitmapCommand.getLayerCommands()) {
					command.run(canvas, layer);
				}

				// check for resize/rotate
				if (!layerBitmapCommand.getLayerUndoCommands().isEmpty()) {
					Command undoCommand = layerBitmapCommand.getLayerUndoCommands().get(0);
					if (undoCommand instanceof ResizeCommand) {
						TransformTool.undoResizeCommand(layer, (ResizeCommand) undoCommand);
					} else if (undoCommand instanceof RotateCommand) {
						TransformTool.undoRotateCommand(layer, (RotateCommand) undoCommand);
					}
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
				update();
				LayerListener.getInstance().refreshView();
				PaintroidApplication.drawingSurface.refreshDrawingSurface();
				IndeterminateProgressDialog.getInstance().dismiss();
			}
		}.execute();

		PaintroidApplication.perspective.setScale(scale);
		PaintroidApplication.perspective.setSurfaceTranslationX(surfaceTranslationX);
		PaintroidApplication.perspective.setSurfaceTranslationY(surfaceTranslationY);
	}

	public void performRedo() {
		final Layer layer = LayerListener.getInstance().getCurrentLayer();
		LayerCommand layerCommand = new LayerCommand(layer);
		LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);

		float scale = PaintroidApplication.perspective.getScale();
		float surfaceTranslationX = PaintroidApplication.perspective.getSurfaceTranslationX();
		float surfaceTranslationY = PaintroidApplication.perspective.getSurfaceTranslationY();

		final Command command = layerBitmapCommand.addCommandToRedoList();
		UndoRedoManager.getInstance().update();

		final Canvas canvas = PaintroidApplication.drawingSurface.getCanvas();
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
					IndeterminateProgressDialog.getInstance().show();
				}
			}

			@Override
			protected Void doInBackground(Void... params) {
				if (command != null)
					command.run(canvas, layer);

				// check for resize/rotate
				if (command instanceof ResizeCommand) {
					TransformTool.redoResizeCommand(layer, (ResizeCommand) command);
				} else if (command instanceof RotateCommand) {
					TransformTool.redoRotateCommand(layer, (RotateCommand) command);
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
				update();
				LayerListener.getInstance().refreshView();
				PaintroidApplication.drawingSurface.refreshDrawingSurface();
				IndeterminateProgressDialog.getInstance().dismiss();
			}
		}.execute();

		PaintroidApplication.perspective.setScale(scale);
		PaintroidApplication.perspective.setSurfaceTranslationX(surfaceTranslationX);
		PaintroidApplication.perspective.setSurfaceTranslationY(surfaceTranslationY);
	}

	public void update() {
		Layer currentLayer = LayerListener.getInstance().getCurrentLayer();
		LayerCommand layerCommand = new LayerCommand(currentLayer);
		LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager
				.getLayerBitmapCommand(layerCommand);

		updateUndoButton(layerBitmapCommand);
		updateRedoButton(layerBitmapCommand);
	}

	private void updateUndoButton(LayerBitmapCommand layerBitmapCommand) {
		if (layerBitmapCommand.getLayerCommands().size() != 0)
			PaintroidApplication.commandManager.enableUndo(true);
		else
			PaintroidApplication.commandManager.enableUndo(false);
		if (layerBitmapCommand.getLayerUndoCommands().size() != 0)
			PaintroidApplication.commandManager.enableRedo(true);
		else
			PaintroidApplication.commandManager.enableRedo(false);
	}

	private void updateRedoButton(LayerBitmapCommand layerBitmapCommand) {
		if (layerBitmapCommand.getLayerCommands().size() != 0)
			PaintroidApplication.commandManager.enableUndo(true);
		else
			PaintroidApplication.commandManager.enableUndo(false);
		if (layerBitmapCommand.getLayerUndoCommands().size() != 0)
			PaintroidApplication.commandManager.enableRedo(true);
		else
			PaintroidApplication.commandManager.enableRedo(false);
	}

}
