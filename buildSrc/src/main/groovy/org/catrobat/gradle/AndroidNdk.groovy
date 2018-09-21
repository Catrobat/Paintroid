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
 * Allows to install the Android SDK Tools.
 */
@TypeChecked
class AndroidNdk extends ManualToolDownloader {
    boolean installLatest = false
    String latestVersion
    String packageName = 'ndk-bundle'

    AndroidNdk(File androidSdk, Closure settings = null) {
        super(androidSdk)

        addVersion('16.1.4479499', {
            linux('android-ndk-r16b-linux-x86_64.zip', 'bcdea4f5353773b2ffa85b5a9a2ae35544ce88ec5b507301d8cf6a76b765d901')
            mac('android-ndk-r16b-darwin-x86_64.zip', '9654a692ed97713e35154bfcacb0028fdc368128d636326f9644ed83eec5d88b')
            windows('android-ndk-r16b-windows-x86_64.zip', '4c6b39939b29dfd05e27c97caf588f26b611f89fe95aad1c987278bd1267b562')
        }, ['r16b', '16b'])

        if (settings) {
            apply(settings)
        }
    }

    @Override
    String packageDescription() {
        'Android NDK'
    }

    @Override
    void setVersion(String version) {
        if (version == 'latest') {
            installLatest = true
        } else {
            installLatest = false
            super.setVersion(version)
        }
    }

    @Override
    protected void checkInstalled() {
        String v = version
        if (installLatest) {
            if (!latestVersion) {
                latestVersion = sdkManager.latestVersion(packageName)
            }
            v = latestVersion
        }

        throwOnFailure(sdkManager.isInstalled(packageName, v), "Package [$packageName] version [$v] is not installed!")
    }

    @Override
    protected void forceInstall() {
        println("Installing ${packageDescription()}...")

        if (installLatest) {
            sdkManager.install([packageName])
        } else {
            downloadAndExtract(androidSdk, packageName)
        }
        checkInstalled()

        println("Installed ${packageDescription()}.")
    }
}
