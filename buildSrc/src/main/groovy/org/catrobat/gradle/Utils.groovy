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
import org.apache.tools.ant.taskdefs.condition.Os

@TypeChecked
class Utils {
    static boolean isRunningOnJenkins() {
        'JENKINS_URL' in System.getenv()
    }

    static File joinPaths(File file, String... paths) {
        paths.each{ String path ->
            file = new File(file, path)
        }
        return file
    }

    static File executable(File exe, String winEnding) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return new File(exe.absolutePath + winEnding)
        } else {
            return exe
        }
    }

    static void applySettings(Closure settings, Object target) {
        settings = (Closure) settings.clone()
        settings.delegate = target
        settings.resolveStrategy = Closure.DELEGATE_FIRST
        settings()
    }

    static String checksum(File file, String type) {
        def digest = java.security.MessageDigest.getInstance("SHA-256")
        file.eachByte(4096) { buffer, length ->
            digest.update(buffer, 0, length)
        }
        digest.digest().encodeHex() as String
    }

    static Map asMap(Object object) {
        Map properties = object.properties

        properties.remove('class')
        properties.remove('declaringClass')
        properties.remove('metaClass')

        properties
    }
}
