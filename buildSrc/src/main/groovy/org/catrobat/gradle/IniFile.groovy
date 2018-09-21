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
class IniFile {
    File iniFile

    IniFile(File iniFile) {
        this.iniFile = iniFile
    }

    /**
     * Adds values to the ini files.
     *
     * Existing elements are updated in the order they are found.
     * Remaining elements are added in alphabetical order.
     */
    void updateValues(Map values) {
        def remainingSorted = new TreeMap(values)

        // first update existing elements without changing their order
        def contents = iniFile.text.readLines().collect { String line ->
            def parts = line.split('=')
            if (parts.size() == 2) {
                def k = parts[0].trim()
                if (k in values) {
                    line = "$k=${values[k]}"
                    remainingSorted.remove(k)
                }
            }

            line
        }

        // now add the remaining elements in alphabetical order
        contents += remainingSorted.collect { k, v ->
            "$k=$v".toString()
        }

        iniFile.write(contents.join('\n'))
    }
}
