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
 */

package at.tugraz.ist.paintroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint.Cap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import at.tugraz.ist.paintroid.R;

public class DialogStrokePicker extends Dialog implements OnClickListener {

	public interface OnStrokeChangedListener {
		void strokeChanged(int stroke);

		void strokeShape(Cap type);
	}

	private OnStrokeChangedListener strokeChangedListener;

	// standard stroke widths in pixels
	private final int stroke_1 = 1;
	private final int stroke_2 = 5;
	private final int stroke_3 = 15;
	private final int stroke_4 = 25;

	public DialogStrokePicker(Context context, OnStrokeChangedListener listener) {

		super(context);
		this.strokeChangedListener = listener;

		initComponents();
	}

	private void initComponents() {
		setContentView(R.layout.dialog_stroke);
		setTitle(R.string.stroke_title);
		setCancelable(true);

		Button btn_cancel = (Button) findViewById(R.id.stroke_btn_Cancel);
		btn_cancel.setOnClickListener(this);

		ImageButton btn_circle = (ImageButton) findViewById(R.id.stroke_ibtn_circle);
		btn_circle.setOnClickListener(this);
		ImageButton btn_rect = (ImageButton) findViewById(R.id.stroke_ibtn_rect);
		btn_rect.setOnClickListener(this);
		ImageButton btn_stroke_1 = (ImageButton) findViewById(R.id.stroke_ibtn_stroke_1);
		btn_stroke_1.setOnClickListener(this);
		ImageButton btn_stroke_2 = (ImageButton) findViewById(R.id.stroke_ibtn_stroke_2);
		btn_stroke_2.setOnClickListener(this);
		ImageButton btn_stroke_3 = (ImageButton) findViewById(R.id.stroke_ibtn_stroke_3);
		btn_stroke_3.setOnClickListener(this);
		ImageButton btn_stroke_4 = (ImageButton) findViewById(R.id.stroke_ibtn_stroke_4);
		btn_stroke_4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

			case R.id.stroke_btn_Cancel:
				this.cancel(); // close Dialog
				break;

			case R.id.stroke_ibtn_circle:
				strokeChangedListener.strokeShape(Cap.ROUND);
				dismiss();
				break;

			case R.id.stroke_ibtn_rect:
				strokeChangedListener.strokeShape(Cap.SQUARE);
				dismiss();
				break;

			case R.id.stroke_ibtn_stroke_1:
				strokeChangedListener.strokeChanged(stroke_1);
				dismiss();
				break;

			case R.id.stroke_ibtn_stroke_2:
				strokeChangedListener.strokeChanged(stroke_2);
				dismiss();
				break;

			case R.id.stroke_ibtn_stroke_3:
				strokeChangedListener.strokeChanged(stroke_3);
				dismiss();
				break;

			case R.id.stroke_ibtn_stroke_4:
				strokeChangedListener.strokeChanged(stroke_4);
				dismiss();
				break;

			default:
				break;
		}
	}
}
