package org.catrobat.paintroid.command.implementation;

import android.graphics.Canvas;

import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;

public class LayerCommand extends BaseCommand {
	private Layer mLayer;
	private ArrayList<Integer> mListOfMergedLayerIds;
	private ArrayList<LayerBitmapCommand> mLayersBitmapCommands;
	private CommandManagerImplementation.CommandType mLayerCommandType;

	private int mOldLayerPosition;

	public LayerCommand(Layer layer) {
		mLayer = layer;
		mOldLayerPosition = -1;
	}

	public LayerCommand(Layer newLayer, ArrayList<Integer> listOfMergedLayerIds) {
		mLayer = newLayer;
		mListOfMergedLayerIds = listOfMergedLayerIds;
		mLayersBitmapCommands = new ArrayList<>(mListOfMergedLayerIds.size());
		mLayerCommandType = CommandManagerImplementation.CommandType.NO_LAYER_COMMAND;
		mOldLayerPosition = -1;
	}

	public Layer getLayer() {
		return mLayer;
	}

	public ArrayList<Integer> getLayersToMerge() {
		return mListOfMergedLayerIds;
	}

	public void setLayersBitmapCommands(ArrayList<LayerBitmapCommand> layersBitmapCommandManagerList) {
		this.mLayersBitmapCommands = layersBitmapCommandManagerList;
	}

	public ArrayList<LayerBitmapCommand> getLayersBitmapCommands() {
		return mLayersBitmapCommands;
	}

	public void setmLayerCommandType(CommandManagerImplementation.CommandType type) {
		mLayerCommandType = type;
	}

	public CommandManagerImplementation.CommandType getmLayerCommandType() {
		return mLayerCommandType;
	}

	public void setOldLayerPosition(int pos) {
		mOldLayerPosition = pos;
	}

	public int getOldLayerPosition() {
		return mOldLayerPosition;
	}

	@Override
	public void run(Canvas canvas, Layer layer) {
	}
}
