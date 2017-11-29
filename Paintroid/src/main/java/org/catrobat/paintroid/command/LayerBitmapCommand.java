package org.catrobat.paintroid.command;

import org.catrobat.paintroid.tools.Layer;

import java.util.List;

/**
 * Describes layer commands responsible for drawing. These commands are performed on layer's bitmap.
 */
public interface LayerBitmapCommand {
	/**
	 * Retrieves layer assigned to command manager.
	 *
	 * @return Layer which has been assigned to command manager.
	 */
	Layer getLayer();

	/**
	 * Commits command for assigned layer.
	 *
	 * @param command which has been performed on layer.
	 */
	void commitCommandToLayer(Command command);

	void addCommandToList(Command command);

	/**
	 * Retrieves all the commands performed on layers bitmap.
	 *
	 * @return layer bitmap commands.
	 */
	List<Command> getLayerCommands();

	List<Command> getLayerUndoCommands();

	/**
	 * Copies layer commands to current LayerBitmapCommand.
	 *
	 * @param commands commands to be copied.
	 */
	void copyLayerCommands(List<Command> commands);

	void clearLayerBitmap();

	/**
	 * Removes first command from Command List and add these to Undo List
	 */
	void addCommandToUndoList();

	/**
	 * Removes the first command from Undo List
	 *
	 * @return Command to Redo
	 */
	Command addCommandToRedoList();

	/**
	 * Undo drawing command for assigned layer.
	 */
	void undo();

	/**
	 * Redo drawing command for assigned layer.
	 */
	void redo();

	/**
	 * Check if bitmap is painted.
	 */
	boolean moreCommands();

	void runAllCommands();
}
