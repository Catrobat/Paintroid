package org.catrobat.paintroid.command.implementation.layer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.dialog.layerchooser.LayerRow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

public class HideLayerCommand extends BaseCommand {
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
				Log.i(PaintroidApplication.TAG, String.valueOf(i));

				if (PaintroidApplication.commandManager.getCommands().get(i)
						.getCommandLayer() == this.layerIndex
						|| PaintroidApplication.commandManager.getCommands()
								.get(i) instanceof HideLayerCommand) {
					PaintroidApplication.commandManager.getCommands().get(i)
							.setHidden(true);
					// PaintroidApplication.commandManager.decrementCounter();
				}
				i--;
			}
			// this.setDeleted(true);
		}
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	public HideLayerCommand(int layerIndex) {
		this.layerIndex = layerIndex;
	}
}
