package org.catrobat.paintroid.command.implementation.layer;

import java.util.LinkedList;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class SwitchLayerCommand extends BaseCommand {

	public int firstLayer;
	public int secondLayer;

	public SwitchLayerCommand(int a, int b) {
		this.firstLayer = a;
		this.secondLayer = b;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {

		LinkedList<Command> l = PaintroidApplication.commandManager
				.getCommands();

		for (int i = 0; i < PaintroidApplication.commandManager.getCommands()
				.size(); i++) {
			if (PaintroidApplication.commandManager.getCommands().get(i)
					.getCommandLayer() == this.firstLayer) {
				PaintroidApplication.commandManager.getCommands().get(i)
						.setCommandLayer(this.secondLayer);
			} else if (PaintroidApplication.commandManager.getCommands().get(i)
					.getCommandLayer() == this.secondLayer) {
				PaintroidApplication.commandManager.getCommands().get(i)
						.setCommandLayer(this.firstLayer);
			}
		}
	}
}
