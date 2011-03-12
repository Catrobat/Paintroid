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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * A standard error dialog.
 * 
 * Status: refactored 20.02.2011
 * @author PaintroidTeam
 * @version 0.6.4b
 */
public class DialogError extends AlertDialog {

	/**
	 * Constructor that handles strings
	 * 
	 * @param title Displayed title
	 * @param message Displayed message
	 */
	public DialogError(Context context, String title, String message) {
		super(context);
		this.setIcon(android.R.drawable.ic_menu_report_image);
		this.setTitle(title);
		this.setMessage(message);
		this.setButton("OK", 
				       new DialogInterface.OnClickListener() { 
			                public void onClick(DialogInterface dialog, int id) {
	        	              dialog.cancel();
	                        }
		              	});
	}
	
	/**
	 * Constructor that handles resources
	 * 
	 * @param titleResource Displayed title as a resource id
	 * @param messageResource Displayed message as a resource id
	 */
	public DialogError(Context context, int titleResource, int messageResource) {
		this(context, context.getString(titleResource), context.getString(messageResource));
	}
}
