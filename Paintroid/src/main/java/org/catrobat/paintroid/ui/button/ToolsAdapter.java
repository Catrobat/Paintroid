/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.button;

import java.util.ArrayList;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ToolsAdapter extends BaseAdapter {

	private Context mContext;

	private ArrayList<ToolType> mButtonsList;

	public ToolsAdapter(Context context, boolean fromCatrobat) {
		this.mContext = context;
		initButtons(fromCatrobat);
	}

	private void initButtons(boolean fromCatrobat) {

		mButtonsList = new ArrayList<ToolType>();

		mButtonsList.add(ToolType.BRUSH);
		mButtonsList.add(ToolType.CURSOR);
		mButtonsList.add(ToolType.PIPETTE);
		mButtonsList.add(ToolType.FILL);
		mButtonsList.add(ToolType.STAMP);
		mButtonsList.add(ToolType.RECT);
		mButtonsList.add(ToolType.ELLIPSE);
		mButtonsList.add(ToolType.IMPORTPNG);
		mButtonsList.add(ToolType.CROP);
		mButtonsList.add(ToolType.ERASER);
		mButtonsList.add(ToolType.FLIP);
		mButtonsList.add(ToolType.MOVE);
		mButtonsList.add(ToolType.ZOOM);
		mButtonsList.add(ToolType.ROTATE);
		mButtonsList.add(ToolType.LINE);

		// deactivateToolsFromPreferences();

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

	public ToolType getToolType(int position) {
		return mButtonsList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if (rowView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			rowView = inflater.inflate(R.layout.tool_button, null);
			ImageView imageView = (ImageView) rowView
					.findViewById(R.id.tool_button_image);
			imageView.setImageResource(mButtonsList.get(position)
					.getImageResource());
			TextView textView = (TextView) rowView
					.findViewById(R.id.tool_button_text);
			textView.setText(mButtonsList.get(position).getNameResource());
		}
		return rowView;
	}

	/* EXCLUDE PREFERENCES FOR RELEASE */
	// private void deactivateToolsFromPreferences() {
	// SharedPreferences sharedPreferences = PreferenceManager
	// .getDefaultSharedPreferences(mContext);
	// for (int toolsIndex = 0; toolsIndex < mButtonsList.size(); toolsIndex++)
	// {
	// final String toolButtonText = mContext.getString(mButtonsList.get(
	// toolsIndex).getNameResource());
	// if (sharedPreferences.getBoolean(toolButtonText, false) == false) {
	// mButtonsList.remove(toolsIndex);
	// toolsIndex--;
	// }
	// }
	// }

}
