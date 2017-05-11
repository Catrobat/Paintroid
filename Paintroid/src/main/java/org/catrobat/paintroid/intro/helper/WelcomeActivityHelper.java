/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.intro.helper;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import java.util.Locale;

public class WelcomeActivityHelper {
    public static int getDpFromDimension(int dimension, Context context) {
        return (int) (dimension / context.getResources().getDisplayMetrics().density);
    }

    public static int getDpFromInt(float dimension, Context context) {
        return (int) (dimension / context.getResources().getDisplayMetrics().density);
    }

    public static int getSpFromDimension(int dimension, Context context) {
        return (int) (dimension / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    public static boolean isRTL(Locale locale) {
        try {
            final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
            return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                    directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isRTL(Context context) {
        boolean configRTL = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (context.getResources().getConfiguration().getLayoutDirection()
                    == View.LAYOUT_DIRECTION_RTL) {
                configRTL = true;
            }
        }

        return isRTL() || configRTL;
    }

    public static void reverseArray(int[] array) {
        for(int i = 0; i < array.length / 2; i++)
        {
            int temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    public static int calculateTapTargetRadius(int heightInt, Context context, int radiusOffset) {
        return getDpFromInt(heightInt, context) / 2 - radiusOffset;
    }

    public static int calculateTapTargetRadius(float heightDim, Context context, int radiusOffset) {
        return getDpFromDimension((int) heightDim, context) / 2 - radiusOffset;
    }


}
