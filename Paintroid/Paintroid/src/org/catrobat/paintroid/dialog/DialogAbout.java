/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.dialog;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DialogAbout extends BaseDialog implements OnClickListener {
	private Context mContext;

	public DialogAbout(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_about);

		setTitle(R.string.about_title);
		setCancelable(true);

		setCanceledOnTouchOutside(true);

		TextView aboutVersionNameTextView = (TextView) findViewById(R.id.dialog_about_version_name_text_view);
		String versionName = Utils.getVersionName(mContext);
		aboutVersionNameTextView.setText(R.string.about_version);
		aboutVersionNameTextView.append(" " + versionName);

		TextView aboutTextView = (TextView) findViewById(R.id.about_tview_Text);
		String aboutText = String.format(mContext.getString(R.string.about_content),
				mContext.getString(R.string.licence_type_paintroid));
		aboutTextView.setText(aboutText);

		TextView aboutUrlTextView = (TextView) findViewById(R.id.about_tview_Url);
		aboutUrlTextView.setMovementMethod(LinkMovementMethod.getInstance());
		Resources resources = mContext.getResources();
		String paintroidLicence = String.format(resources.getString(R.string.about_link_template),
				resources.getString(R.string.license_url), resources.getString(R.string.about_licence_url_text));
		aboutUrlTextView.append(Html.fromHtml(paintroidLicence));
		aboutUrlTextView.append("\n\n");
		String aboutCatroid = String.format(resources.getString(R.string.about_link_template),
				resources.getString(R.string.catroid_url), resources.getString(R.string.about_catroid_url_text));
		aboutUrlTextView.append(Html.fromHtml(aboutCatroid));
		aboutUrlTextView.append("\n");

		findViewById(R.id.about_btn_Cancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.about_btn_Cancel:
				cancel();
				break;
		}
	}
}
