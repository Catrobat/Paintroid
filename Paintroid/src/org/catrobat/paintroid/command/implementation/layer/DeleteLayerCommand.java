package org.catrobat.paintroid.command.implementation.layer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.dialog.layerchooser.LayerChooserDialog;
import org.catrobat.paintroid.dialog.layerchooser.LayerRow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class DeleteLayerCommand extends BaseCommand {
	public int layerIndex;
	public LayerRow data;
	public boolean firstTime = true;

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);

		if (this.firstTime == true) {
			this.firstTime = false;
			int numCommands = PaintroidApplication.commandManager.getCommands()
					.size();
			int i = numCommands - 1;

			while (i < numCommands && i >= 1) {

				if (PaintroidApplication.commandManager.getCommands().get(i)
						.getCommandLayer() == this.layerIndex) {
					PaintroidApplication.commandManager.getCommands().get(i)
							.setDeleted(true);
				} else if (PaintroidApplication.commandManager.getCommands()
						.get(i).getCommandLayer() > this.layerIndex) {
					PaintroidApplication.commandManager
							.getCommands()
							.get(i)
							.setCommandLayer(
									PaintroidApplication.commandManager
											.getCommands().get(i)
											.getCommandLayer() - 1);
				}
				i--;
			}
			this.setHidden(true);
		}
		showAllCommands();
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	public void reverseDeletion(int layerIndex) {
		switchLayersBack(layerIndex);

		for (int i = 1; i < PaintroidApplication.commandManager.getCommands()
				.size(); i++) {
			if (PaintroidApplication.commandManager.getCommands().get(i)
					.getCommandLayer() == layerIndex) {
				PaintroidApplication.commandManager.getCommands().get(i)
						.setDeleted(false);
				PaintroidApplication.commandManager.getCommands().get(i)
						.setUndone(false);
			}

		}
		this.setDeleted(true);
		PaintroidApplication.commandManager.getCommands().remove(this);
	}

	private void switchLayersBack(int layerIndex) {
		for (int i = LayerChooserDialog.layer_data.size() - 1; i > layerIndex; i--) {
			Command sl_Command = new SwitchLayerCommand(i, i - 1);
			Log.i(PaintroidApplication.TAG, i + " - " + layerIndex);
			PaintroidApplication.commandManager.commitCommand(sl_Command);
		}
	}

	private void showAllCommands() {
		for (int j = 0; j < PaintroidApplication.commandManager.getCommands()
				.size(); j++) {
			Log.i(PaintroidApplication.TAG,
					String.valueOf(j)
							+ " "
							+ PaintroidApplication.commandManager.getCommands()
									.get(j).toString()
							+ " "
							+ String.valueOf(PaintroidApplication.commandManager
									.getCommands().get(j).getCommandLayer()));
		}

	}

	public DeleteLayerCommand(int layerIndex, LayerRow data) {
		this.layerIndex = layerIndex;
		this.data = data;
	}

	public LayerRow getData() {
		return this.data;
	}

	public int getLayerIndex() {
		return this.layerIndex;
	}
}
