package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import org.catrobat.paintroid.R

class ImportImageDialog : MainActivityDialogFragment() {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val importGallery = view.findViewById<LinearLayout>(R.id.pocketpaint_dialog_import_gallery)
		val importStickers = view.findViewById<LinearLayout>(R.id.pocketpaint_dialog_import_stickers)
		importGallery.setOnClickListener {
			presenter.importFromGalleryClicked()
			dismiss()
		}
		importStickers.setOnClickListener {
			presenter.importStickersClicked()
			dismiss()
		}
	}

	@SuppressLint("InflateParams")
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val inflater = requireActivity().layoutInflater
		val layout = inflater.inflate(R.layout.dialog_pocketpaint_import_image, null)
		onViewCreated(layout, savedInstanceState)
		return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
				.setTitle(R.string.dialog_import_image_title)
				.setView(layout)
				.setNegativeButton(R.string.pocketpaint_cancel) { dialog, which -> dismiss() }
				.create()
	}

	companion object {
		@JvmStatic
		fun newInstance(): ImportImageDialog = ImportImageDialog()
	}
}