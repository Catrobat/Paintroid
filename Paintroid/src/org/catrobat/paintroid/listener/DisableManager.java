package org.catrobat.paintroid.listener;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.ui.implementation.ToolbarImplementation;

public class DisableManager {

	public static CommandManager mCommandManager;
	public static ToolbarImplementation mToolbar;

	public static enum StatusMode {
		ENABLE_UNDO, DISABLE_UNDO, ENABLE_REDO, DISABLE_REDO
	};

	DisableManager() {

	}

	public static void setCommandManager(CommandManager commandManager) {
		mCommandManager = commandManager;
	}

	public static void setToolbar(ToolbarImplementation toolbar) {
		mToolbar = toolbar;
	}

	public static void update(StatusMode status) {
		switch (status) {
		case ENABLE_UNDO:
			mToolbar.getUndoButton()
					.setImageResource(R.drawable.icon_menu_undo);
			break;
		case DISABLE_UNDO:
			mToolbar.getUndoButton().setImageResource(
					R.drawable.icon_menu_undo_disabled);
			break;
		case ENABLE_REDO:
			mToolbar.getRedoButton()
					.setImageResource(R.drawable.icon_menu_redo);
			break;
		case DISABLE_REDO:
			mToolbar.getRedoButton().setImageResource(
					R.drawable.icon_menu_redo_disabled);
			break;

		default:
			break;
		}
	}

}
