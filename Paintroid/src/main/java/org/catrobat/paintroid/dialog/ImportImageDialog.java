package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.catrobat.paintroid.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class ImportImageDialog extends MainActivityDialogFragment {

	public static ImportImageDialog newInstance() {
		return new ImportImageDialog();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		LinearLayout importGallery = view.findViewById(R.id.pocketpaint_dialog_import_gallery);
		LinearLayout importStickers = view.findViewById(R.id.pocketpaint_dialog_import_stickers);

		importGallery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getPresenter().importFromGalleryClicked();
				dismiss();
			}
		});

		importStickers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				getPresenter().importStickersClicked();
				dismiss();
			}
		});
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_pocketpaint_import_image, null);
		onViewCreated(layout, savedInstanceState);

		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setTitle(R.string.dialog_import_image_title)
				.setView(layout)
				.setNegativeButton(R.string.pocketpaint_cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
				.create();
	}
}
