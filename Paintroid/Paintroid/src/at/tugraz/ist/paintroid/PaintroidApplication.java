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

package at.tugraz.ist.paintroid;

import android.app.Application;
import android.content.Context;
import at.tugraz.ist.paintroid.command.CommandManager;
import at.tugraz.ist.paintroid.command.implementation.CommandManagerImplementation;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.DrawingSurface;
import at.tugraz.ist.paintroid.ui.Perspective;

public class PaintroidApplication extends Application {
	public static final String TAG = "PAINTROID";
	public static final float MOVE_TOLLERANCE = 5;

	public static Context APPLICATION_CONTEXT;
	public static DrawingSurface DRAWING_SURFACE;
	public static CommandManager COMMAND_MANAGER;
	public volatile static Tool CURRENT_TOOL;
	public static Perspective CURRENT_PERSPECTIVE;

	@Override
	public void onCreate() {
		super.onCreate();

		APPLICATION_CONTEXT = getApplicationContext();
		COMMAND_MANAGER = new CommandManagerImplementation(APPLICATION_CONTEXT);

		// mDisplay = ((WindowManager)
		// getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// int width = display.getWidth();
		// int height = display.getHeight();
	}
}
