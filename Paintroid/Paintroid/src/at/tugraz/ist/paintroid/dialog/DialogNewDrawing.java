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

package at.tugraz.ist.paintroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.paintroid.R;

public class DialogNewDrawing extends Dialog implements android.view.View.OnClickListener {

	private Button emptydrawingButton;
	private Button camdrawingButton;
	private Button cancelButton;

	public RESULT_CODE resultCode = RESULT_CODE.CANCEL;

	public static enum RESULT_CODE {
		CANCEL, NEW_EMPTY, NEW_CAMERA
	};

	public DialogNewDrawing(Context context) {
		super(context);

		setContentView(R.layout.dialog_newdrawing);
		setTitle(R.string.dialog_newdrawing_title);

		emptydrawingButton = (Button) this.findViewById(R.id.newdrawing_btn_EmptyDrawing);
		emptydrawingButton.setOnClickListener(this);

		camdrawingButton = (Button) this.findViewById(R.id.newdrawing_btn_FromCam);
		camdrawingButton.setOnClickListener(this);

		cancelButton = (Button) this.findViewById(R.id.newdrawing_btn_Cancel);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.newdrawing_btn_EmptyDrawing:
				resultCode = RESULT_CODE.NEW_EMPTY;
				break;
			case R.id.newdrawing_btn_FromCam:
				resultCode = RESULT_CODE.NEW_CAMERA;
				break;
			default:
				resultCode = RESULT_CODE.CANCEL;
				break;
		}
		dismiss();
	}
}
