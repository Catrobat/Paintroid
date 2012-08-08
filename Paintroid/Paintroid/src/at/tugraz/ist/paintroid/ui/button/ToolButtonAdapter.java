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
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;

public class ToolButtonAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<ToolButton> mButtonsList;

	public ToolButtonAdapter(Context context, boolean fromCatrobat) {
		this.context = context;
		initButtons(fromCatrobat);
	}

	private void initButtons(boolean fromCatrobat) {

		mButtonsList = new ArrayList<ToolButton>();
		mButtonsList.add(new ToolButton(R.drawable.brush64, R.string.button_brush, ToolType.BRUSH));
		mButtonsList.add(new ToolButton(R.drawable.cursor64, R.string.button_cursor, ToolType.CURSOR));
		mButtonsList.add(new ToolButton(R.drawable.pipette64, R.string.button_pipette, ToolType.PIPETTE));
		mButtonsList.add(new ToolButton(R.drawable.magic64, R.string.button_magic, ToolType.MAGIC));
		mButtonsList.add(new ToolButton(R.drawable.undo64, R.string.button_undo, ToolType.UNDO));
		mButtonsList.add(new ToolButton(R.drawable.redo64, R.string.button_redo, ToolType.REDO));
		mButtonsList.add(new ToolButton(R.drawable.stamp64, R.string.button_floating_box, ToolType.STAMP));
		mButtonsList.add(new ToolButton(R.drawable.import64, R.string.button_import_png, ToolType.IMPORTPNG));
		mButtonsList.add(new ToolButton(R.drawable.eraser_64, R.string.button_eraser, ToolType.ERASER));
		mButtonsList.add(new ToolButton(R.drawable.icon_crop, R.string.button_crop, ToolType.CROP));

		if (fromCatrobat) {
			mButtonsList.add(new ToolButton(R.drawable.ic_menu_save_64, R.string.save, ToolType.SAVE));
			mButtonsList.add(new ToolButton(R.drawable.ic_menu_more_64, R.string.button_back_to_catroid,
					ToolType.BACK_TO_CATROID));
		} else {
			mButtonsList.add(new ToolButton(R.drawable.ic_plain_document_48, R.string.button_filemanager,
					ToolType.FILEMENU));
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
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			rowView = inflater.inflate(R.layout.tool_button, null);
			ImageView imageView = (ImageView) rowView.findViewById(R.id.tool_button_image);
			imageView.setImageResource(mButtonsList.get(position).drawableId);
			TextView textView = (TextView) rowView.findViewById(R.id.tool_button_text);
			textView.setText(mButtonsList.get(position).stringId);
		}
		// TextView toolButton;
		//
		// if (convertView == null) {
		// toolButton = new TextView(context);
		// toolButton.setTextAppearance(context, R.style.ToolButton);
		// toolButton.setGravity(Gravity.CENTER_HORIZONTAL);
		//
		// } else {
		// toolButton = (TextView) convertView;
		// }
		//
		// toolButton.setCompoundDrawablesWithIntrinsicBounds(0, mButtonsList.get(position).drawableId, 0, 0);
		// toolButton.setText(mButtonsList.get(position).stringId);
		return rowView;
	}

}
