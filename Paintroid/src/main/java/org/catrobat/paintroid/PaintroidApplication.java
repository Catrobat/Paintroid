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
import java.util.LinkedList;
import java.util.Locale;

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
        languageSharedPreferences = getSharedPreferences("For_language", getApplicationContext().MODE_PRIVATE);
        String langTag = languageSharedPreferences.getString("Nur", "");
        if (langTag.equals("")) {
            Multilingual.setContextLocale(getApplicationContext(), defaultSystemLanguage);
        }
        if (langTag.equals("ar")) {
            Multilingual.setContextLocale(getApplicationContext(), "ar");
        }
        if (langTag.equals("az-rAZ")) {
            Multilingual.setContextLocale(getApplicationContext(), "az-rAZ");
        }
        if (langTag.equals("bs")) {
            Multilingual.setContextLocale(getApplicationContext(), "bs");
        }
        if (langTag.equals("ca-rES")) {
            Multilingual.setContextLocale(getApplicationContext(), "ca-rES");
        }
        if (langTag.equals("cs-rCZ")) {
            Multilingual.setContextLocale(getApplicationContext(), "cs-rCZ");
        }
        if (langTag.equals("da")) {
            Multilingual.setContextLocale(getApplicationContext(), "da");
        }
        if (langTag.equals("de")) {
            Multilingual.setContextLocale(getApplicationContext(), "de");
        }
        if (langTag.equals("en-rAU")) {
            Multilingual.setContextLocale(getApplicationContext(), "en-rAU");
        }
        if (langTag.equals("en-rCA")) {
            Multilingual.setContextLocale(getApplicationContext(), "en-rCA");
        }
        if (langTag.equals("en-rGB")) {
            Multilingual.setContextLocale(getApplicationContext(), "en-rGB");
        }
        if (langTag.equals("es")) {
            Multilingual.setContextLocale(getApplicationContext(), "es");
        }
        if (langTag.equals("fa")) {
            Multilingual.setContextLocale(getApplicationContext(), "fa");
        }
        if (langTag.equals("fr")) {
            Multilingual.setContextLocale(getApplicationContext(), "fr");
        }
        if (langTag.equals("gl-rES")) {
            Multilingual.setContextLocale(getApplicationContext(), "gl-rES");
        }
        if (langTag.equals("gu")) {
            Multilingual.setContextLocale(getApplicationContext(), "gu");
        }
        if (langTag.equals("he")) {
            Multilingual.setContextLocale(getApplicationContext(), "he");
        }
        if (langTag.equals("hi")) {
            Multilingual.setContextLocale(getApplicationContext(), "hi");
        }
        if (langTag.equals("hr")) {
            Multilingual.setContextLocale(getApplicationContext(), "hr");
        }
        if (langTag.equals("hu")) {
            Multilingual.setContextLocale(getApplicationContext(), "hu");
        }
        if (langTag.equals("id")) {
            Multilingual.setContextLocale(getApplicationContext(), "id");
        }
        if (langTag.equals("it")) {
            Multilingual.setContextLocale(getApplicationContext(), "it");
        }
        if (langTag.equals("ja")) {
            Multilingual.setContextLocale(getApplicationContext(), "ja");
        }
        if (langTag.equals("ko")) {
            Multilingual.setContextLocale(getApplicationContext(), "ko");
        }
        if (langTag.equals("mk")) {
            Multilingual.setContextLocale(getApplicationContext(), "mk");
        }
        if (langTag.equals("ml")) {
            Multilingual.setContextLocale(getApplicationContext(), "ml");
        }
        if (langTag.equals("ms")) {
            Multilingual.setContextLocale(getApplicationContext(), "ms");
        }
        if (langTag.equals("nl")) {
            Multilingual.setContextLocale(getApplicationContext(), "nl");
        }
        if (langTag.equals("no")) {
            Multilingual.setContextLocale(getApplicationContext(), "no");
        }
        if (langTag.equals("pl")) {
            Multilingual.setContextLocale(getApplicationContext(), "pl");
        }
        if (langTag.equals("ps")) {
            Multilingual.setContextLocale(getApplicationContext(), "ps");
        }
        if (langTag.equals("pt")) {
            Multilingual.setContextLocale(getApplicationContext(), "pt");
        }
        if (langTag.equals("pt-rBR")) {
            Multilingual.setContextLocale(getApplicationContext(), "pt-rBR");
        }
        if (langTag.equals("ro")) {
            Multilingual.setContextLocale(getApplicationContext(), "ro");
        }
        if (langTag.equals("ru")) {
            Multilingual.setContextLocale(getApplicationContext(), "ru");
        }
        if (langTag.equals("sd")) {
            Multilingual.setContextLocale(getApplicationContext(), "sd");
        }
        if (langTag.equals("sl")) {
            Multilingual.setContextLocale(getApplicationContext(), "sl");
        }
        if (langTag.equals("sq")) {
            Multilingual.setContextLocale(getApplicationContext(), "sq");
        }
        if (langTag.equals("sr-rCS")) {
            Multilingual.setContextLocale(getApplicationContext(), "sr-rCS");
        }
        if (langTag.equals("sr-rSP")) {
            Multilingual.setContextLocale(getApplicationContext(), "sr-rSP");
        }
        if (langTag.equals("sv")) {
            Multilingual.setContextLocale(getApplicationContext(), "sv");
        }
        if (langTag.equals("ta")) {
            Multilingual.setContextLocale(getApplicationContext(), "ta");
        }
        if (langTag.equals("te")) {
            Multilingual.setContextLocale(getApplicationContext(), "te");
        }
        if (langTag.equals("th")) {
            Multilingual.setContextLocale(getApplicationContext(), "th");
        }
        if (langTag.equals("tr")) {
            Multilingual.setContextLocale(getApplicationContext(), "tr");
        }
        if (langTag.equals("ur")) {
            Multilingual.setContextLocale(getApplicationContext(), "ur");
        }
        if (langTag.equals("vi")) {
            Multilingual.setContextLocale(getApplicationContext(), "vi");
        }
        if (langTag.equals("zh-rCN")) {
            Multilingual.setContextLocale(getApplicationContext(), "zh-rCN");
        }
        if (langTag.equals("zh-rTW")) {
            Multilingual.setContextLocale(getApplicationContext(), "zh-rTW");
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
