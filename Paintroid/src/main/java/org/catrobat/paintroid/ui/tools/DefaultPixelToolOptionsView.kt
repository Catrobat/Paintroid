package org.catrobat.paintroid.ui.tools

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSeekBar
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.options.PixelationToolOptionsView.OnPixelationPreviewListener
import org.catrobat.paintroid.tools.options.PixelationToolOptionsView
import java.lang.NumberFormatException
import java.text.NumberFormat
import java.text.ParseException
import java.util.*

@VisibleForTesting
private const val MIN_WIDTH = 1
private const val MAX_WIDTH = 100
private const val MIN_HEIGHT = 1
private const val MAX_HEIGHT = 200
private const val MIN_COLOR = 1
private const val MAX_COLOR = 30

// ask PO maybe needed the minumum but the bar is not scalable so far (API 21 is current?)

class DefaultPixelToolOptionsView (rootView : ViewGroup): PixelationToolOptionsView{
    /*private val widthLayout : View
    private val heightLayout : View
    private val topLayout : View
    private val colorLayout : View
    private val bottomLayout : View
    private var widthText : EditText
    private var heightText : EditText
    private var colorText : EditText
    private var colorSeekBar : SeekBar
    private var widthSeekBar : SeekBar
    private var heightSeekBar : SeekBar*/

    private val pixelNumWidth : AppCompatEditText
    private val pixelNumHeight : AppCompatEditText
    private var colorNumText : AppCompatEditText
    //private val thisLayer : Chip
    private val currentView = rootView
    private var pixelChangedListener : OnPixelationPreviewListener? = null
    private var pixelNumWidthWatcher : PixelToolNumTextWatcher
    private var pixelNumHeightWatcher : PixelToolNumTextWatcher
    private var colorNumBar : AppCompatSeekBar

    private var callback : PixelationToolOptionsView.OnPixelationPreviewListener?=  null

    companion object {
        private val TAG = DefaultPixelToolOptionsView::class.java.simpleName
    }



    init {
        val inflater  = LayoutInflater.from(rootView.context)
        val pixelView = inflater.inflate(R.layout.dialog_pocketpaint_pixel, rootView, true)
        colorNumBar = pixelView.findViewById(R.id.pocketpaint_pixel_color_seekbar)
        colorNumText = pixelView.findViewById(R.id.pocketpaint_transform_pixel_color_text)
        /*initColorText()
        initWidthText()
        initHeightText()*/
        pixelNumWidth =pixelView.findViewById(R.id.pocketpaint_pixel_width_value)
        pixelNumHeight = pixelView.findViewById(R.id.pocketpaint_pixel_height_value)
        pixelNumWidthWatcher = object : PixelToolNumTextWatcher()
        {
            override fun setValue(value: Float) {
                callback?.setPixelWidth(value)
            }
        }
        pixelNumHeightWatcher = object : PixelToolNumTextWatcher()
        {
            override fun setValue(value: Float) {
                callback?.setPixelHeight(value)
            }
        }
        pixelNumWidth.addTextChangedListener(pixelNumWidthWatcher)
        pixelNumHeight.addTextChangedListener(pixelNumHeightWatcher)
      //  pixelNumHeight.setText(MAX_HEIGHT)
      //  pixelNumWidth.setText(MAX_WIDTH)
        colorNumText.filters = arrayOf<InputFilter>(DefaultNumberRangeFilter(MIN_COLOR, MAX_COLOR))
        colorNumText.setText(String.format(Locale.getDefault(), "%d", colorNumBar.progress))
        colorNumText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) =
                Unit

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) =
                Unit

            override fun afterTextChanged(editable: Editable) {
                val percentageTextString = colorNumText.text.toString()
                val percentageTextInt: Int = try {
                    percentageTextString.toInt()
                } catch (exp: NumberFormatException) {
                    exp.localizedMessage?.let {
                        Log.d(TAG, it)
                    }
                    MIN_COLOR
                }
                colorNumBar.progress = percentageTextInt
                colorNumText.setSelection(colorNumText.length())
            }
        })
        colorNumBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress == 0) {
                    return
                }
                colorNumText.setText(String.format(Locale.getDefault(), "%d", progress))
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                colorNumText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
            }
        })
    }
    // handle up probs error

    override fun invalidate() {
        TODO("Not yet implemented")
    }

    override fun getTopToolOptions(): View {
        TODO("Not yet implemented")
    }  //topLayout

    override fun getBottomToolOptions(): View {
        TODO("Not yet implemented")
    }

    override fun setPixelPreviewListener(onPixelationPreviewListener: PixelationToolOptionsView.OnPixelationPreviewListener) {
        this.pixelChangedListener = onPixelationPreviewListener
    }

    abstract class PixelToolNumTextWatcher : TextWatcher {

        protected abstract fun setValue(value: Float)

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
        override fun afterTextChanged(editable: Editable) {
            var str = editable.toString()
            if (str.isEmpty()) {
                str = MAX_COLOR.toString()

            }
            try {
                val value = NumberFormat.getIntegerInstance().parse(str)?.toFloat()
                value?.let { setValue(it) }
            } catch (e: ParseException) {
                e.message?.let { Log.e(TAG, it) }
            }
        }
    }

}