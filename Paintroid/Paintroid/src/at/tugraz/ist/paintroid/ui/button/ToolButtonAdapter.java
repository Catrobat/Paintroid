/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.ui.button;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;

public class ToolButtonAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<ToolButton> mButtonsList;

	public ToolButtonAdapter(Context context, boolean fromCatrobat) {
		this.mContext = context;
		initButtons(fromCatrobat);
	}

	private void initButtons(boolean fromCatrobat) {

		mButtonsList = new ArrayList<ToolButton>();

		mButtonsList.add(new ToolButton(R.drawable.icon_menu_brush, R.string.button_brush, ToolType.BRUSH));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_cursor, R.string.button_cursor, ToolType.CURSOR));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_pipette, R.string.button_pipette, ToolType.PIPETTE));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_magic, R.string.button_magic, ToolType.MAGIC));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_stamp, R.string.button_stamp, ToolType.STAMP));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_import_image, R.string.button_import_image,
				ToolType.IMPORTPNG));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_crop, R.string.button_crop, ToolType.CROP));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_eraser, R.string.button_eraser, ToolType.ERASER));
		mButtonsList.add(new ToolButton(R.drawable.icon_menu_flip_horizontal, R.string.button_flip, ToolType.FLIP));

		if (fromCatrobat) {
			mButtonsList.add(new ToolButton(R.drawable.icon_menu_undo, R.string.button_undo, ToolType.UNDO));
			mButtonsList.add(new ToolButton(R.drawable.icon_menu_redo, R.string.button_redo, ToolType.REDO));
		}

	}

	@Override
	public int getCount() {
		return mButtonsList.size();
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
		return mButtonsList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			rowView = inflater.inflate(R.layout.tool_button, null);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.tool_button_image);
			imageView.setImageResource(mButtonsList.get(position).drawableId);
		}
		return rowView;
	}

}
