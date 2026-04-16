package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.app.Dialog
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
    private lateinit var sharedPreferences: UserPreferences

    private var initialEnabledValue = false
    private var initialPercentageValue = 0

    private var enabled = false
    private var percentage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = UserPreferences(requireActivity().getPreferences(Context.MODE_PRIVATE))

        initialEnabledValue = savedInstanceState?.getBoolean(INITIAL_ENABLED_KEY)
            ?: sharedPreferences.preferenceZoomWindowEnabled
        initialPercentageValue = savedInstanceState?.getInt(INITIAL_PERCENTAGE_KEY)
            ?: sharedPreferences.preferenceZoomWindowZoomPercentage

        enabled = savedInstanceState?.getBoolean(ENABLED_KEY) ?: initialEnabledValue
        percentage = savedInstanceState?.getInt(PERCENTAGE_KEY) ?: initialPercentageValue
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val enabledSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_zoom_window_enabled)
        val slider = view.findViewById<Slider>(R.id.pocketpaint_zoom_window_slider)
        val sliderTextView = view.findViewById<TextView>(R.id.pocketpaint_zoom_window_slider_progress)

        enabledSwitch.isChecked = enabled
        sliderTextView.text = "$percentage%"
        slider.value = percentage.toFloat()

        enabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            enabled = isChecked
        }

        slider.addOnChangeListener { _, value, _ ->
            percentage = value.toInt()
            sliderTextView.text = "$percentage%"
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(INITIAL_ENABLED_KEY, initialEnabledValue)
        outState.putInt(INITIAL_PERCENTAGE_KEY, initialPercentageValue)
        outState.putBoolean(ENABLED_KEY, enabled)
        outState.putInt(PERCENTAGE_KEY, percentage)
    }

    override fun onCancel(dialog: DialogInterface) {
        sharedPreferences.preferenceZoomWindowEnabled = initialEnabledValue
        sharedPreferences.preferenceZoomWindowZoomPercentage = initialPercentageValue
        super.onCancel(dialog)
    }

    companion object {
        private const val INITIAL_ENABLED_KEY = "initialEnabledKey"
        private const val INITIAL_PERCENTAGE_KEY = "initialPercentageKey"
        private const val ENABLED_KEY = "enabledKey"
        private const val PERCENTAGE_KEY = "percentageKey"
    }
}
