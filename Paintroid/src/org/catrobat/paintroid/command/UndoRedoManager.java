package org.catrobat.paintroid.command;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.ui.Statusbar;

public final class UndoRedoManager {

	private static UndoRedoManager mInstance;
	private Statusbar mStatusbar;

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

	public void setStatusbar(Statusbar statusbar) {
		mStatusbar = statusbar;
	}

	public void update(StatusMode status) {
		switch (status) {
		case ENABLE_UNDO:
			mStatusbar.toggleUndo(R.drawable.icon_menu_undo);
			mStatusbar.enableUndo();

			break;
		case DISABLE_UNDO:
			mStatusbar.toggleUndo(R.drawable.icon_menu_undo_disabled);
			mStatusbar.disableUndo();
			break;
		case ENABLE_REDO:
			mStatusbar.toggleRedo(R.drawable.icon_menu_redo);
			mStatusbar.enableRedo();
			break;
		case DISABLE_REDO:
			mStatusbar.toggleRedo(R.drawable.icon_menu_redo_disabled);
			mStatusbar.disableRedo();
			break;

		default:
			break;
		}
	}

}
