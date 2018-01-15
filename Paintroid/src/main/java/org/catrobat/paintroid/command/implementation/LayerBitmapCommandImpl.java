package org.catrobat.paintroid.command.implementation;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.Tool;

import java.util.LinkedList;
import java.util.List;

public class LayerBitmapCommandImpl implements LayerBitmapCommand {
	public LinkedList<Command> commandList;
	public LinkedList<Command> undoCommandList;
	private Layer layer;

	public LayerBitmapCommandImpl(LayerCommand layerCommand) {
		layer = layerCommand.getLayer();
		commandList = new LinkedList<>();
		undoCommandList = new LinkedList<>();
	}

	@Override
	public Layer getLayer() {
		return layer;
	}

	@Override
	public void commitCommandToLayer(final Command command) {
		synchronized (commandList) {
			undoCommandList.clear();
			commandList.addLast(command);

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
					command.run(canvas, layer);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
					LayerListener.getInstance().refreshView();
					PaintroidApplication.drawingSurface.refreshDrawingSurface();
					NavigationDrawerMenuActivity.isSaved = false;
					IndeterminateProgressDialog.getInstance().dismiss();
				}
			}.execute();
		}
	}

	@Override
	public void addCommandToList(Command command) {
		undoCommandList.clear();
		commandList.addLast(command);
		PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
	}

	@Override
	public List<Command> getLayerCommands() {
		return commandList;
	}

	@Override
	public List<Command> getLayerUndoCommands() {
		return undoCommandList;
	}

	@Override
	public void copyLayerCommands(List<Command> commands) {
		commandList.addAll(commands);
	}

	@Override
	public void undo() {
	}

	public synchronized void addCommandToUndoList() {
		synchronized (commandList) {
			if (!commandList.isEmpty()) {
				Command command = commandList.removeLast();
				undoCommandList.addFirst(command);
			}
		}
	}

	public synchronized void addLayerCommandToUndoList(LayerCommand layerCommand) {
		synchronized (commandList) {
			synchronized (undoCommandList) {
				if (!commandList.isEmpty()) {
					undoCommandList.addFirst(layerCommand);
					commandList.remove(layerCommand);
				}
			}
		}
	}

	@Override
	public synchronized void redo() {
		synchronized (undoCommandList) {

			if (!undoCommandList.isEmpty()) {
				Command command = undoCommandList.removeFirst();
				commandList.addLast(command);
				command.run(PaintroidApplication.drawingSurface.getCanvas(), layer);
				PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
				LayerListener.getInstance().refreshView();
			}
		}
	}

	@Override
	public Command addCommandToRedoList() {
		synchronized (undoCommandList) {

			if (!undoCommandList.isEmpty()) {
				Command command = undoCommandList.removeFirst();
				commandList.addLast(command);
				return command;
			}
			return null;
		}
	}

	public void addLayerCommandToRedoList(LayerCommand layerCommand) {
		synchronized (commandList) {
			commandList.addLast(layerCommand);
		}
		synchronized (undoCommandList) {
			if (!undoCommandList.isEmpty()) {
				undoCommandList.remove(layerCommand);
			}
		}
	}

	@Override
	public void clearLayerBitmap() {
		Resources resources = PaintroidApplication.applicationContext.getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Bitmap bitmap;
		int orientation = resources.getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			bitmap = Bitmap.createBitmap(dm.heightPixels, dm.widthPixels, Bitmap.Config.ARGB_8888);
		} else {
			bitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
		}
		bitmap.eraseColor(Color.TRANSPARENT);
		layer.setImage(bitmap);
		PaintroidApplication.drawingSurface.resetBitmap(bitmap);
	}

	@Override
	public boolean moreCommands() {
		return !commandList.isEmpty();
	}

	@Override
	public void runAllCommands() {
		for (Command command : getLayerCommands()) {
			command.run(PaintroidApplication.drawingSurface.getCanvas(), getLayer());
		}
	}
}
