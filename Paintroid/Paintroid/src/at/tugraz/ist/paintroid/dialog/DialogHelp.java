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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;

/**
 * The help dialog displays information about the long clicked button
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class DialogHelp extends Dialog implements OnClickListener{

	private int id_;
	/**
	 * Constructor
	 * @param id 
	 * 
	 */
	public DialogHelp(Context context, int id) {
		super(context);
		id_ = id;
		init();

	}

	/**
	 * Show the dialog
	 * 
	 */
	private void init(){
		
		setContentView(R.layout.dialog_help);
		setTitle(R.string.help_title);
		setCancelable(true);

		TextView text = (TextView) findViewById(R.id.help_tview_Text);
		
		switch (id_) {
		case R.id.ibtn_Scroll:
			text.setText(R.string.help_content_scroll);
			break;

		case R.id.ibtn_Zoom:
			Log.d("PaintroidHelp", "Zoombutton LONG Click");
			text.setText(R.string.help_content_zoom);
			break;

		case R.id.ibtn_Draw:
			text.setText(R.string.help_content_brush);
			break;

		case R.id.ibtn_Choose:
			text.setText(R.string.help_content_eyedropper);
			break;

		case R.id.ibtn_Action:
			text.setText(R.string.help_content_wand);
			break;

		case R.id.ibtn_Undo:
			text.setText(R.string.help_content_undo);
			break;
			
		case R.id.ibtn_Redo:
			text.setText(R.string.help_content_redo);
			break;

		case R.id.ibtn_File:
			text.setText(R.string.help_content_file);
			break;

		case R.id.btn_Color: 
			text.setText(R.string.help_content_color);
			break;

		case R.id.ibtn_Stroke:
			text.setText(R.string.help_content_stroke);
			break;
		default:
			break;
		}
		

		Button button = (Button) findViewById(R.id.help_btn_Done);
		button.setText(R.string.help_done);
		button.setOnClickListener(this); 
   
		show();
	}
	
	/**
	 * Handles the onClick events
	 * 
	 * Closes the dialog if the done button
	 * was hit.
	 * 
	 */
	@Override
	public void onClick(View v) {
		
		// close dialog
		if (v.getId() == R.id.help_btn_Done)
			this.cancel();
	}
}
