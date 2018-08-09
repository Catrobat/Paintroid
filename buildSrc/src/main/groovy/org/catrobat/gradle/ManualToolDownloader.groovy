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
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.resources.ResourceException

/**
 * Class that handles manually downloading potentially outdated Android tools like the SDK and the NDK.
 *
 * sdkmanager often only supports the most recent version of a tool.
 * As a result if an older version is needed the correct zip files for the used operating
 * system have to be downloaded manually.
 * All this is handled by ManualToolDownloader.
 *
 * For usage subclass for your own tool and add all the versions you support.
 */
@TypeChecked
abstract class ManualToolDownloader {
    File androidSdk
    SdkManager sdkManager
    String baseUrl = 'https://dl.google.com/android/repository'
    URL url
    String sha256sum
    String version
    Map<String, String> versionLookup = [:]
    Map<String, Closure<Void>> versions = [:]

    ManualToolDownloader(File androidSdk) {
        this.androidSdk = androidSdk
        this.sdkManager = new SdkManager(this.androidSdk)
    }

    void apply(Closure settings) {
        Utils.applySettings(settings, this)
    }

    void linux(String name, String sha256sum) {
        shouldSetUrl(Os.FAMILY_UNIX, name, sha256sum)
    }

    void mac(String name, String sha256sum) {
        shouldSetUrl(Os.FAMILY_MAC, name, sha256sum)
    }

    void windows(String name, String sha256sum) {
        shouldSetUrl(Os.FAMILY_WINDOWS, name, sha256sum)
    }

    void addVersion(String version, Closure<Void> closure, List<String> aliases = []) {
        versions[version] = closure
        aliases.each { String alias ->
            versionLookup[alias] = version
        }
    }

    void setVersion(String version) {
        version = versionLookup[version] ?: version
        def settings = versions[version]
        if (!settings) {
            throw new ToolException("${packageDescription()} version [$version] is not supported " +
                    "(supported: ${versions.keySet() + versionLookup.keySet()}, manually add the version you want!")
        }

        this.version = version
        apply(settings)
    }

    /**
     * Performs an installation if the tool was not correctly installed yet.
     */
    void install() {
        try {
            checkInstalled()
            println("${packageDescription()} installed already.")
        } catch (ToolException) {
            forceInstall()
        }
    }

    abstract String packageDescription()

    /**
     * Throws a ToolException when the tool is not installed.
     */
    abstract protected void checkInstalled()

    abstract protected void forceInstall()

    protected void download(File destination) {
        println("Downloading $url to $destination ...")
        destination.withOutputStream { out ->
            url.withInputStream { from ->
                out << from
            }
        }
        println("Downloaded $url")

        println("Checking checksum ...")
        if (sha256sum && Utils.checksum(destination, 'SHA-256') != sha256sum) {
            throw new ResourceException("Faulty checksum for $destination!")
        }
    }

    /**
     * @param destination The destination to extract the downloaded zip file to. Will be cleared before.
     */
    protected void downloadAndExtract(File parentDirectory, String directoryName) {
        if (!url) {
            throw new ResourceException("No url provided to download. Use all of the linux/max/windows functions!")
        }

        File destination = new File(parentDirectory, directoryName)
        println("Removing $destination if it exists.")
        if (!destination.deleteDir()) {
            throw new ResourceException("Could not delete [$destination]")
        }

        def temp = File.createTempFile('zipDownloader', null)
        download(temp)

        parentDirectory.mkdirs()
        Unzipper.unzip(temp, parentDirectory)
        temp.delete()
    }

    protected void throwOnFailure(boolean success, String message) {
        if (!success) {
            throw new ToolException(message)
        }
    }

    private void shouldSetUrl(String osFamily, String name, String sha256sum) {
        if (Os.isFamily(osFamily)) {
            url = new URL(baseUrl + "/" + name)
            this.sha256sum = sha256sum
        }
    }
}
