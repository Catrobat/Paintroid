/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.MainActivity.ToolType;

public class ToolButtonAdapter extends BaseAdapter {

	private Context context;

	private ToolButton[] buttons = { new ToolButton(R.drawable.brush64, R.string.button_brush, ToolType.BRUSH),
			new ToolButton(R.drawable.cursor64, R.string.button_cursor, ToolType.CURSOR),
			new ToolButton(R.drawable.scroll64, R.string.button_choose, ToolType.SCROLL),
			new ToolButton(R.drawable.zoom64, R.string.button_zoom, ToolType.ZOOM),
			new ToolButton(R.drawable.pipette64, R.string.button_pipette, ToolType.PIPETTE),
			new ToolButton(R.drawable.magic64, R.string.button_magic, ToolType.MAGIC),
			new ToolButton(R.drawable.undo64, R.string.button_undo, ToolType.UNDO),
			new ToolButton(R.drawable.redo64, R.string.button_redo, ToolType.REDO),
			new ToolButton(R.drawable.scroll64, R.string.button_floating_box, ToolType.FLOATINGBOX),
			new ToolButton(R.drawable.scroll64, R.string.button_import_png, ToolType.IMPORTPNG) };

	public ToolButtonAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return buttons.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public ToolButton getToolButton(int position) {
		return buttons[position];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView toolButton;
		if (convertView == null) {
			toolButton = new TextView(context);
			toolButton.setGravity(Gravity.CENTER_HORIZONTAL);
		} else {
			toolButton = (TextView) convertView;
		}
		toolButton.setCompoundDrawablesWithIntrinsicBounds(0, buttons[position].drawableId, 0, 0);
		toolButton.setText(buttons[position].stringId);
		return toolButton;
	}

}
