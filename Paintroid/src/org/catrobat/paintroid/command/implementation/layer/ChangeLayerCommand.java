package org.catrobat.paintroid.command.implementation.layer;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.UndoRedoManager;
import org.catrobat.paintroid.command.implementation.BaseCommand;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class ChangeLayerCommand extends BaseCommand {

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);

		if (PaintroidApplication.commandManager
				.hasUndosLeft(PaintroidApplication.commandManager.getCommands()
						.size())) {
			UndoRedoManager.getInstance().update(
					UndoRedoManager.StatusMode.ENABLE_UNDO);
		}

		setChanged();
		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}

	public ChangeLayerCommand() {
	}
}
