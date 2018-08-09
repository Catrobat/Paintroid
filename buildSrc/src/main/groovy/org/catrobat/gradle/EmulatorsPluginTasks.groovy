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
import groovy.transform.TypeCheckingMode
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.Task

@TypeChecked
class EmulatorsPluginTasks {
    Project project
    EmulatorsPluginExtension ext

    EmulatorsPluginTasks(Project project, EmulatorsPluginExtension ext) {
        this.project = project
        this.ext = ext
    }

    void registerTasks() {
        registerRetrieveLogcatTask()
        registerStartEmulatorTask()
        registerStopEmulatorTask()
        registerAdbDisableAnimationsGloballyTask()
        registerSdbResetAnimationsGloballyTask()
    }


    private void registerRetrieveLogcatTask() {
        registerTask('retrieveLogcat', {
            description = 'Retrieves the logcat.txt from the device.'
            group = 'android'

            doLast {
                def device = androidDevice()
                def logcat = new File(project.rootDir, 'logcat.txt')
                device.writeLogcat(logcat)
            }
        })
    }

    private void registerStartEmulatorTask() {
        registerTask('startEmulator', {
            description = 'Starts the android emulator. Use -Pemulator or EMULATOR_OVERRIDE to specify the emulator to use.'
            group = 'android'

            doLast {
                def emulatorName = emulatorName()
                def emulatorExt = lookupEmulator(emulatorName)

                def avdCreator = new AvdCreator(sdkDirectory(), determineEnvironment())
                avdCreator.reuseOrCreateAvd(emulatorName, emulatorExt.avdSettings)
                reuseRunningOrStartEmulator(emulatorName)
            }
        })
    }

    private void registerStopEmulatorTask() {
        registerTask('stopEmulator', {
            description = 'Stops the android emulator'
            group = 'android'

            doLast {
                try {
                    def device = androidDevice()
                    device.command(['emu', 'kill']).verbose().execute()
                    device.waitForStopped()
                } catch (NoDeviceException) {
                    // already stopped
                }
            }
        })
    }


    private void registerAdbDisableAnimationsGloballyTask() {
        registerTask('adbDisableAnimationsGlobally', {
            description = 'Disables android animations globally on the connected device'
            group = 'android'

            doLast {
                logger.lifecycle(description)
                androidDevice().disableAnimationsGlobally()
            }
        })
    }

    private void registerSdbResetAnimationsGloballyTask() {
        registerTask('adbResetAnimationsGlobally', {
            description = 'Reset android animations globally on the connected device'
            group = 'android'

            doLast {
                logger.lifecycle(description)
                androidDevice().resetAnimationsGlobally()
            }
        })
    }

    private void registerTask(String name, @DelegatesTo(Task) Closure settings) {
        project.task(name, settings)
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private File sdkDirectory() {
        project.android.sdkDirectory
    }

    private String emulatorName() {
        String name = System.getenv()["EMULATOR_OVERRIDE"] ?: project.properties["emulator"]
        if (name) {
            return name
        }

        if (ext.emulatorLookup.size() != 1) {
            throw new InvalidUserDataException("Specify the emulator to use. Available emulators: ${ext.emulatorLookup.keySet()}")
        }

        ext.emulatorLookup.keySet().iterator().next()
    }

    private EmulatorExtension lookupEmulator(String name) {
        if (!ext.emulatorLookup.containsKey(name)) {
            throw new InvalidUserDataException("There is no emulator named [$name]!")
        }

        ext.emulatorLookup[name]
    }

    /**
     * Ensure that any function here works both on local machines as well as one Jenkins.
     *
     * This is done by setting the needed environment variables.
     */
    private Map<String, String> determineEnvironment() {
        def env = new HashMap(System.getenv())

        def fallbackEnv = {k, v ->
            if (!env.containsKey(k)) {
                println("ENV: Setting unspecified $k to [$v]")
                env[k.toString()] = v.toString()
            }
        }

        fallbackEnv('ANDROID_AVD_HOME', env['WORKSPACE'] ?: project.rootDir.toPath())
        env
    }

    @TypeChecked(TypeCheckingMode.SKIP)
    private Adb adb() {
        new Adb(project.android.getAdbExecutable())
    }

    private AndroidDevice androidDevice(String androidSerial = null) {
        new AndroidDevice(adb(), androidSerial)
    }

    private void reuseRunningOrStartEmulator(String emulatorName) {
        def proc
        def device

        try {
            // try to access an already running emulator
            device = androidDevice()
        } catch (DeviceNotFoundException e) {
            // A specific device was specified that does not exist
            throw e
        } catch (NoDeviceException) {
            // no device running, start one
            println('Start the emulator!')

            def emulatorStarter = new EmulatorStarter(sdkDirectory())
            lookupEmulator(emulatorName).emulatorParameters.each {
                Utils.applySettings(it, emulatorStarter)
            }
            proc = emulatorStarter.start(emulatorName, determineEnvironment())

            try {
                device = androidDevice(adb().waitForSerial())
            } catch(NoDeviceException e) {
                proc.waitForOrKill(1)
                throw e
            }
        }

        try {
            println("Using device ${device.androidSerial}")
            device.waitForBooted()
        } catch (BootIncompleteException e) {
            proc?.waitForOrKill(1)
            throw e
        }
    }
}
