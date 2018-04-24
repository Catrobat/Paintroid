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

import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.command.implementation.RotateCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.Tool;

import java.util.ArrayList;
import java.util.List;

public final class UndoRedoManager {

	private static UndoRedoManager instance;

	private UndoRedoManager() {
	}

	public static UndoRedoManager getInstance() {
		if (instance == null) {
			instance = new UndoRedoManager();
		}
		return instance;
	}

	private static void undoResizeCommand(Layer undoLayer, ResizeCommand undoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == undoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> layerCommands = layerBitmapCommand.getLayerCommands();

			if (!layerCommands.isEmpty()) {
				int indexOfLastElement = layerCommands.size() - 1;
				Command lastCommand = layerCommands.get(indexOfLastElement);
				if (lastCommand instanceof ResizeCommand) {
					layerBitmapCommand.addCommandToUndoList();
					LayerListener.getInstance().selectLayer(layer);
					layerBitmapCommand.clearLayerBitmap();
					layerBitmapCommand.runAllCommands();
					continue;
				}
			}

			int undoWidth = undoLayer.getImage().getWidth();
			int undoHeight = undoLayer.getImage().getHeight();
			int currentWidth = layer.getImage().getWidth();
			int currentHeight = layer.getImage().getHeight();

			Command resizeCommand = new ResizeCommand(
					-undoCommand.getResizeCoordinateXLeft(),
					-undoCommand.getResizeCoordinateYTop(),
					currentWidth - (undoCommand.getResizeCoordinateXRight() - undoWidth),
					currentHeight - (undoCommand.getResizeCoordinateYBottom() - undoHeight),
					undoCommand.getMaximumBitmapResolution());

			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), resizeCommand);
		}

		if (!undoLayer.getSelected()) {
			LayerListener.getInstance().selectLayer(undoLayer);
		}
	}

	private static void redoResizeCommand(Layer redoLayer, ResizeCommand redoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == redoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> undoCommands = layerBitmapCommand.getLayerUndoCommands();

			if (!undoCommands.isEmpty()) {
				Command firstCommand = undoCommands.get(0);
				if (firstCommand instanceof ResizeCommand) {
					firstCommand.run(PaintroidApplication.drawingSurface.getCanvas(), layer);
					layerBitmapCommand.addCommandToRedoList();
					continue;
				}
			}

			Command resizeCommand = new ResizeCommand(
					redoCommand.getResizeCoordinateXLeft(),
					redoCommand.getResizeCoordinateYTop(),
					redoCommand.getResizeCoordinateXRight(),
					redoCommand.getResizeCoordinateYBottom(),
					redoCommand.getMaximumBitmapResolution());

			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), resizeCommand);
		}
	}

	private static void undoRotateCommand(Layer undoLayer, RotateCommand undoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == undoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> layerCommands = layerBitmapCommand.getLayerCommands();

			if (!layerCommands.isEmpty()) {
				int indexOfLastElement = layerCommands.size() - 1;
				Command lastCommand = layerCommands.get(indexOfLastElement);
				if (lastCommand instanceof RotateCommand) {
					layerBitmapCommand.addCommandToUndoList();
					LayerListener.getInstance().selectLayer(layer);
					layerBitmapCommand.clearLayerBitmap();
					layerBitmapCommand.runAllCommands();
					continue;
				}
			}

			RotateCommand.RotateDirection rotateDirection = null;
			switch (undoCommand.getRotateDirection()) {
				case ROTATE_LEFT:
					rotateDirection = RotateCommand.RotateDirection.ROTATE_RIGHT;
					break;
				case ROTATE_RIGHT:
					rotateDirection = RotateCommand.RotateDirection.ROTATE_LEFT;
			}

			Command rotateCommand = new RotateCommand(rotateDirection);
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), rotateCommand);
		}

		if (!undoLayer.getSelected()) {
			LayerListener.getInstance().selectLayer(undoLayer);
		}
	}

	private static void redoRotateCommand(Layer redoLayer, RotateCommand redoCommand) {
		for (Layer layer : LayerListener.getInstance().getAdapter().getLayers()) {
			if (layer == redoLayer) {
				continue;
			}

			LayerCommand layerCommand = new LayerCommand(layer);
			LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager.getLayerBitmapCommand(layerCommand);
			List<Command> undoCommands = layerBitmapCommand.getLayerUndoCommands();

			if (!undoCommands.isEmpty()) {
				Command firstCommand = undoCommands.get(0);
				if (firstCommand instanceof RotateCommand) {
					firstCommand.run(PaintroidApplication.drawingSurface.getCanvas(), layer);
					layerBitmapCommand.addCommandToRedoList();
					continue;
				}
			}

			Command rotateCommand = new RotateCommand(redoCommand.getRotateDirection());
			PaintroidApplication.commandManager.commitCommandToLayer(new LayerCommand(layer), rotateCommand);
		}
	}

	public void performUndo() {
		final CommandManager commandManager = PaintroidApplication.commandManager;
		synchronized (commandManager.getDrawBitmapCommandsAtLayer()) {
			final Layer layer = LayerListener.getInstance().getCurrentLayer();
			LayerCommand layerCommand = new LayerCommand(layer);
			final LayerBitmapCommand layerBitmapCommand = commandManager.getLayerBitmapCommand(layerCommand);

			final float scale = PaintroidApplication.perspective.getScale();
			final float surfaceTranslationX = PaintroidApplication.perspective.getSurfaceTranslationX();
			final float surfaceTranslationY = PaintroidApplication.perspective.getSurfaceTranslationY();

			if (commandManager.getLayerOperationsCommandList().size() <= 1
					&& layerBitmapCommand.getLayerCommands().size() <= 1) {
				update();
				return;
			}

			boolean isLayerCommandInstance;
			Command lastExecutedCommand = layerBitmapCommand.getLayerCommands().get(layerBitmapCommand.getLayerCommands().size() - 1);

			if (lastExecutedCommand instanceof LayerCommand) {
				isLayerCommandInstance = true;

				if (((LayerCommand) lastExecutedCommand).getLayerCommandType() == CommandManagerImplementation.CommandType.ADD_LAYER) {
					ArrayList<LayerBitmapCommand> list = commandManager
							.getLayerBitmapCommands(((LayerCommand) lastExecutedCommand).getLayer().getLayerID());
					((LayerCommand) lastExecutedCommand).setLayersBitmapCommands(list);

					commandManager.processLayerUndo((LayerCommand) lastExecutedCommand);
					commandManager.deleteLayerCommandFromDrawBitmapCommandsAtLayer((LayerCommand) lastExecutedCommand);

					commandManager.addLayerCommandToUndoList();
				} else if (((LayerCommand) lastExecutedCommand).getLayerCommandType() == CommandManagerImplementation.CommandType.REMOVE_LAYER) {
					commandManager.deleteLayerCommandFromDrawBitmapCommandsAtLayer((LayerCommand) lastExecutedCommand);
					commandManager.processLayerUndo((LayerCommand) lastExecutedCommand);
					commandManager.addLayerCommandToUndoList();
				} else if (((LayerCommand) lastExecutedCommand).getLayerCommandType() == CommandManagerImplementation.CommandType.MERGE_LAYERS) {
					commandManager.deleteLayerCommandFromDrawBitmapCommandsAtLayer((LayerCommand) lastExecutedCommand);
					commandManager.processLayerUndo((LayerCommand) lastExecutedCommand);
					commandManager.addLayerCommandToUndoList();
				}
			} else {
				isLayerCommandInstance = false;
				layerBitmapCommand.addCommandToUndoList();
			}

			final boolean isLayerCommand = isLayerCommandInstance;
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
					if (!isLayerCommand) {
						layerBitmapCommand.clearLayerBitmap();

						for (Command command : layerBitmapCommand.getLayerCommands()) {
							command.run(canvas, layer);
						}

						// check for resize/rotate
						if (!layerBitmapCommand.getLayerUndoCommands().isEmpty()) {
							Command undoCommand = layerBitmapCommand.getLayerUndoCommands().get(0);
							if (undoCommand instanceof ResizeCommand) {
								undoResizeCommand(layer, (ResizeCommand) undoCommand);
							} else if (undoCommand instanceof RotateCommand) {
								undoRotateCommand(layer, (RotateCommand) undoCommand);
							}
						}
					}

					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
					update();

					PaintroidApplication.perspective.setScale(scale);
					PaintroidApplication.perspective.setSurfaceTranslationX(surfaceTranslationX);
					PaintroidApplication.perspective.setSurfaceTranslationY(surfaceTranslationY);

					LayerListener.getInstance().refreshView();
					PaintroidApplication.drawingSurface.refreshDrawingSurface();
					NavigationDrawerMenuActivity.isSaved = false;
					IndeterminateProgressDialog.getInstance().dismiss();
				}
			}.execute();
		}
	}

	public void performRedo() {
		final Layer layer = LayerListener.getInstance().getCurrentLayer();
		LayerCommand layerCommand = new LayerCommand(layer);
		final CommandManager commandManager = PaintroidApplication.commandManager;
		LayerBitmapCommand layerBitmapCommand = commandManager.getLayerBitmapCommand(layerCommand);

		final float scale = PaintroidApplication.perspective.getScale();
		final float surfaceTranslationX = PaintroidApplication.perspective.getSurfaceTranslationX();
		final float surfaceTranslationY = PaintroidApplication.perspective.getSurfaceTranslationY();

		if (commandManager.getLayerOperationsUndoCommandList().size() == 0
				&& layerBitmapCommand.getLayerUndoCommands().size() == 0) {
			update();
			return;
		}

		final Command lastUndoCommand = layerBitmapCommand.getLayerUndoCommands().get(0);
		boolean isLayerCommandInstance;

		if (lastUndoCommand instanceof LayerCommand) {
			isLayerCommandInstance = true;

			if (((LayerCommand) lastUndoCommand).getLayerCommandType() == CommandManagerImplementation.CommandType.ADD_LAYER) {
				commandManager.addLayerCommandToDrawBitmapCommandsAtLayer((LayerCommand) lastUndoCommand);

				commandManager.processLayerRedo((LayerCommand) lastUndoCommand);
				commandManager.addLayerCommandToRedoList();
			} else if (((LayerCommand) lastUndoCommand).getLayerCommandType() == CommandManagerImplementation.CommandType.REMOVE_LAYER) {
				ArrayList<LayerBitmapCommand> list = commandManager
						.getLayerBitmapCommands(((LayerCommand) lastUndoCommand).getLayer().getLayerID());
				((LayerCommand) lastUndoCommand).setLayersBitmapCommands(list);

				commandManager.processLayerRedo((LayerCommand) lastUndoCommand);

				commandManager
						.addLayerCommandToDrawBitmapCommandsAtLayer((LayerCommand) lastUndoCommand);

				commandManager.addLayerCommandToRedoList();
			} else if (((LayerCommand) lastUndoCommand).getLayerCommandType() == CommandManagerImplementation.CommandType.MERGE_LAYERS) {
				commandManager.processLayerRedo((LayerCommand) lastUndoCommand);

				commandManager
						.addLayerCommandToDrawBitmapCommandsAtLayer((LayerCommand) lastUndoCommand);

				commandManager.addLayerCommandToRedoList();
			}
		} else {
			isLayerCommandInstance = false;
			layerBitmapCommand.addCommandToRedoList();
		}

		final boolean isLayerCommand = isLayerCommandInstance;
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
				if (!isLayerCommand) {

					if (lastUndoCommand != null) {
						lastUndoCommand.run(canvas, layer);
					}

					// check for resize/rotate
					if (lastUndoCommand instanceof ResizeCommand) {
						redoResizeCommand(layer, (ResizeCommand) lastUndoCommand);
					} else if (lastUndoCommand instanceof RotateCommand) {
						redoRotateCommand(layer, (RotateCommand) lastUndoCommand);
					}
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
				update();

				PaintroidApplication.perspective.setScale(scale);
				PaintroidApplication.perspective.setSurfaceTranslationX(surfaceTranslationX);
				PaintroidApplication.perspective.setSurfaceTranslationY(surfaceTranslationY);

				LayerListener.getInstance().refreshView();
				PaintroidApplication.drawingSurface.refreshDrawingSurface();
				NavigationDrawerMenuActivity.isSaved = false;
				IndeterminateProgressDialog.getInstance().dismiss();
			}
		}.execute();
	}

	public void update() {
		Layer currentLayer = LayerListener.getInstance().getCurrentLayer();
		LayerCommand layerCommand = new LayerCommand(currentLayer);
		LayerBitmapCommand layerBitmapCommand = PaintroidApplication.commandManager
				.getLayerBitmapCommand(layerCommand);

		updateUndoButton(layerBitmapCommand);
	}

	private void updateUndoButton(LayerBitmapCommand layerBitmapCommand) {
		final CommandManager commandManager = PaintroidApplication.commandManager;
		final boolean enableUndo = layerBitmapCommand.getLayerCommands().size() > 1
				|| commandManager.getLayerOperationsCommandList().size() > 1;
		final boolean enableRedo = !layerBitmapCommand.getLayerUndoCommands().isEmpty();
		commandManager.enableUndo(enableUndo);
		commandManager.enableRedo(enableRedo);
	}
}
