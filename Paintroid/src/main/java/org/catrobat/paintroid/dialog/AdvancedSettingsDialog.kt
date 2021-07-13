package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import org.catrobat.paintroid.R
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint.antialiasing

class AdvancedSettingsDialog : MainActivityDialogFragment() {
    private var initValue: Boolean = antialiasing

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val antialiasingSwitch = view.findViewById<SwitchCompat>(R.id.pocketpaint_antialiasing)

        antialiasingSwitch.isChecked = antialiasing

        antialiasingSwitch?.setOnCheckedChangeListener { _, isChecked ->
            antialiasing = isChecked
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
                antialiasing = initValue
                dismiss()
            }
            .create()
    }

    override fun onCancel(dialog: DialogInterface) {
        antialiasing = initValue
        super.onCancel(dialog)
    }
}
