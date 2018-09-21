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
import org.gradle.api.resources.ResourceException
import org.gradle.internal.impldep.org.apache.commons.lang.SystemUtils

/**
 * Handles installation of SDK, NDK, images and further modules.
 */
@TypeChecked
class Installer {
    File androidSdk
    SdkManager sdkManager

    Installer(File androidSdk = null) {
        if (androidSdk) {
            this.androidSdk = androidSdk
        } else {
            String sdkRoot = System.getenv()['ANDROID_SDK_ROOT']
            if (!sdkRoot) {
                throw new ResourceException("Environment variable ANDROID_SDK_ROOT is not set!")
            }
            this.androidSdk = new File(sdkRoot)
        }
        this.sdkManager = new SdkManager(this.androidSdk)
    }

    Installer installSdk(Closure sdkSettings) {
        if (sdkSettings) {
            new AndroidSdkTools(androidSdk, sdkSettings).install()
        }
        this
    }

    Installer installNdk(Closure ndkSettings) {
        if (ndkSettings) {
            new AndroidNdk(androidSdk, ndkSettings).install()
        }
        this
    }

    Installer installImage(String image) {
        if (!image) {
            return this
        }

        def packages = ['emulator', image]

        def match = image =~ /system-images;android-(\d+);([^;]+)/
        if (!match) {
            throw new InvalidUserDataException("The image [$image] seems to have a wrong structure!")
        }

        if (match.group(2) == 'google_apis') {
            def version = match.group(1)

            // between api level 15 and 24 there is an explicit add-ons package for google apis listed
            if ((15..24).contains(version as Integer)) {
                packages << "add-ons;addon-google_apis-google-$version".toString()
            }
        }

        installPackages(packages)
    }

    Installer installPackages(List<String> packages) {
        if (!packages) {
            return this
        }

        println("Installing packages [$packages] to [$androidSdk]")
        sdkManager.install(packages)
        this
    }

    Installer writeLicenseFiles() {
        File licencesDir = new File(androidSdk, 'licenses')
        licencesDir.mkdir()
        if (!licencesDir.exists()) {
            throw new ResourceException("The license directory could not be created: $licencesDir")
        }

        def createLicense = { String fileName, String license ->
            File licenseFile = new File(licencesDir, fileName)
            if (!licenseFile.exists()) {
                licenseFile << license
            }
        }

        createLicense('android-sdk-license', '\nd56f5187479451eabf01fb78af6dfcb131a6481e')
        createLicense('android-sdk-preview-license', '\n84831b9409646a918e30573bab4c9c91346d8abd')

        this
    }
}
