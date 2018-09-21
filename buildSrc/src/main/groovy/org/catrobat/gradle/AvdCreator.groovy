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

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.TypeChecked

/**
 * Creates an avd with the given settings.
 *
 * The hardware properties are written to the avd config file.
 */
@TypeChecked
class AvdCreator {
    File sdkDirectory
    Map environment
    File avdStore
    File existingAvds

    AvdCreator(File sdkDirectory, Map environment) {
        this.sdkDirectory = sdkDirectory
        this.environment = environment

        String avd_home = environment['ANDROID_AVD_HOME']
        if (!avd_home) {
            throw new IllegalStateException("The environment does not contain an ANDROID_AVD_HOME.")
        }
        this.avdStore = new File(avd_home)
        this.existingAvds = new File(avdStore, 'avdstore.json')
    }

    void createAvd(String avdName, AvdSettings settings) {
        checkSettings(settings)

        storeAvd(avdName, settings)

        def avdmanager = new CommandBuilder(Utils.joinPaths(sdkDirectory, 'tools', 'bin', 'avdmanager'), '.bat')

        avdmanager.addArguments(['create', 'avd', '-f', '-n', avdName])
        avdmanager.addOptionalArguments(settings.sdcardSizeMb, ['-c', "${settings.sdcardSizeMb}M"])
        avdmanager.addArguments(['-k', settings.systemImage])
        avdmanager.addArguments(settings.arguments)

        avdmanager.input('no\r\n').directory(avdStore).environment(environment).verbose()
        avdmanager.execute()

        // update the avd ini-file with the specified properties
        def avdConfigFile = new IniFile(Utils.joinPaths(avdStore, avdName + '.avd', 'config.ini'))
        avdConfigFile.updateValues(settings.hardwareProperties)
    }

    void reuseOrCreateAvd(String avdName, AvdSettings settings) {
        if (!containsAvd(avdName, settings)) {
            println("Create AVD")
            createAvd(avdName, settings)
        }
    }

    private void checkSettings(AvdSettings settings) {
        def throw_if_null = { name, value ->
            if (!value) {
                throw new IllegalStateException("Setting '$name' is not specified but needed by createAvd.")
            }
        }

        throw_if_null(settings.systemImage, 'systemImage')
        throw_if_null(settings.screenDensity, 'screenDensity')
    }

    private boolean containsAvd(String name, AvdSettings settings) {
        def avds = readExistingAvds()
        if (avds[name] != Utils.asMap(settings)) {
            return false
        }

        avdDir(name).exists() && avdIni(name).exists()
    }

    private Map readExistingAvds() {
        if (!existingAvds.exists() || !existingAvds.canRead()) {
            return [:]
        }

        new JsonSlurper().parseText(existingAvds.text) as Map ?: [:]
    }

    void storeAvd(String name, AvdSettings settings) {
        println("Storing avd [$name]")
        avdDir(name).deleteDir()
        avdIni(name).delete()

        Map avds = readExistingAvds()
        avds[name] = Utils.asMap(settings)
        existingAvds.delete()
        existingAvds << JsonOutput.toJson(avds)
    }

    private File avdDir(String name) {
        new File(avdStore, "${name}.avd")
    }

    private File avdIni(String name) {
        new File(avdStore, "${name}.ini")
    }
}
