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
class AndroidSdkTools extends ManualToolDownloader {
    String packageName = 'tools'

    AndroidSdkTools(File androidSdk, Closure settings = null) {
        super(androidSdk)

        addVersion('26.1.1', {
            linux('sdk-tools-linux-4333796.zip', '92ffee5a1d98d856634e8b71132e8a95d96c83a63fde1099be3d86df3106def9')
            mac('sdk-tools-darwin-4333796.zip', 'ecb29358bc0f13d7c2fa0f9290135a5b608e38434aad9bf7067d0252c160853e')
            windows('sdk-tools-windows-4333796.zip', '7e81d69c303e47a4f0e748a6352d85cd0c8fd90a5a95ae4e076b5e5f960d3c7a')
        }, ['latest'])

        if (settings) {
            apply(settings)
        }
    }

    @Override
    String packageDescription() {
        'Android SDK Tools'
    }

    /**
     * Throws an exception if the tools are not installed (correctly).
     */
    @Override
    protected void checkInstalled() {
        throwOnFailure(androidSdk.isDirectory(), "[$androidSdk] is not a directory!")
        throwOnFailure(sdkManager.canExecute(), "[$sdkManager] is not executable!")
        throwOnFailure(sdkManager.isInstalled(packageName, version), "Package [$packageName] version [$version] is not installed!")
    }

    @Override
    protected void forceInstall() {
        println("Installing ${packageDescription()}...")
        downloadAndExtract(androidSdk, packageName)
        checkInstalled()
        println("Installed ${packageDescription()}.")
    }
}
