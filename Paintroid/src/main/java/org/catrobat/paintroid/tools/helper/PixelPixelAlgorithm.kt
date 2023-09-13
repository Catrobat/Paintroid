package org.catrobat.paintroid.tools.helper

import android.R.attr
import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.floor


class PixelPixelAlgorithm(
    private val inputPix: Bitmap?,
    private val color: Int,
    private val width: Int,
    private val height: Int,
) {


    var inputImg =  inputPix

    var outputImg : Bitmap? = inputImg?.copy(inputImg?.config, true)
    val iterations : Int =  130

    init {

     /*   for (y in 0 until inputImg!!.height)
        {
            for(x in 0 until inputImg!!.width / 2){
                //outputImg?.setPixel(x,y, Color.GREEN)
            }
        }*/
     }

    fun run()
    {

    }

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