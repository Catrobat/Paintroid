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
class DependenciesExtension {
    List<String> packages = []
    Closure sdkSettings
    Closure ndkSettings

    void apply(Closure settings) {
        Utils.applySettings(settings, this)
    }

    /**
     * Specify that the Android NDK should be installed.
     * @param settings Settings for the NDK in a closure, see AndroidNdk.
     *                 In general you want to use the most recent NDK, thus do not specify
     *                 a version here unless you have very good reasons.
     */
    void ndk(@DelegatesTo(AndroidNdk) Closure settings) {
        this.ndkSettings = settings
    }

    /**
     * Specifies that the latest Android NDK version should be installed.
     */
    void ndk() {
        ndk({ version = 'latest' })
    }

    /**
     * @param settings for the SDK in a closure, see AndroidSdkTools.
     */
    void sdk(@DelegatesTo(AndroidSdkTools) Closure settings) {
        this.sdkSettings = settings
    }

    /**
     * Specifies that the latest Android SDK Tools version should be installed.
     */
    void sdk() {
        sdk({ version = 'latest' })
    }

    /**
     * @param packages exact name of additional packages to install, as listed by the sdkmanager.
     * @note Do not specify the android emulator image here, use the emulator blocks instead.
     */
    void packages(List<String> packages) {
        this.packages += packages
    }
}
