package org.catrobat.paintroid.command;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.implementation.ToolbarImplementation;

public final class UndoRedoManager {

	private static UndoRedoManager mInstance;
	// private CommandManager mCommandManager;
	private ToolbarImplementation mToolbar;

	public static enum StatusMode {
		ENABLE_UNDO, DISABLE_UNDO, ENABLE_REDO, DISABLE_REDO
	};

	private UndoRedoManager() {

	}

	public static UndoRedoManager getInstance() {
		if (mInstance == null) {
			mInstance = new UndoRedoManager();
		}
		return mInstance;
	}

	public void setToolbar(ToolbarImplementation toolbar) {
		mToolbar = toolbar;
	}

	public void update(StatusMode status) {
		switch (status) {
		case ENABLE_UNDO:
			mToolbar.toggleUndo(R.drawable.icon_menu_undo);
			mToolbar.enableUndo();

			break;
		case DISABLE_UNDO:
			mToolbar.toggleUndo(R.drawable.icon_menu_undo_disabled);
			mToolbar.disableUndo();
			break;
		case ENABLE_REDO:
			mToolbar.toggleRedo(R.drawable.icon_menu_redo);
			mToolbar.enableRedo();
			break;
		case DISABLE_REDO:
			mToolbar.toggleRedo(R.drawable.icon_menu_redo_disabled);
			mToolbar.disableRedo();
			break;

		default:
			break;
		}
	}

}
