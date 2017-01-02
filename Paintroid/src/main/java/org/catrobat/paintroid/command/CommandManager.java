/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command;

import org.catrobat.paintroid.command.implementation.LayerCommand;

/**
 * Describes undo/redo command manager responsible for applications layer management.
 */
public interface CommandManager {

	/**
	 * Adds the new command (draw path, erase, draw shape) to corresponding layer.
	 * @param bitmapCommand command to commit to layer bitmap.
	 * @param layerCommand contains layer to which command should be commited.
	 */
	void commitCommandToLayer(LayerCommand layerCommand, Command bitmapCommand);

	/**
	 * Adds new layer to application.
	 * @param layerCommand contains layer to add.
	 */
	void commitAddLayerCommand(LayerCommand layerCommand);

	/**
	 * Removes corresponding layer from application.
	 * @param layerCommand contains layer to remove.
	 */
	void commitRemoveLayerCommand(LayerCommand layerCommand);

	/**
	 * Merges two layers.
	 * @param layerCommand contains layer to be merged.
	 */
	void commitMergeLayerCommand(LayerCommand layerCommand);

	/**
	 * Changes visibility of corresponding layer.
	 * @param layerCommand contains layer which visibility should be changed.
	 */
	void commitLayerVisibilityCommand(LayerCommand layerCommand);

	/**
	 * Locks the corresponding layer.
	 * @param layerCommand contains layer which should be (un)locked.
	 */
	void commitLayerLockCommand(LayerCommand layerCommand);

	/**
	 * Renames corresponding layer.
	 * @param layerCommand contains layer to rename.
	 */
	void commitRenameLayerCommand(LayerCommand layerCommand);

	/**
	 * Undo last command applied to specific layer.
	 */
	void undo();

	/**
	 * Redo last command applied to specific layer.
	 */
	void redo();

	/**
	 * Clears manager command lists.
	 */
	void resetAndClear(boolean clearLayerBitmapCommandsList);

	/**
	 * Checks if bitmap is painted.
	 */
	boolean checkIfDrawn();
}
