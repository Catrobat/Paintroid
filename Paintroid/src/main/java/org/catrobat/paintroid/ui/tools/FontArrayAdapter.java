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

package org.catrobat.paintroid.ui.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FontArrayAdapter extends ArrayAdapter<String> {
	private int normalStyle = Typeface.NORMAL;
	private Typeface sansSerifFontFace = Typeface.create(Typeface.SANS_SERIF, normalStyle);
	private Typeface serifFontFace = Typeface.create(Typeface.SERIF, normalStyle);
	private Typeface defaultFontFace = Typeface.create(Typeface.MONOSPACE, normalStyle);
	private Typeface stcFontFace = Typeface.createFromAsset(getContext().getResources().getAssets(), "STC.otf");
	private Typeface dubaiFontFace = Typeface.createFromAsset(getContext().getResources().getAssets(), "Dubai.TTF");

	public FontArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		TextView spinnerText = (TextView) super.getDropDownView(position, convertView, parent);
		switch (position) {
			case 1:
				spinnerText.setTypeface(serifFontFace);
				break;
			case 2:
				spinnerText.setTypeface(sansSerifFontFace);
				break;
			case 3:
				spinnerText.setTypeface(dubaiFontFace);
				break;
			case 4:
				spinnerText.setTypeface(stcFontFace);
				break;
			default:
				spinnerText.setTypeface(defaultFontFace);
				break;
		}
		return spinnerText;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		TextView spinnerText = (TextView) super.getView(position, convertView, parent);
		switch (position) {
			case 1:
				spinnerText.setTypeface(serifFontFace);
				break;
			case 2:
				spinnerText.setTypeface(sansSerifFontFace);
				break;
			case 3:
				spinnerText.setTypeface(dubaiFontFace);
				break;
			case 4:
				spinnerText.setTypeface(stcFontFace);
				break;
			default:
				spinnerText.setTypeface(defaultFontFace);
				break;
		}
		return spinnerText;
	}
}
