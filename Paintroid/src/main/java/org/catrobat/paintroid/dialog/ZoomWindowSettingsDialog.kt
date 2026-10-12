package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.slider.Slider
import org.catrobat.paintroid.R
import org.catrobat.paintroid.UserPreferences

class ZoomWindowSettingsDialog : MainActivityDialogFragment() {

    private val sharedPreferences by lazy {
        UserPreferences(requireActivity().getPreferences(Context.MODE_PRIVATE))
    }

    private var initialEnabledValue = true
    private var initialPercentageValue = UserPreferences.initialZoomPercent

    private var enabled = initialEnabledValue
    private var percentage = initialPercentageValue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialEnabledValue = sharedPreferences.preferenceZoomWindowEnabled
        initialPercentageValue = sharedPreferences.preferenceZoomWindowZoomPercentage
        enabled = initialEnabledValue
        percentage = initialPercentageValue
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val enabledSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_zoom_window_enabled)
        val slider = view.findViewById<Slider>(R.id.pocketpaint_zoom_window_slider)
        val sliderTextView = view.findViewById<TextView>(R.id.pocketpaint_zoom_window_slider_progress)

        enabledSwitch.isChecked = initialEnabledValue
        sliderTextView.text = "$initialPercentageValue%"
        slider.value = initialPercentageValue.toFloat()

        enabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            enabled = isChecked
        }

        slider.addOnChangeListener { _, value, _ ->
            var percentageValue = value.toInt().toString()
            sliderTextView.text = "$percentageValue%"

            percentage = value.toInt()
        }

        slider.setLabelFormatter { value: Float ->
            value.toInt().toString() + '%'
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_zoomwindow_settings, null)
        onViewCreated(layout, savedInstanceState)

        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
                .setTitle(R.string.menu_zoom_settings)
                .setView(layout)
                .setPositiveButton(R.string.pocketpaint_ok) { _, _ ->
                    sharedPreferences.preferenceZoomWindowEnabled = enabled
                    sharedPreferences.preferenceZoomWindowZoomPercentage = percentage
                    dismiss()
                }
                .setNegativeButton(R.string.cancel_button_text) { _, _ ->
                    sharedPreferences.preferenceZoomWindowEnabled = initialEnabledValue
                    sharedPreferences.preferenceZoomWindowZoomPercentage = initialPercentageValue
                    dismiss()
                }
                .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        sharedPreferences.preferenceZoomWindowEnabled = initialEnabledValue
        sharedPreferences.preferenceZoomWindowZoomPercentage = initialPercentageValue
        super.onCancel(dialog)
    }
}
