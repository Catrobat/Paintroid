package org.catrobat.paintroid.tools.helper

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import androidx.annotation.VisibleForTesting
import androidx.core.graphics.ColorUtils.RGBToLAB
import kotlin.math.sqrt


class PixelPixelAlgorithm(
    private val inputPix: Bitmap,
    private val color: Int,
    private val width: Int,
    private val height: Int,
) {


    var inputImg =  inputPix

    var outputImg : Bitmap? = inputImg.copy(inputImg.config, true)
    val iterations : Int =  130
    val N =  inputImg.width* inputImg.height
    private val numOfSuperPixel = width* height
    private val aproxSuperPixelSize : Float = (N/numOfSuperPixel).toFloat()
    private val S = sqrt(aproxSuperPixelSize)
    init {

    }
    @VisibleForTesting
    val SuperPixelMat =  Array(width) { x ->
        Array(height) { y ->
            // Just as an example, initialize with (x, y) coordinates and some default l, a, b values.
           SuperPixel(x, y, -10.0, -10.0, -10.0)
        }
    }

    fun run()
    {

    }

    fun createLabBitmap(input : Bitmap)
    {

        for (x in 0 until input.width) {
            for(y in 0 until  input.height) {
                val c: Int = input.getPixel(x,y)
                val  Red = Color.red(c)
                val Green= Color.green(c)
                val Blue = Color.blue(c)
                val outLab = DoubleArray(3)  // This will hold the L*, a*, b* values

                RGBToLAB(Red, Green, Blue, outLab)

            }
    }
    }

    fun innitSuperPixels(input : Bitmap)
    {
        val inputPixelAmountWidthSpan =  (input.width / width).toFloat()
        val inputPixelAmountHeightSpan =  (input.height / height).toFloat()
        var currentXpos = inputPixelAmountWidthSpan / 2
        var currentYpos = inputPixelAmountHeightSpan
        for( i in 0 until width)
        {
            for (j in 0 until  height)
            {
                val c: Int = input.getPixel(currentXpos.toInt(),currentYpos.toInt())
                val  Red = Color.red(c)
                val Green= Color.green(c)
                val Blue = Color.blue(c)
                val outLab = DoubleArray(3)
                RGBToLAB(Red, Green, Blue, outLab)
                SuperPixelMat[i][j] =  SuperPixel(currentXpos.toInt(), currentYpos.toInt(), outLab[0],outLab[1], outLab[2] )
            }
        }
    }
/*

    fun RGB2XYZ(sR: Int, sG: Int, sB: Int) : Triple<Double, Double, Double>{
        val R = sR / 255.0
        val G = sG / 255.0
        val B = sB / 255.0
        val r: Double
        val g: Double
        val b: Double
        r = if (R <= 0.04045) {
            R / 12.92
        } else {
            ((R + 0.055) / 1.055).pow(2.4)
        }
        g = if (G <= 0.04045) {
            G / 12.92
        } else {
            ((G + 0.055) / 1.055).pow(2.4)
        }
        b = if (B <= 0.04045) {
            B / 12.92
        } else {
            ((B + 0.055) / 1.055).pow(2.4)
        }
        val X = r * 0.4124564 + g * 0.3575761 + b * 0.1804375
        val Y = r * 0.2126729 + g * 0.7151522 + b * 0.0721750
        val Z = r * 0.0193339 + g * 0.1191920 + b * 0.9503041
        return Triple(X,Y,Z)
    }

    fun RGB2LAB(sR: Int, sG: Int, sB: Int): Triple<Double,Double,Double> {
        //------------------------
        // sRGB to XYZ conversion
        //------------------------

        val xYZ : Triple<Double, Double,Double> = RGB2XYZ(sR, sG, sB)

        //------------------------
        // XYZ to LAB conversion
        //------------------------
        val epsilon = 0.008856 //actual CIE standard
        val kappa = 903.3 //actual CIE standard
        val Xr = 0.950456 //reference white
        val Yr = 1.0 //reference white
        val Zr = 1.088754 //reference white
        val xr = xYZ.first / Xr
        val yr = xYZ.second / Yr
        val zr = xYZ.third / Zr
        val fx: Double
        val fy: Double
        val fz: Double
        fx = if (xr > epsilon) {
            Math.pow(xr, 1.0 / 3.0)
        } else {
            (kappa * xr + 16.0) / 116.0
        }
        fy = if (yr > epsilon) {
            Math.pow(yr, 1.0 / 3.0)
        } else {
            (kappa * yr + 16.0) / 116.0
        }
        fz = if (zr > epsilon) {
            Math.pow(zr, 1.0 / 3.0)
        } else {
            (kappa * zr + 16.0) / 116.0
        }

        val L  = 116.0 * fx - 16.0
        val A = 500.0 * (fx - fy)
        val B = 200.0 * (fy - fz)
        return Triple(L,A,B)
    }
    fun DoRGBtoLABConversion(Input:Bitmap) {
        val sz: Int = inputImg.width * inputImg.height
        val labVal = DoubleArray(3)
        var i = 0
        var r: Int = 0
        var g: Int = 0
        var b: Int = 0
        for (x in 0 until Input.width) {
            for(y in 0 until  Input.height) {
                val c: Int = Input.getPixel(x,y)
                r += Color.red(c)
                g+= Color.green(c)
                b += Color.blue(c)
            }
        }
            r = ubuff[j] shr 16 and 0xFF
            g = ubuff[j] shr 8 and 0xFF
            b = ubuff[j] and 0xFF
            RGB2LAB(r, g, b, labVal)
            lvec[j] = labVal[0]
            avec[j] = labVal[1]
            bvec[j] = labVal[2]
        }
    }
*/
    fun getMean(input :Bitmap): Triple<Int, Int, Int> {
        var Red :Int = 0
        var Green : Int = 0
        var Blue : Int = 0
        for (x in 0 until input.width) {
           for(y in 0 until  input.height) {
               val c: Int = input.getPixel(x,y)
               Red += Color.red(c)
               Green+= Color.green(c)
               Blue += Color.blue(c)
           }
       }
        val pixelNum = input.width * input.height
       // return Triple((Red.toFloat() / pixelNum), (Green.toFloat() / pixelNum), (Blue.toFloat()/pixelNum))
        return Triple(Math.round(Red.toFloat() / pixelNum), Math.round(Green.toFloat() / pixelNum), Math.round(Blue.toFloat()/pixelNum))
     //   return Triple(floor(Red.toFloat() / pixelNum), floor(Green.toFloat() / pixelNum), floor(Blue.toFloat()/pixelNum))


    }
    fun getOuput(): Bitmap? {

        return outputImg
    }
}