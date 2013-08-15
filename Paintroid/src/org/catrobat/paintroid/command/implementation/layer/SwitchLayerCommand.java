package org.catrobat.paintroid.command.implementation.layer;

import java.util.Collections;
import java.util.Comparator;
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

		for (int i = 1; i < PaintroidApplication.commandManager.getCommands()
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
		Collections.sort(l, new Comparator<Command>() {
			@Override
			public int compare(Command o1, Command o2) {
				if (o1.getCommandLayer() > o2.getCommandLayer()) {
					return -1;
				}
				if (o1.getCommandLayer() < o2.getCommandLayer()) {
					return 1;
				}
				return 0;
			}
		});
	}
}
