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

@TypeChecked
class Adb {
    File adbExe

    Adb(File adbExe) {
        this.adbExe = adbExe
    }

    CommandBuilder command() {
        new CommandBuilder(adbExe)
    }

    List<String> getAndroidDevices() {
        List<String> deviceIds = []
        command().addArguments(['devices']).execute().eachLine { line ->
            line = line.trim()
            def i = line.indexOf('\tdevice')
            if (i > 0) {
                deviceIds << line.substring(0, i)
            }
        }
        deviceIds
    }

    String getAndroidSerial() {
        def availableDevices = getAndroidDevices()
        def androidSerial = System.getenv('ANDROID_SERIAL')

        if (androidSerial?.trim() && !availableDevices.contains(androidSerial)) {
            throw new DeviceNotFoundException("Device ${androidSerial} not found")
        } else if (availableDevices.size() == 0) {
            throw new NoDeviceExcpetion("No connected devices!")
        } else {
            androidSerial = availableDevices.first()
        }

        return androidSerial.toString().trim()
    }

    String waitForSerial(int timeout=60) {
        for (int i = 0; i < timeout; ++i) {
            try {
                return getAndroidSerial()
            } catch (NoDeviceExcpetion) {
                sleep(1000)
            }
        }
        return getAndroidSerial()
    }
}
