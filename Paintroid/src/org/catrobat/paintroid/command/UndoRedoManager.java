/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
