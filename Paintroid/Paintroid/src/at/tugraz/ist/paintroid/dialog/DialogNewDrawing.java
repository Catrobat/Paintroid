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
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *   
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import at.tugraz.ist.paintroid.R;

public class DialogNewDrawing extends Dialog implements android.view.View.OnClickListener {

	Button emptydrawingButton;
	Button camdrawingButton;
	Button cancelButton;
	public String newDrawingChoose = null;

	public DialogNewDrawing(Context context) {
		super(context);

		this.setContentView(R.layout.dialog_newdrawing);
		this.setTitle(R.string.dialog_newdrawing_title);
		//this.setMessage(context.getString(R.string.dialog_newdrawing_text));

		Button emptydrawingButton = (Button) this.findViewById(R.id.newdrawing_btn_EmptyDrawing);
		Button camdrawingButton = (Button) this.findViewById(R.id.newdrawing_btn_FromCam);
		Button cancelButton = (Button) this.findViewById(R.id.newdrawing_btn_Cancel);

		emptydrawingButton.setOnClickListener(this);
		camdrawingButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.newdrawing_btn_EmptyDrawing) {
			newDrawingChoose = "NEWDRAWING";
			this.dismiss();

		}

		if (view.getId() == R.id.newdrawing_btn_FromCam) {
			newDrawingChoose = "FROMCAM";
			this.dismiss();
		}

		if (view.getId() == R.id.newdrawing_btn_Cancel) {
			newDrawingChoose = "CANCEL";
			this.dismiss();
		}

	}

}
