/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

package at.tugraz.ist.paintroid.dialog.colorpicker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import at.tugraz.ist.paintroid.R;

public class ColorPickerDialog extends Dialog {

	private ColorPickerView colorPickerView;
	private OnColorPickedListener onColorPickedListener;
	private int initialColor;
	private int newColor;
	private Button buttonOldColor;
	private Button buttonNewColor;

	public interface OnColorPickedListener {
		public void colorChanged(int color);
	}

	public ColorPickerDialog(Context context, OnColorPickedListener listener, int color) {
		super(context);
		onColorPickedListener = listener;
		initialColor = color;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout mainContent = new LinearLayout(getContext());
		mainContent.setOrientation(LinearLayout.VERTICAL);

		LinearLayout colorButtonLayout = new LinearLayout(getContext());
		colorButtonLayout.setBackgroundResource(R.drawable.transparentrepeat);
		buttonOldColor = new Button(getContext());
		buttonOldColor.setText(getContext().getResources().getString(R.string.color_old_color));
		LinearLayout.LayoutParams buttonOldColorParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		buttonOldColorParams.weight = 1;
		colorButtonLayout.addView(buttonOldColor, buttonOldColorParams);

		buttonOldColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		buttonNewColor = new Button(getContext());
		buttonNewColor.setText(getContext().getResources().getString(R.string.color_new_color));
		LinearLayout.LayoutParams buttonNewColorParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		buttonNewColorParams.weight = 1;
		colorButtonLayout.addView(buttonNewColor, buttonNewColorParams);

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

		colorPickerView = new ColorPickerView(getContext());
		colorPickerView.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				changeNewColor(color);
			}
		});

		LinearLayout.LayoutParams colorPickerViewParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		colorPickerViewParams.weight = 1;
		LinearLayout.LayoutParams colorButtonLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		colorButtonLayoutParams.weight = 0;

		mainContent.addView(colorPickerView, colorPickerViewParams);
		mainContent.addView(colorButtonLayout, colorButtonLayoutParams);

		setContentView(mainContent);

		buttonOldColor.setBackgroundColor(initialColor);
		buttonOldColor.setTextColor(~initialColor | 0xFF000000); // without
																	// alpha
		changeNewColor(initialColor);
		colorPickerView.setSelectedColor(initialColor);
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
