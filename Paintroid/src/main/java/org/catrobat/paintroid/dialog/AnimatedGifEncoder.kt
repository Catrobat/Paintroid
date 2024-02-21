package org.catrobat.paintroid.dialog



import android.graphics.Bitmap
import android.graphics.Color
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.io.IOException

class AnimatedGifEncoder { 

    private var width: Int = 0
    private var height: Int = 0
    private lateinit var indexedPixels: ByteArray
    private var colorDepth: Int = 0
    private lateinit var colorTab: ByteArray
    private lateinit var usedEntry: BooleanArray
    private var palSize: Int = 0
    private var dispose: Int = -1
    private var closeStream: Boolean = false
    private var firstFrame: Boolean = true
    private var sample: Int = 10
    private var delay: Int = 0
    private lateinit var outputStream: FileOutputStream
    private lateinit var bufferedOutputStream: BufferedOutputStream
    private var transIndex: Int = 0
    private var started: Boolean = false

    fun start(fileOutputStream: FileOutputStream) {
        if (!started) {
            outputStream = fileOutputStream
            bufferedOutputStream = BufferedOutputStream(outputStream)
            writeString("GIF89a") // header
            started = true
        }
    }

    fun setDelay(ms: Int) {
        delay = Math.round(ms / 10.0f)
    }

    fun addFrame(image: Bitmap) {
        if (!started || image == null) return

        try {
            if (!firstFrame) {
                writeByte(0x21)
                writeByte(0xF9)
                writeByte(4)
                writeByte((dispose shl 2) or 0x01)
                writeShort(delay)
                writeByte(transIndex)
                writeByte(0)
            }
            val transparent = -1
            val width = image.width
            val height = image.height
            if (width != this.width || height != this.height) {
                throw IllegalArgumentException("Gif frame dimension mismatch")
            }
            val pixels = IntArray(width * height)
            image.getPixels(pixels, 0, width, 0, 0, width, height)
            if (firstFrame) {
                indexedPixels = ByteArray(width * height)
                usedEntry = BooleanArray(256)
                var nPix = 0
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        val idx = i * width + j
                        val c = pixels[idx]
                        val r = Color.red(c)
                        val g = Color.green(c)
                        val b = Color.blue(c)
                        if (r == 255 && g == 255 && b == 255) {
                            transIndex = idx
                        }
                        if (!usedEntry[c]) {
                            usedEntry[c] = true
                            nPix++
                        }
                        indexedPixels[idx] = c.toByte()
                    }
                }
                palSize = nPix
                colorDepth = (Math.log(palSize.toDouble()) / Math.log(2.0) + 0.5).toInt()
                if (transIndex != -1) {
                    transIndex = findClosest(transIndex)
                }
            }
            // Further processing logic for pixels and palette
            // Write pixels to GIF
            // Write palette to GIF
            // Write extension blocks if not first frame
            firstFrame = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun finish() {
        if (!started) return

        try {
            bufferedOutputStream.write(0x3b) // gif trailer
            bufferedOutputStream.flush()
            if (closeStream) {
                bufferedOutputStream.close()
            }
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        started = false
    }

    private fun writeByte(value: Int) {
        bufferedOutputStream.write(value)
    }

    private fun writeShort(value: Int) {
        bufferedOutputStream.write(value and 0xff)
        bufferedOutputStream.write((value shr 8) and 0xff)
    }

    private fun writeString(string: String) {
        for (i in 0 until string.length) {
            bufferedOutputStream.write(string[i].toInt())
        }
    }

    private fun findClosest(color: Int): Int {
        var minpos = 0
        var dmin = 256 * 256 * 256
        val len = colorTab.size
        for (i in 0 until len step 3) {
            val dr = Color.red(color) - (colorTab[i].toInt() and 0xff)
            val dg = Color.green(color) - (colorTab[i + 1].toInt() and 0xff)
            val db = Color.blue(color) - (colorTab[i + 2].toInt() and 0xff)
            val d = dr * dr + dg * dg + db * db
            val index = i / 3
            if (usedEntry[index] && d < dmin) {
                dmin = d
                minpos = index
            }
        }
        return minpos

    }
}

