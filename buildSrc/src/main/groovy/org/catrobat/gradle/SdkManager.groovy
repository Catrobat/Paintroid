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
import org.gradle.api.resources.ResourceException

/**
 * Handles the interaction with the sdkmanager executable.
 */
@TypeChecked
class SdkManager {
    File sdkManager

    SdkManager(File androidSdk) {
        this.sdkManager = Utils.executable(Utils.joinPaths(new File(androidSdk, 'tools'), 'bin', 'sdkmanager'), '.bat')
    }

    String toString() {
        sdkManager.toString()
    }

    void install(List<String> packages) {
        if (packages) {
            new CommandBuilder(sdkManager).addArguments(packages).input('y\n').verbose().execute(30 * 60)
        }
    }

    boolean canExecute() {
        sdkManager.canExecute()
    }

    boolean isInstalled(String packageName, String version) {
        String result = new CommandBuilder(sdkManager).addArguments(['--list']).execute()
        String installedPackages = result.substring(result.indexOf('Installed packages:'),
                result.indexOf('Available Packages:'))

        def lineOfPackage = installedPackages.readLines().find { it.trim().startsWith(packageName + ' ') }
        if (!lineOfPackage) {
            return false
        }

        def columns = lineOfPackage.split('\\|')
        return columns.size() == 4 && columns[1].trim() == version
    }

    String latestVersion(String packageName) {
        String result = new CommandBuilder(sdkManager).addArguments(['--list']).execute()
        String availablePackages = result.substring(result.indexOf('Available Packages:'))

        def lineOfPackage = availablePackages.readLines().find { it.trim().startsWith(packageName + ' ') }
        if (!lineOfPackage) {
            throw new ResourceException("Package [$packageName] is not available via sdkmanager!")
        }

        def columns = lineOfPackage.split('\\|')
        if (columns.size() != 3) {
            throw new ResourceException("The sdkmanager output format changed! Cannot parse line [$lineOfPackage]!")
        }

        columns[1].trim()
    }
}
