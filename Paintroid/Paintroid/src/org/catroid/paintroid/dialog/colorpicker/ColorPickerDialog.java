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
 *
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *        Copyright (C) 2011 Devmil (Michael Lamers) 
 *        Mail: develmil@googlemail.com
 *
 *        Licensed under the Apache License, Version 2.0 (the "License");
 *        you may not use this file except in compliance with the License.
 *        You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *        Unless required by applicable law or agreed to in writing, software
 *        distributed under the License is distributed on an "AS IS" BASIS,
 *        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *        See the License for the specific language governing permissions and
 *        limitations under the License.
 */

package org.catroid.paintroid.dialog.colorpicker;

import org.catroid.paintroid.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ColorPickerDialog extends AlertDialog {

	private ColorPickerView colorPickerView;
	private OnColorPickedListener onColorPickedListener;
	private int newColor = 0;
	private Button buttonOldColor;
	private Button buttonNewColor;

	public interface OnColorPickedListener {
		public void colorChanged(int color);
	}

	public ColorPickerDialog(Context context, OnColorPickedListener listener) {
		super(context);
		onColorPickedListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.colorpicker_dialog);

		buttonOldColor = (Button) findViewById(R.id.btn_oldcolor);
		buttonOldColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		buttonNewColor = (Button) findViewById(R.id.btn_newcolor);
		buttonNewColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onColorPickedListener != null) {
					onColorPickedListener.colorChanged(newColor);
				}
				dismiss();
				changeOldColor(newColor);
			}
		});

		colorPickerView = (ColorPickerView) findViewById(R.id.view_colorpicker);
		colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				changeNewColor(color);
			}
		});
	}

	public void setInitialColor(int color) {
		changeOldColor(color);
		changeNewColor(color);
		colorPickerView.setSelectedColor(color);
	}

	private void changeOldColor(int color) {
		buttonOldColor.setBackgroundColor(color);
		buttonOldColor.setTextColor(~color | 0xFF000000); // without alpha
	}

	private void changeNewColor(int color) {
		buttonNewColor.setBackgroundColor(color);
		buttonNewColor.setTextColor(~color | 0xFF000000); // without alpha
		newColor = color;
	}
}
