/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.command.serialization

import android.graphics.Paint
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.command.implementation.LineToolCommand
import org.catrobat.paintroid.command.implementation.PathCommand

class LineToolCommandSerializer(version: Int) : VersionSerializer<LineToolCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: LineToolCommand) {
        val pathSerializer = SerializablePath.PathSerializer(version);
//        output.writeInt(command.pathCommands.size)
//        command.pathCommands.forEach {
//            pathSerializer.write(kryo, output, it)
//        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out LineToolCommand>): LineToolCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out LineToolCommand>): LineToolCommand {
        val size = input.readInt()
        val pathList = ArrayList<SerializablePath>()
        repeat(size) {
            val sizeActions = input.readInt()
            val actionList = ArrayList<SerializablePath.SerializableAction>()
            repeat(sizeActions) {
                actionList.add(kryo.readClassAndObject(input) as SerializablePath.SerializableAction)
            }
            pathList.add(SerializablePath(actionList))
        }
        return LineToolCommand(pathList)
    }
}
