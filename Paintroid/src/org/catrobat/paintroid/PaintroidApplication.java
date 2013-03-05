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

package org.catrobat.paintroid;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import android.app.Application;
import android.content.Context;

public class PaintroidApplication extends Application {
	public static final String TAG = "PAINTROID";

	public static Context applicationContext;
	public static DrawingSurface drawingSurface;
	public static CommandManager commandManager;
	public static Tool currentTool;
	public static Perspective perspective;
	public static boolean openedFromCatroid = false;

	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = getApplicationContext();
		commandManager = new CommandManagerImplementation(applicationContext);
	}

}
