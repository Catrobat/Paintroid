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

import android.graphics.Path
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output

open class SerializablePath : Path {

    var serializableActions = ArrayList<SerializableAction>()

    constructor() : super()

    constructor(actions: ArrayList<SerializableAction>) : super() {
        actions.forEach {
            it.perform(this)
        }
    }

    constructor(src: SerializablePath) : super(src) {
        this.serializableActions.addAll(src.serializableActions)
    }

    override fun moveTo(x: Float, y: Float) {
        serializableActions.add(Move(x, y))
        super.moveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        serializableActions.add(Line(x, y))
        super.lineTo(x, y)
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        serializableActions.add(Quad(x1, y1, x2, y2))
        super.quadTo(x1, y1, x2, y2)
    }

    override fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        serializableActions.add(Cube(x1, y1, x2, y2, x3, y3))
        super.cubicTo(x1, y1, x2, y2, x3, y3)
    }

    override fun rewind() {
        serializableActions.clear()
        super.rewind()
    }

    interface SerializableAction {
        fun perform(path: Path)
    }

    class Move(val x: Float, val y: Float) : SerializableAction {
        override fun perform(path: Path) {
            path.moveTo(x, y)
        }
    }

    class Line(val x: Float, val y: Float) : SerializableAction {
        override fun perform(path: Path) {
            path.lineTo(x, y)
        }
    }

    class Quad(val x1: Float, val y1: Float, val x2: Float, val y2: Float) : SerializableAction {
        override fun perform(path: Path) {
            path.quadTo(x1, y1, x2, y2)
        }
    }

    class Cube(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val x3: Float, val y3: Float) : SerializableAction {
        override fun perform(path: Path) {
            path.cubicTo(x1, y1, x2, y2, x3, y3)
        }
    }

    class Rewind : SerializableAction {
        override fun perform(path: Path) {
            path.rewind()
        }
    }

    class PathSerializer(version: Int) : VersionSerializer<SerializablePath>(version) {
        override fun write(kryo: Kryo, output: Output, path: SerializablePath) {
            output.writeInt(path.serializableActions.size)
            path.serializableActions.forEach { action ->
                kryo.writeClassAndObject(output, action)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out SerializablePath>): SerializablePath =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out SerializablePath>): SerializablePath {
            val size = input.readInt()
            val actionList = ArrayList<SerializableAction>()
            repeat(size) {
                actionList.add(kryo.readClassAndObject(input) as SerializableAction)
            }
            return SerializablePath(actionList)
        }
    }

    class PathActionMoveSerializer(version: Int) : VersionSerializer<Move>(version) {
        override fun write(kryo: Kryo, output: Output, action: Move) {
            with(output) {
                writeFloat(action.x)
                writeFloat(action.y)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Move>): Move =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Move>): Move {
            return with(input) {
                Move(readFloat(), readFloat())
            }
        }
    }

    class PathActionLineSerializer(version: Int) : VersionSerializer<Line>(version) {
        override fun write(kryo: Kryo, output: Output, action: Line) {
            with(output) {
                writeFloat(action.x)
                writeFloat(action.y)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Line>): Line =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Line>): Line {
            return with(input) {
                Line(readFloat(), readFloat())
            }
        }
    }

    class PathActionQuadSerializer(version: Int) : VersionSerializer<Quad>(version) {
        override fun write(kryo: Kryo, output: Output, action: Quad) {
            with(output) {
                writeFloat(action.x1)
                writeFloat(action.y1)
                writeFloat(action.x2)
                writeFloat(action.y2)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Quad>): Quad =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Quad>): Quad {
            return with(input) {
                Quad(readFloat(), readFloat(), readFloat(), readFloat())
            }
        }
    }

    class PathActionCubeSerializer(version: Int) : VersionSerializer<Cube>(version) {
        override fun write(kryo: Kryo, output: Output, action: Cube) {
            with(output) {
                writeFloat(action.x1)
                writeFloat(action.y1)
                writeFloat(action.x2)
                writeFloat(action.y2)
                writeFloat(action.x3)
                writeFloat(action.y3)
            }
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Cube>): Cube =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Cube>) =
            with(input) {
                Cube(readFloat(), readFloat(), readFloat(), readFloat(), readFloat(), readFloat())
            }
    }

    class PathActionRewindSerializer(version: Int) : VersionSerializer<Rewind>(version) {
        override fun write(kryo: Kryo, output: Output, action: Rewind) {
            // Has no member variables to save
        }

        override fun read(kryo: Kryo, input: Input, type: Class<out Rewind>): Rewind =
            super.handleVersions(this, kryo, input, type)

        override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out Rewind>): Rewind =
            Rewind()
    }
}
