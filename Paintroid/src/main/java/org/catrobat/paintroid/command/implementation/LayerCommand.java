package org.catrobat.paintroid.command.implementation;

import android.graphics.Canvas;

import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;

public class LayerCommand extends BaseCommand {
	private Layer layer;
	private ArrayList<Integer> listOfMergedLayerIds;
	private ArrayList<LayerBitmapCommand> layersBitmapCommands;
	private CommandManagerImplementation.CommandType layerCommandType;

	private int oldLayerPosition;

	public LayerCommand(Layer layer) {
		this.layer = layer;
		oldLayerPosition = -1;
	}

	public LayerCommand(Layer newLayer, ArrayList<Integer> listOfMergedLayerIds) {
		layer = newLayer;
		this.listOfMergedLayerIds = listOfMergedLayerIds;
		layersBitmapCommands = new ArrayList<>(this.listOfMergedLayerIds.size());
		layerCommandType = CommandManagerImplementation.CommandType.NO_LAYER_COMMAND;
		oldLayerPosition = -1;
	}

	public Layer getLayer() {
		return layer;
	}

	public ArrayList<Integer> getLayersToMerge() {
		return listOfMergedLayerIds;
	}

	public ArrayList<LayerBitmapCommand> getLayersBitmapCommands() {
		return layersBitmapCommands;
	}

	public void setLayersBitmapCommands(ArrayList<LayerBitmapCommand> layersBitmapCommandManagerList) {
		this.layersBitmapCommands = layersBitmapCommandManagerList;
	}

	public CommandManagerImplementation.CommandType getLayerCommandType() {
		return layerCommandType;
	}

	public void setLayerCommandType(CommandManagerImplementation.CommandType type) {
		layerCommandType = type;
	}

	public int getOldLayerPosition() {
		return oldLayerPosition;
	}

	public void setOldLayerPosition(int pos) {
		oldLayerPosition = pos;
	}

	@Override
	public void run(Canvas canvas, Layer layer) {
	}
}
