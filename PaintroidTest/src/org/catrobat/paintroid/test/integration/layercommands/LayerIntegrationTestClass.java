package org.catrobat.paintroid.test.integration.layercommands;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.layer.DeleteLayerCommand;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;

import android.graphics.PointF;
import android.util.Log;

public class LayerIntegrationTestClass extends BaseIntegrationTestClass {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.catrobat.paintroid.test.integration.BaseIntegrationTestClass#setUp()
	 */
	@Override
	protected void setUp() {
		super.setUp();
		pf = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PaintroidApplication.currentTool.changePaintStrokeWidth(500);
	}

	public PointF pf;

	public LayerIntegrationTestClass() throws Exception {
		super();
	}

	public void showAllCommands() {
		for (int j = 0; j < PaintroidApplication.commandManager.getCommands().size(); j++) {
			Log.i(PaintroidApplication.TAG,
					String.valueOf(j)
							+ " "
							+ PaintroidApplication.commandManager.getCommands().get(j).toString()
							+ " "
							+ String.valueOf(PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer()));
		}

	}

	public int getNumOfCommandsOfLayer(int i) {
		int counter = 0;
		for (int j = 1; j < PaintroidApplication.commandManager.getCommands().size(); j++) {
			if (PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer() == i
					&& PaintroidApplication.commandManager.getCommands().get(j).isDeleted() == false
					&& PaintroidApplication.commandManager.getCommands().get(j).isHidden() == false
					&& !(PaintroidApplication.commandManager.getCommands().get(j) instanceof DeleteLayerCommand)) {
				counter++;
			}
		}
		return counter;
	}

	public int getNumOfHiddenCommandsOfLayer(int i) {
		int counter = 0;
		for (int j = 1; j < PaintroidApplication.commandManager.getCommands().size(); j++) {
			if (PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer() == i
					&& PaintroidApplication.commandManager.getCommands().get(j).isDeleted() == false
					&& PaintroidApplication.commandManager.getCommands().get(j).isHidden() == true
					&& !(PaintroidApplication.commandManager.getCommands().get(j) instanceof DeleteLayerCommand)) {
				counter++;
			}
		}
		return counter;
	}

	public int getNumOfDeletedCommandsOfLayer(int i) {
		int counter = 0;
		for (int j = 1; j < PaintroidApplication.commandManager.getCommands().size(); j++) {
			Log.i(PaintroidApplication.TAG, PaintroidApplication.commandManager.getCommands().get(j).toString() + " "
					+ String.valueOf(PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer()));
			if (PaintroidApplication.commandManager.getCommands().get(j).getCommandLayer() == i
					&& PaintroidApplication.commandManager.getCommands().get(j).isDeleted() == true
					&& !(PaintroidApplication.commandManager.getCommands().get(j) instanceof DeleteLayerCommand)) {
				counter++;
			}
		}
		return counter;
	}

}
