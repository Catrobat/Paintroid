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

package org.catrobat.paintroid.ui.tools;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.catrobat.paintroid.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

public class FontArrayAdapter extends ArrayAdapter<String> {
	private Typeface sansSerif = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
	private Typeface serif = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
	private Typeface monospace = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
	private Typeface stc = ResourcesCompat.getFont(getContext(), R.font.stc_regular);
	private Typeface dubai = ResourcesCompat.getFont(getContext(), R.font.dubai);

	private final Typeface[] typeFaces = {
			sansSerif,
			monospace,
			serif,
			dubai,
			stc,
	};

	public FontArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		TextView spinnerText = (TextView) super.getDropDownView(position, convertView, parent);
		spinnerText.setTypeface(typeFaces[position]);
		return spinnerText;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		TextView spinnerText = (TextView) super.getView(position, convertView, parent);
		spinnerText.setTypeface(typeFaces[position]);
		return spinnerText;
	}
}
