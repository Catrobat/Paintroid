/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DialogTermsOfUseAndService extends DialogFragment implements OnClickListener {

	public DialogTermsOfUseAndService() {
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		AlertDialog.Builder builder = new CustomAlertDialogBuilder(
				getActivity());

		builder.setTitle(R.string.terms_of_use_and_service_title);
		View view = inflater.inflate(R.layout.dialog_terms_of_use_and_service, null);

		TextView termsOfUseAndServiceTextView = (TextView) view
				.findViewById(R.id.terms_of_use_and_service_tview_Text);
		termsOfUseAndServiceTextView.setText(R.string.terms_of_use_and_service_content);

		TextView termsOfUseAndServiceUrlTextView = (TextView) view
				.findViewById(R.id.terms_of_use_and_service_tview_Url);
		termsOfUseAndServiceUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());
		Resources resources = getActivity().getResources();
		String paintroidTermsOfUseAndService = String.format(
				resources.getString(R.string.terms_of_use_and_service_link_template),
				resources.getString(R.string.terms_of_use_and_service_url),
				resources.getString(R.string.terms_of_use_and_service_url_text));
		termsOfUseAndServiceUrlTextView.append(Html.fromHtml(paintroidTermsOfUseAndService));
		termsOfUseAndServiceUrlTextView.append("\n\n");
		String aboutCatroid = String.format(
				resources.getString(R.string.about_link_template),
				resources.getString(R.string.catroid_url),
				resources.getString(R.string.about_catroid_url_text));
		termsOfUseAndServiceUrlTextView.append(Html.fromHtml(aboutCatroid));
		termsOfUseAndServiceUrlTextView.append("\n");

		builder.setView(view);
		builder.setNeutralButton(R.string.done, this);

		return builder.create();

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case AlertDialog.BUTTON_NEUTRAL:
			dismiss();
			break;
		}

	}
}
