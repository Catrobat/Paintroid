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
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.helper.DefaultNumberRangeFilter
import org.catrobat.paintroid.tools.options.PixelationToolOptionsView.OnPixelationPreviewListener
import org.catrobat.paintroid.tools.options.PixelationToolOptionsView
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

    //private val thisLayer : Chip
    private val currentView = rootView
    private val pixelChangedListener : OnPixelationPreviewListener? = null
    companion object {
        private val TAG = DefaultBrushToolOptionsView::class.java.simpleName
    }

    init {
        val inflater  = LayoutInflater.from(rootView.context)
        val pixelView = inflater.inflate(R.layout.dialog_pocketpaint_pixel, rootView, true)
        pixelView.apply {

        }
        /*initColorText()
        initWidthText()
        initHeightText()*/
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
        TODO("Not yet implemented")
    }
   /* inner class OnPixelChangedColorSeekBarListener : OnSeekBarChangeListener
    {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (progress < MIN_COLOR)
            {
                seekBar.progress = MIN_COLOR
            }
            if(fromUser)
            {
                colorText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar)  = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            colorText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
        }

    }

    inner class OnPixelChangedWidthSeekBarListener : OnSeekBarChangeListener
    {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (progress < MIN_WIDTH)
            {
                seekBar.progress = MIN_WIDTH
            }
            if(fromUser)
            {
                widthText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar)  = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            widthText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
        }

    }
    inner class OnPixelChangedHeightSeekBarListener : OnSeekBarChangeListener
    {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (progress < MIN_HEIGHT)
            {
                seekBar.progress = MIN_HEIGHT
            }
            if(fromUser)
            {
                heightText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar)  = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            heightText.setText(String.format(Locale.getDefault(), "%d", seekBar.progress))
        }

    }*/
}