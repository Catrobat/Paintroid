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
 * Creates an avd with the given settings.
 *
 * Users can specify additional command line arguments (addArguments) and
 * hardware properties (addProperty).
 * The hardware properties are then written to the avd config file.
 */
@TypeChecked
class AvdCreator {
    String systemImage
    Integer sdcardSizeMb
    private static Map screenDensityLookup = ['ldpi': '120', 'mdpi': '160', 'tvdpi': '213',
                                              'hdpi': '240', 'xhdpi': '320', 'xxhdpi': '480',
                                              'xxxhdpi': '640']
    private static String screenDensityName = 'hw.lcd.density'
    private Map properties = [:]
    private List arguments = []
    private AvdStore avdStore
    private File sdkDirectory

    /**
     * Creates an AvdCreator with the given closure.
     *
     * You can call functions of AvdCreator from within the closure
     * and access parameters.
     *
     * For example:
     * def avdCreator = new AvdCreator {
     *     systemImage = 'system-images;android-24;default;x86_64'
     *     sdcardSizeMb = 200
     * }
     *
     * @param closure
     */
    AvdCreator(File sdkDirectory, AvdStore avdStore) {
        this.sdkDirectory = sdkDirectory
        this.avdStore = avdStore
    }

    AvdCreator apply(Closure settings) {
        settings = (Closure)settings.clone()
        settings.delegate = this
        settings.resolveStrategy = Closure.DELEGATE_FIRST
        settings()
        this
    }

    void setScreenDensity(String density) {
        if (density in screenDensityLookup) {
            properties[screenDensityName] = screenDensityLookup[density]
        } else if (density.isNumber()) {
            properties[screenDensityName] = density
        } else {
            throw new InputMismatchException("'$density' is not a valid density")
        }
    }

    String getScreenDensity() {
        return properties[screenDensityName]
    }

    void addArguments(List arguments) {
        this.arguments += arguments
    }

    void addProperties(Map properties) {
        this.properties << properties
    }

    void createAvd(Map environment) {
        checkSettings()

        def avdName = avdStore.generateAvdName()
        def avdmanager = new CommandBuilder(Utils.joinPaths(sdkDirectory, 'tools', 'bin', 'avdmanager'), '.bat')

        avdmanager.addArguments(['create', 'avd', '-f', '-n', avdName])
        avdmanager.addOptionalArguments(sdcardSizeMb, ['-c', "${sdcardSizeMb}M"])
        avdmanager.addArguments(['-k', systemImage])
        avdmanager.addArguments(arguments)

        avdmanager.input('no\r\n').directory(avdStore.avdStore).environment(environment).verbose()
        avdmanager.execute()

        // update the avd ini-file with the specified properties
        avdStore.avdConfigFile.setValues(properties)
    }

    void reuseOrCreateAvd(Map environment) {
        try {
            // first check whether an avd exists already
            avdStore.readAvdName()
        } catch (NoAvdException e) {
            println(e.message)
            println("Create AVD")
            createAvd(environment)
        }
    }

    private void checkSettings() {
        def throw_if_null = { name, value ->
            if (!value) {
                throw new IllegalStateException("Setting '$name' is not specified but needed by createAvd.")
            }
        }

        throw_if_null(systemImage, 'systemImage')
        throw_if_null(screenDensity, 'screenDensity')
    }
}
