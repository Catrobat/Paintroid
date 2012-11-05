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

package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.Tool.ToolType;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DialogHelp extends BaseDialog implements OnClickListener {

	private int id_;
	private ToolType toolType_;

	/**
	 * Constructor
	 * 
	 * @param id
	 * 
	 */
	public DialogHelp(Context context, int id, ToolType toolType) {
		super(context);
		id_ = id;
		toolType_ = toolType;
		init();
	}

	public DialogHelp(Context context, int id) {
		super(context);
		id_ = id;
		toolType_ = null;
		init();
	}

	/**
	 * Show the dialog
	 * 
	 */
	private void init() {

		setContentView(R.layout.dialog_help);
		setTitle(R.string.help_title);
		setCancelable(true);

		TextView text = (TextView) findViewById(R.id.help_tview_Text);

		if (toolType_ == null) {
			setToolMenuHelp(text);
		} else {
			setToolBarHelp(text);
		}

		Button button = (Button) findViewById(R.id.help_btn_Done);
		button.setText(R.string.help_done);
		button.setOnClickListener(this);

	}

	private void setToolBarHelp(TextView text) {

		switch (id_) {
		case R.id.btn_status_tool:
			switch (toolType_) {
			case MAGIC:
				text.setText(R.string.help_content_wand);
				break;
			case CURSOR:
				text.setText(R.string.help_content_cursor);
				break;
			case BRUSH:
				text.setText(R.string.help_content_brush);
				break;
			case PIPETTE:
				text.setText(R.string.help_content_eyedropper);
				break;
			case ZOOM:
				text.setText(R.string.help_content_zoom);
				break;
			case STAMP:
				text.setText(R.string.help_content_stamp);
				break;
			default:
				break;
			}
			break;

		// case R.id.ibtn_Tool:
		// text.setText(R.string.help_content_brush);
		// break;

		// case R.id.ibtn_Choose:
		// text.setText(R.string.help_content_eyedropper);
		// break;
		//
		// case R.id.ibtn_Action:
		// text.setText(R.string.help_content_wand);
		// break;

		case R.id.btn_status_undo:
			text.setText(R.string.help_content_undo);
			break;

		case R.id.btn_status_parameter:
			switch (toolType_) {
			case MAGIC:
			case CURSOR:
			case BRUSH:
			case PIPETTE:
				text.setText(R.string.help_content_stroke);
				break;
			case ZOOM:
				break;
			case STAMP:
				text.setText(R.string.help_content_rotate_right);
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}
	}

	private void setToolMenuHelp(TextView text) {
		switch (id_) {
		case R.string.button_brush:
			text.setText(R.string.help_content_brush);
			break;
		case R.string.button_cursor:
			text.setText(R.string.help_content_cursor);
			break;
		case R.string.button_choose:
			text.setText(R.string.help_content_choose);
			break;
		case R.string.button_zoom:
			text.setText(R.string.help_content_zoom);
			break;
		case R.string.button_pipette:
			text.setText(R.string.help_content_eyedropper);
			break;
		case R.string.button_magic:
			text.setText(R.string.help_content_wand);
			break;
		case R.string.button_undo:
			text.setText(R.string.help_content_undo);
			break;
		case R.string.button_redo:
			text.setText(R.string.help_content_redo);
			break;
		case R.string.button_stamp:
			text.setText(R.string.help_content_stamp);
			break;
		case R.string.button_import_image:
			text.setText(R.string.help_content_import_png);
			break;
		default:
			break;
		}
	}

	/**
	 * Handles the onClick events
	 * 
	 * Closes the dialog if the done button was hit.
	 * 
	 */
	@Override
	public void onClick(View v) {

		// close dialog
		if (v.getId() == R.id.help_btn_Done) {
			this.cancel();
		}
	}
}
