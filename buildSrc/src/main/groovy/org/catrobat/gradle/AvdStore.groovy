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
class AvdStore {
    File avdStore
    File lastUsedAvd

    AvdStore(File avdStore) {
        this.avdStore = avdStore
        this.lastUsedAvd = new File(avdStore, 'last_unique_avd_name.tmp')
    }

    String readAvdName() {
        def checkFile = { File file ->
            if (!file.exists()) {
                throw new NoAvdException("$file does not exist.")
            }
        }

        checkFile(lastUsedAvd)
        def name = lastUsedAvd.text.trim()
        if (name.isEmpty()) {
            throw new NoAvdException('No AVD configured.')
        }

        checkFile(new File(avdStore, "${name}.avd"))
        checkFile(new File(avdStore, "${name}.ini"))

        return name
    }

    String generateAvdName() {
        def uuid = UUID.randomUUID().toString()
        lastUsedAvd.write(uuid)
        return uuid
    }

    IniFile getAvdConfigFile() {
        new IniFile(Utils.joinPaths(avdStore, readAvdName() + '.avd', 'config.ini'))
    }
}
