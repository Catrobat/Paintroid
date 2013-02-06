package org.catrobat.paintroid.listener;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.implementation.ToolbarImplementation;

public final class DisableManager {

	private static DisableManager mInstance;
	// private CommandManager mCommandManager;
	private ToolbarImplementation mToolbar;

	public static enum StatusMode {
		ENABLE_UNDO, DISABLE_UNDO, ENABLE_REDO, DISABLE_REDO
	};

	private DisableManager() {

	}

	public static DisableManager getInstance() {
		if (mInstance == null) {
			mInstance = new DisableManager();
		}
		return mInstance;
	}

	public void setToolbar(ToolbarImplementation toolbar) {
		mToolbar = toolbar;
	}

	public void update(StatusMode status) {
		switch (status) {
		case ENABLE_UNDO:
			// mToolbar.getUndoButton()
			// .setImageResource(R.drawable.icon_menu_undo);
			mToolbar.toggleUndo(R.drawable.icon_menu_undo);
			mToolbar.enableUndo();

			break;
		case DISABLE_UNDO:
			// mToolbar.getUndoButton().setImageResource(
			// R.drawable.icon_menu_undo_disabled);
			mToolbar.toggleUndo(R.drawable.icon_menu_undo_disabled);
			mToolbar.disableUndo();
			break;
		case ENABLE_REDO:
			// mToolbar.getRedoButton()
			// .setImageResource(R.drawable.icon_menu_redo);
			mToolbar.toggleRedo(R.drawable.icon_menu_redo);
			mToolbar.enableRedo();
			break;
		case DISABLE_REDO:
			// mToolbar.getRedoButton().setImageResource(
			// R.drawable.icon_menu_redo_disabled);
			mToolbar.toggleRedo(R.drawable.icon_menu_redo_disabled);
			mToolbar.disableRedo();
			break;

		default:
			break;
		}
	}

}
