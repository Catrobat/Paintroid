/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2018 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.gradle

import groovy.transform.TypeChecked

/**
 * Settings to use for avd creation.
 *
 * Users can specify additional command line arguments and hardware properties.
 * The hardware properties are then written to the avd config file.
 *
 * For how these settings are used see AvdCreator.
 */
@TypeChecked
class AvdSettings {
    String systemImage
    Integer sdcardSizeMb
    Map hardwareProperties = [:]
    List arguments = []
    private static Map screenDensityLookup = ['ldpi': '120', 'mdpi': '160', 'tvdpi': '213',
                                              'hdpi': '240', 'xhdpi': '320', 'xxhdpi': '480',
                                              'xxxhdpi': '640']
    private static String screenDensityName = 'hw.lcd.density'

    void setScreenDensity(String density) {
        if (density in screenDensityLookup) {
            hardwareProperties[screenDensityName] = screenDensityLookup[density]
        } else if (density.isNumber()) {
            hardwareProperties[screenDensityName] = density
        } else {
            throw new InputMismatchException("'$density' is not a valid density")
        }
    }

    String getScreenDensity() {
        return hardwareProperties[screenDensityName]
    }
}
