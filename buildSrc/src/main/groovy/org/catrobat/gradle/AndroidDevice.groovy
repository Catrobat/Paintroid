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
class AndroidDevice {
    Adb adb
    String androidSerial

    AndroidDevice(Adb adb, String androidSerial = null) {
        this.adb = adb
        this.androidSerial = androidSerial ?: adb.getAndroidSerial()
    }

    /**
     * @return A command builder with the given command and using the serial to run on the device
     */
    CommandBuilder command(List additionalParameters) {
        adb.command().addArguments(['-s', androidSerial]).addArguments(additionalParameters)
    }

    void setGlobalSetting(String setting_name, def value) {
        command(['shell', 'settings', 'put', 'global', setting_name, value.toString()]).verbose().execute()
    }

    void deleteGlobalSetting(String setting_name) {
        command(['shell', 'settings', 'delete', 'global', setting_name]).verbose().execute()
    }

    void writeLogcat(File logcat) {
        if (!stillRunning()) {
            println("WARNING: Cannot retrieve logcat from '$androidSerial'.")
            return
        }
        logcat.withOutputStream { os ->
            command(['logcat', '-d']).executeAsynchronously().waitForProcessOutput(os, os)
        }
    }

    void waitForBooted() {
        println("Waiting for device $androidSerial to be booted.")
        for (int i = 0; i < 60; ++i) {
            def result = command(['shell', 'getprop', 'sys.boot_completed']).execute().trim()
            if (result == "1") {
                println("Deivce $androidSerial booted successfully.")
                return
            }

            sleep(1000)
        }
        throw new BootIncompleteException("The boot of device $androidSerial did not complete.")
    }

    void waitForStopped() {
        println("Wait for device $androidSerial to stop.")
        for (int i = 0; i < 30; ++i) {
            if (!stillRunning()) {
                // device could not be found, thus it is stopped
                return
            }
            sleep(1000)
        }

        // If it turns out that the emulator fails to be killed often consider killing
        // the emulator via OS commands.
        // Similar to what was done in buildScritps/
        throw new ResourceException("Failed to stop device $androidSerial.")
    }

    void install(File apk) {
        println("Installing '$apk'")
        command(['install', apk]).verbose().execute()
    }

    void disableAnimationsGlobally() {
        println("Disabling animations for device $androidSerial.")
        setGlobalSetting('window_animation_scale', '0.0')
        setGlobalSetting('transition_animation_scale', '0.0')
        setGlobalSetting('animator_duration_scale', '0.0',)
    }

    void resetAnimationsGlobally() {
        println("Resetting animations for device $androidSerial.")
        deleteGlobalSetting('window_animation_scale')
        deleteGlobalSetting('transition_animation_scale')
        deleteGlobalSetting('animator_duration_scale')
    }

    private boolean stillRunning() {
        adb.getAndroidDevices().contains(androidSerial)
    }
}
