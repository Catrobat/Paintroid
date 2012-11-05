/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
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

package org.catrobat.paintroid.dialog.colorpicker;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.BaseDialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class ColorPickerDialog extends BaseDialog {

	private ColorPickerView mColorPickerView;
	private OnColorPickedListener mOnColorPickedListener;
	private int mNewColor;
	// private int mOldColor;
	// private Button mButtonOldColor;
	private Button mButtonNewColor;

	public interface OnColorPickedListener {
		public void colorChanged(int color);
	}

	public ColorPickerDialog(Context context, OnColorPickedListener listener) {
		super(context);
		mOnColorPickedListener = listener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.colorpicker_dialog);

		// mButtonOldColor = (Button) findViewById(R.id.btn_oldcolor);
		// mButtonOldColor.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// dismiss();
		// }
		// });

		mButtonNewColor = (Button) findViewById(R.id.btn_newcolor);
		mButtonNewColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnColorPickedListener != null) {
					mOnColorPickedListener.colorChanged(mNewColor);
				}
				dismiss();
				// changeOldColor(mNewColor);
			}
		});

		mColorPickerView = (ColorPickerView) findViewById(R.id.view_colorpicker);
		mColorPickerView
				.setOnColorChangedListener(new ColorPickerView.OnColorChangedListener() {
					@Override
					public void colorChanged(int color) {
						changeNewColor(color);
					}
				});
		// mColorPickerView.mColorPickerScrollView = (ScrollView)
		// findViewById(R.id.colorpicker_scroll_view);
	}

	public void setInitialColor(int color) {
		// changeOldColor(color);
		changeNewColor(color);
		// mOldColor = color;
		mColorPickerView.setSelectedColor(color);
	}

	// private void changeOldColor(int color) {
	// mButtonOldColor.setBackgroundColor(color);
	// mButtonOldColor.setTextColor(~color | 0xFF000000); // without alpha
	// }

	private void changeNewColor(int color) {
		mButtonNewColor.setBackgroundColor(color);
		mButtonNewColor.setTextColor(~color | 0xFF000000); // without alpha
		mNewColor = color;
	}

	@Override
	public void onBackPressed() {
		// if (!(mNewColor == mOldColor)) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		// builder.setMessage(R.string.dialog_newcolor_text);
		// builder.setTitle(R.string.dialog_newcolor_title);
		// builder.setCancelable(false);
		// builder.setPositiveButton(R.string.yes,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		if (mOnColorPickedListener != null) {
			mOnColorPickedListener.colorChanged(mNewColor);
		}
		// dialog.dismiss();
		// }
		// });

		// builder.setNegativeButton(R.string.no,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });
		//
		// AlertDialog dialog = builder.create();
		// dialog.show();
		// }
		super.onBackPressed();
	}
}
