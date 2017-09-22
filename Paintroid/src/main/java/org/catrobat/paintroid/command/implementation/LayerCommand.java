package org.catrobat.paintroid.command.implementation;

import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.tools.Layer;

import java.util.ArrayList;

/**
 * Describes Layer command. It can contain either simple layer on which some operation is being
 * performed, or list of merged layers ids, along with the new layer created by merge and
 * merged layers bitmap command managers.
 */
public class LayerCommand {
	private Layer mLayer;
	private ArrayList<Integer> mListOfMergedLayerIds;
	private ArrayList<LayerBitmapCommand> mLayersBitmapCommands;

	private String mLayerNameHolder;

	public LayerCommand(Layer layer) {
		mLayer = layer;
	}

	public LayerCommand(Layer newLayer, ArrayList<Integer> listOfMergedLayerIds) {
		mLayer = newLayer;
		mListOfMergedLayerIds = listOfMergedLayerIds;
		mLayersBitmapCommands = new ArrayList<LayerBitmapCommand>(mListOfMergedLayerIds.size());
	}

	public LayerCommand(Layer layer, String layerNameHolder) {
		this.mLayer = layer;
		this.mLayerNameHolder = layerNameHolder;
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

	public String getLayerNameHolder() {
		return mLayerNameHolder;
	}

	public void setLayerNameHolder(String layerNameHolder) {
		this.mLayerNameHolder = layerNameHolder;
	}
}
