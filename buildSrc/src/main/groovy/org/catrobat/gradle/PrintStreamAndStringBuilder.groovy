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
class PrintStreamAndStringBuilder extends OutputStream {
    PrintStream os
    ByteArrayOutputStream stringBuffer

    PrintStreamAndStringBuilder(PrintStream os) {
        this.os = os
        this.stringBuffer = new ByteArrayOutputStream()
    }


    @Override
    void write(int i) throws IOException {
        os.write(i)
        stringBuffer.write(i)
    }

    @Override
    void write(byte[] var1) throws IOException {
        os.write(var1, 0, var1.length);
        stringBuffer.write(var1, 0, var1.length);
    }

    @Override
    void write(byte[] var1, int var2, int var3) throws IOException {
        os.write(var1, var2, var3)
        stringBuffer.write(var1, var2, var3)
    }

    String toString() {
        stringBuffer.toString()
    }
}