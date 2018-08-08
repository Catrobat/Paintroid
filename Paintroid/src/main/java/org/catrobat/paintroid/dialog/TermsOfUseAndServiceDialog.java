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

import org.catrobat.paintroid.R;

public class TermsOfUseAndServiceDialog extends AppCompatDialogFragment {

	public static TermsOfUseAndServiceDialog newInstance() {
		return new TermsOfUseAndServiceDialog();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (getShowsDialog()) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		return inflater.inflate(R.layout.dialog_pocketpaint_terms_of_use_and_service, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		TextView termsOfUseAndServiceUrlTextView = view.findViewById(R.id.pocketpaint_terms_of_use_and_service_tview_Url);

		termsOfUseAndServiceUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());
		String paintroidTermsOfUseAndService = getString(R.string.terms_of_use_and_service_link_template,
				getString(R.string.terms_of_use_and_service_url),
				getString(R.string.terms_of_use_and_service_url_text));
		termsOfUseAndServiceUrlTextView.append(Html.fromHtml(paintroidTermsOfUseAndService));
		termsOfUseAndServiceUrlTextView.append("\n\n");
		String aboutCatroid = getString(R.string.about_link_template,
				getString(R.string.catroid_url),
				getString(R.string.about_catroid_url_text));
		termsOfUseAndServiceUrlTextView.append(Html.fromHtml(aboutCatroid));
		termsOfUseAndServiceUrlTextView.append("\n");
	}

	@NonNull
	@Override
	@SuppressLint("InflateParams")
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_pocketpaint_terms_of_use_and_service, null);
		onViewCreated(layout, savedInstanceState);

		return new AlertDialog.Builder(getContext(), R.style.PocketPaintAlertDialog)
				.setTitle(R.string.terms_of_use_and_service_title)
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
