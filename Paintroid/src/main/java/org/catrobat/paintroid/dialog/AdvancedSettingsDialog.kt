package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.smoothing
import org.catrobat.paintroid.tools.helper.AdvancedSettingsAlgorithms.useEventSize
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint.Companion.antialiasing

class AdvancedSettingsDialog : MainActivityDialogFragment() {
    private var initValueAntialiasing: Boolean = antialiasing
    private var initValueSmoothing: Boolean = smoothing
    private var initValueUseEventSize: Boolean = useEventSize

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val antialiasingSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_antialiasing)
        val smoothSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_smoothing)
        val useEventSizeSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_eventsize)

        antialiasingSwitch.isChecked = antialiasing
        smoothSwitch.isChecked = smoothing
        useEventSizeSwitch.isChecked = useEventSize

        antialiasingSwitch.setOnCheckedChangeListener { _, isChecked ->
            antialiasing = isChecked
        }

        smoothSwitch?.setOnCheckedChangeListener { _, isChecked ->
            smoothing = isChecked
        }

        useEventSizeSwitch?.setOnCheckedChangeListener { _, isChecked ->
            useEventSize = isChecked
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_advanced_settings, null)
        onViewCreated(layout, savedInstanceState)

        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.menu_advanced)
            .setView(layout)
            .setPositiveButton(R.string.pocketpaint_ok) { _, _ ->
                presenter.setAntialiasingOnOkClicked()
                dismiss()
            }
            .setNegativeButton(R.string.cancel_button_text) { _, _ ->
                antialiasing = initValueAntialiasing
                smoothing = initValueSmoothing
                useEventSize = initValueUseEventSize
                dismiss()
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        antialiasing = initValueAntialiasing
        smoothing = initValueSmoothing
        useEventSize = initValueUseEventSize
        super.onCancel(dialog)
    }
}
