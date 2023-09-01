package org.catrobat.paintroid.ui.tools

import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.Locale

private const val MIN_COLOR = 1
private const val MAX_COLOR = 40

@SuppressLint("NotImplementedDeclaration")
class DefaultPixelToolOptionsView(rootView: ViewGroup) : PixelationToolOptionsView {

    private val pixelNumWidth: AppCompatEditText
    private val pixelNumHeight: AppCompatEditText
    private var colorNumText: AppCompatEditText
    // private val thisLayer : Chip
    // private val currentView = rootView
    private var pixelChangedListener: OnPixelationPreviewListener? = null
    private var pixelNumWidthWatcher: PixelToolNumTextWatcher
    private var pixelNumHeightWatcher: PixelToolNumTextWatcher
    private var colorNumBar: AppCompatSeekBar

    companion object {
        private val TAG = DefaultPixelToolOptionsView::class.java.simpleName
        const val defaulWidth = 40f
        const val defaultHeight = 60f
        const val defaultCollor = 20f
    }

    init {
        val inflater = LayoutInflater.from(rootView.context)
        val pixelView = inflater.inflate(R.layout.dialog_pocketpaint_pixel, rootView, true)
        colorNumBar = pixelView.findViewById(R.id.pocketpaint_pixel_color_seekbar)
        colorNumText = pixelView.findViewById(R.id.pocketpaint_transform_pixel_color_text)
        colorNumBar.progress = defaultCollor.toInt()
        colorNumText.setText(String.format(Locale.getDefault(), "%d", colorNumBar.progress))
        pixelNumWidth = pixelView.findViewById(R.id.pocketpaint_pixel_width_value)
        pixelNumHeight = pixelView.findViewById(R.id.pocketpaint_pixel_height_value)
        pixelNumWidth.setText(defaulWidth.toString())
        pixelNumHeight.setText(defaultHeight.toString())

        pixelNumWidthWatcher = object : PixelToolNumTextWatcher() {
            override fun setValue(value: Float) {
                pixelChangedListener?.setPixelWidth(value)
            }
        }
        pixelNumHeightWatcher = object : PixelToolNumTextWatcher() {
            override fun setValue(value: Float) {
                pixelChangedListener?.setPixelHeight(value)
            }
        }
        pixelNumWidth.addTextChangedListener(pixelNumWidthWatcher)
        pixelNumHeight.addTextChangedListener(pixelNumHeightWatcher)
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
    pixelView.findViewById<View>(R.id.pocketpaint_pixel_apply_button)
        .setOnClickListener {

            pixelChangedListener?.setNumCollor(colorNumBar.progress.toFloat())
        }
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
