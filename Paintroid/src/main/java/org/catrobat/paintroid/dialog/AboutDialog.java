/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.catrobat.paintroid.BuildConfig;
import org.catrobat.paintroid.R;

public class AboutDialog extends AppCompatDialogFragment {

	public static AboutDialog newInstance() {
		return new AboutDialog();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (getShowsDialog()) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		return inflater.inflate(R.layout.dialog_pocketpaint_about, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		TextView aboutVersionNameTextView = view.findViewById(R.id.pocketpaint_dialog_about_version_name_text_view);
		TextView aboutTextView = view.findViewById(R.id.pocketpaint_about_tview_Text);
		TextView aboutUrlTextView = view.findViewById(R.id.pocketpaint_about_tview_Url);

		aboutVersionNameTextView.append(" " + BuildConfig.VERSION_NAME);

		String aboutText = getString(R.string.about_content, getString(R.string.license_type_paintroid));
		aboutTextView.setText(aboutText);

		aboutUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());
		String paintroidLicense = getString(R.string.about_link_template,
				getString(R.string.license_url),
				getString(R.string.about_license_url_text));
		aboutUrlTextView.append(Html.fromHtml(paintroidLicense));
		aboutUrlTextView.append("\n\n");
		String aboutCatroid = getString(R.string.about_link_template,
				getString(R.string.catroid_url),
				getString(R.string.about_catroid_url_text));
		aboutUrlTextView.append(Html.fromHtml(aboutCatroid));
		aboutUrlTextView.append("\n");
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_pocketpaint_about, null);
		onViewCreated(layout, savedInstanceState);

		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setTitle(R.string.about_title)
				.setView(layout)
				.setPositiveButton(R.string.done, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				})
				.create();
	}
}
