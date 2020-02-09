/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.viewholder;

import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.MainActivityContracts;

public class TopBarViewHolder implements MainActivityContracts.TopBarViewHolder {
	public final TextView title;
	public final ImageButton undoButton;
	public final ImageButton redoButton;
	public final ViewGroup layout;

	public TopBarViewHolder(ViewGroup layout) {
		this.layout = layout;
		title = layout.findViewById(R.id.pocketpaint_textview_top);
		undoButton = layout.findViewById(R.id.pocketpaint_btn_top_undo);
		redoButton = layout.findViewById(R.id.pocketpaint_btn_top_redo);
	}

	@Override
	public void enableUndoButton() {
		undoButton.setEnabled(true);
	}

	@Override
	public void disableUndoButton() {
		undoButton.setEnabled(false);
	}

	@Override
	public void enableRedoButton() {
		redoButton.setEnabled(true);
	}

	@Override
	public void disableRedoButton() {
		redoButton.setEnabled(false);
	}

	@Override
	public void hide() {
		layout.setVisibility(View.GONE);
	}

	@Override
	public void show() {
		layout.setVisibility(View.VISIBLE);
	}

	@Override
	public int getHeight() {
		return layout.getHeight();
	}

	@Override
	public void removeStandaloneMenuItems(Menu menu) {
		menu.removeItem(R.id.pocketpaint_nav_save_image);
		menu.removeItem(R.id.pocketpaint_nav_save_duplicate);
		menu.removeItem(R.id.pocketpaint_nav_new_image);
	}

	@Override
	public void removeCatroidMenuItems(Menu menu) {
		menu.removeItem(R.id.pocketpaint_nav_export);
		menu.removeItem(R.id.pocketpaint_nav_discard_image);
	}

	@Override
	public void hideTitleIfNotStandalone() {
		title.setVisibility(View.GONE);
	}
}
