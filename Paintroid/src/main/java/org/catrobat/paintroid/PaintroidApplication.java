/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.app.Application;
import android.content.Context;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class PaintroidApplication extends Application {
	public static Context applicationContext;
	public static DrawingSurface drawingSurface;
	public static CommandManager commandManager;
	public static Tool currentTool;
	public static Perspective perspective;
	public static LinkedList<LayerCommand> layerOperationsCommandList;
	public static LinkedList<LayerCommand> layerOperationsUndoCommandList;
	public static ArrayList<LayerBitmapCommand> drawBitmapCommandsAtLayer;
	public static String defaultSystemLanguage;

	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = getApplicationContext();
		commandManager = new CommandManagerImplementation();

		defaultSystemLanguage = Locale.getDefault().getLanguage();
	}
}
