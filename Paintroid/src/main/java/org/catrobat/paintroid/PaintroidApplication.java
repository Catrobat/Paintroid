/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;


import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.LayerBitmapCommand;
import org.catrobat.paintroid.command.implementation.CommandManagerImplementation;
import org.catrobat.paintroid.command.implementation.LayerCommand;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.Perspective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;

import static org.catrobat.paintroid.MultilingualActivity.LANGUAGE_CODE;
import static org.catrobat.paintroid.MultilingualActivity.LANGUAGE_TAG_KEY;

public class PaintroidApplication extends Application {
    public static final String TAG = "PAINTROID";

    public static Context applicationContext;
    public static DrawingSurface drawingSurface;
    public static CommandManager commandManager;
    public static Tool currentTool;
    public static Perspective perspective;
    public static boolean openedFromCatroid = false;
    public static String catroidPicturePath;
    public static boolean isPlainImage = true;
    public static Menu menu;
    public static boolean isSaved = true;
    public static Uri savedPictureUri = null;
    public static boolean saveCopy = false;
    public static boolean scaleImage = true;
    public static int orientation;
    public static boolean isRTL = false;
    public static int colorPickerInitialColor = Color.BLACK;
    public static LinkedList<Pair<CommandManagerImplementation.CommandType, LayerCommand>> layerOperationsCommandList;
    public static LinkedList<Pair<CommandManagerImplementation.CommandType, LayerCommand>> layerOperationsUndoCommandList;
    public static ArrayList<LayerBitmapCommand> drawBitmapCommandsAtLayer;
    public static String defaultSystemLanguage;
    public static SharedPreferences languageSharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        commandManager = new CommandManagerImplementation();

        defaultSystemLanguage = Locale.getDefault().getLanguage();
        // open the App in the last chosen language
        languageSharedPreferences = getSharedPreferences("For_language", Context.MODE_PRIVATE);
        String languageTag = languageSharedPreferences.getString(LANGUAGE_TAG_KEY, "");
        if (Arrays.asList(LANGUAGE_CODE).contains(languageTag)) {
            if (languageTag.length() == 2) {
                MultilingualActivity.updateLocale(getApplicationContext(), languageTag, null);
            } else {
                String language = languageTag.substring(0, 2);
                String country = languageTag.substring(4);
                MultilingualActivity.updateLocale(getApplicationContext(), language, country);
            }
        } else {
            MultilingualActivity.updateLocale(getApplicationContext(), defaultSystemLanguage, null);
        }

    }

    public static String getVersionName(Context context) {
        String versionName = "unknown";
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            versionName = packageInfo.versionName;
        } catch (NameNotFoundException nameNotFoundException) {
            Log.e(PaintroidApplication.TAG, "Name not found",
                    nameNotFoundException);
        }
        return versionName;
    }
}
