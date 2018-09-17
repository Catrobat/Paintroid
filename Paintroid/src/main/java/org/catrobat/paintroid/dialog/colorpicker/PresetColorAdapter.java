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

package org.catrobat.paintroid.dialog.colorpicker;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.paintroid.R;

import java.util.List;

public class PresetColorAdapter extends RecyclerView.Adapter<PresetColorAdapter.PresetColorViewHolder> {
	private final Callback callback;
	private final List<Integer> colors;

	PresetColorAdapter(Callback callback, @ColorInt List<Integer> colors) {
		this.callback = callback;
		this.colors = colors;
	}

	@NonNull
	@Override
	public PresetColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = inflater.inflate(R.layout.color_chooser_item_presetview, parent, false);
		return new PresetColorViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull final PresetColorViewHolder holder, int position) {
		final int color = colors.get(position);
		Drawable background = ColorPickerDialog.CustomColorDrawable.newCustomColorDrawable(color);
		holder.itemView.setBackground(background);
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callback.onColorClicked(color);
			}
		});
	}

	@Override
	public int getItemCount() {
		return colors.size();
	}

	public interface Callback {
		void onColorClicked(@ColorInt int color);
	}

	public static class PresetColorViewHolder extends RecyclerView.ViewHolder {
		public PresetColorViewHolder(View itemView) {
			super(itemView);
		}
	}
}
