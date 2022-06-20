package org.catrobat.paintroid.command.serialization

import android.graphics.Bitmap
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.command.implementation.ClippingCommand

class ClippingCommandSerializer(version: Int) : VersionSerializer<ClippingCommand>(version) {
    override fun write(kryo: Kryo, output: Output, command: ClippingCommand) {
        with(kryo) {
            writeObject(output, command.bitmap)
            writeObject(output, command.pathBitmap)
        }
    }

    override fun read(kryo: Kryo, input: Input, type: Class<out ClippingCommand>): ClippingCommand =
        super.handleVersions(this, kryo, input, type)

    override fun readCurrentVersion(kryo: Kryo, input: Input, type: Class<out ClippingCommand>): ClippingCommand {
        return with(kryo) {
            val bitmap = readObject(input, Bitmap::class.java)
            val pathBitmap = readObject(input, Bitmap::class.java)
            ClippingCommand(bitmap, pathBitmap)
        }
    }
}
