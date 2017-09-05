package org.catrobat.paintroid.command.implementation;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.listener.LayerListener;
import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.Tool;

import java.util.LinkedList;
import java.util.List;

/**
 * Contains all the commands that are to be executed on the layer's bitmap.
 */
public class LayerBitmapCommandImpl implements LayerBitmapCommand {
	private Layer mLayer;

	public LinkedList<Command> mCommandList;
	public LinkedList<Command> mUndoCommandList;


	public LayerBitmapCommandImpl(LayerCommand layerCommand) {
		mLayer = layerCommand.getLayer();
		mCommandList = new LinkedList<Command>();
		mUndoCommandList = new LinkedList<Command>();
	}


	@Override
	public Layer getLayer() {
		return mLayer;
	}

	@Override
	public void commitCommandToLayer(final Command command) {
		synchronized (mCommandList) {
			mUndoCommandList.clear();
			mCommandList.addLast(command);

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
					command.run(canvas, mLayer);
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
					LayerListener.getInstance().refreshView();
					PaintroidApplication.drawingSurface.refreshDrawingSurface();
					PaintroidApplication.isSaved = false;
					IndeterminateProgressDialog.getInstance().dismiss();
				}
			}.execute();
		}
	}

	@Override
	public void addCommandToList(Command command){
		mUndoCommandList.clear();
		mCommandList.addLast(command);
		PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
	}


	@Override
	public List<Command> getLayerCommands() {
		return mCommandList;
	}

	@Override
	public List<Command> getLayerUndoCommands() {
		return mUndoCommandList;
	}

	@Override
	public void copyLayerCommands(List<Command> commands) {
		for (Command command : commands) {
			mCommandList.add(command);
		}
	}

	@Override
	public synchronized void undo() {
		synchronized (mCommandList) {
		//TODO Can this be removed?
		}
	}

	public synchronized void addCommandToUndoList(){
		synchronized (mCommandList) {
			if(mCommandList.size() > 0){
				Command command = mCommandList.removeLast();
				mUndoCommandList.addFirst(command);
			}
		}
	}

	@Override
	public synchronized void redo() {
		synchronized (mUndoCommandList) {

			if (mUndoCommandList.size() != 0) {
				Command command = mUndoCommandList.removeFirst();
				mCommandList.addLast(command);
				command.run(PaintroidApplication.drawingSurface.getCanvas(), mLayer);
				PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
				//LayersDialog.getInstance().refreshView();
				LayerListener.getInstance().refreshView(); //TODO why refresh view here
			}

		}
	}

	@Override
	public Command addCommandToRedoList(){
		synchronized (mUndoCommandList) {

			if (mUndoCommandList.size() != 0) {
				Command command = mUndoCommandList.removeFirst();
				mCommandList.addLast(command);
				return command;
			}
			return null;
		}
	}

	private void executeAllCommandsOnLayerCanvas() {

		clearLayerBitmap();
		for (Command command : mCommandList) {
			command.run(PaintroidApplication.drawingSurface.getCanvas(), mLayer);
		}

		PaintroidApplication.currentTool.resetInternalState(Tool.StateChange.RESET_INTERNAL_STATE);
		//LayersDialog.getInstance().refreshView();
		LayerListener.getInstance().refreshView(); //TODO why refresh view here?

	}

	@Override
	public void clearLayerBitmap() {
		WindowManager wm = (WindowManager) PaintroidApplication.applicationContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		Bitmap bitmap;
		if (PaintroidApplication.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			bitmap = Bitmap.createBitmap(dm.heightPixels, dm.widthPixels, Bitmap.Config.ARGB_8888);
		} else {
			bitmap = Bitmap.createBitmap(dm.widthPixels, dm.heightPixels, Bitmap.Config.ARGB_8888);
		}
		bitmap.eraseColor(Color.TRANSPARENT);
		mLayer.setImage(bitmap);
		PaintroidApplication.drawingSurface.resetBitmap(bitmap);
	}

	@Override
	public boolean moreCommands() {
		if (mCommandList.size() > 0)
			return true;

		return false;
	}

	@Override
	public void runAllCommands() {
		for (Command command : getLayerCommands()) {
			command.run(PaintroidApplication.drawingSurface.getCanvas(), getLayer());
		}
	}

}
