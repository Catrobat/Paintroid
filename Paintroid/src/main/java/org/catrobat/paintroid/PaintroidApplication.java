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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.contract.LayerContracts;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import java.io.File;
import java.util.Locale;

public class PaintroidApplication extends Application {
	public static DrawingSurface drawingSurface;
	public static CommandManager commandManager;
	public static Tool currentTool;
	public static Perspective perspective;
	public static String defaultSystemLanguage;
	public static LayerContracts.Model layerModel;
	public static File cacheDir;
	public static Bitmap checkeredBackgroundBitmap;

	@Override
	public void onCreate() {
		super.onCreate();
		cacheDir = getCacheDir();
		defaultSystemLanguage = Locale.getDefault().getLanguage();
		checkeredBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pocketpaint_checkeredbg);
	}
}
