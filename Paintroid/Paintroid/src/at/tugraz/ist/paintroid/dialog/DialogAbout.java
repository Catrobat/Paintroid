/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;

public class DialogAbout extends AlertDialog implements OnClickListener {
	private WebView mWebView;
	private static final String LICENSE_URL = "http://www.catroid.org/catroid/licenseofsystem";

	public DialogAbout(Context context) {
		super(context);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_about);

		setTitle(R.string.about_title);
		setCancelable(true);

		Button button = (Button) findViewById(R.id.about_btn_Cancel);
		button.setOnClickListener(this);

		button = (Button) findViewById(R.id.about_btn_License);
		button.setOnClickListener(this);

		mWebView = (WebView) findViewById(R.id.about_wview_license);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebViewClient(new SimpleWebViewClient());

		mWebView.loadUrl(LICENSE_URL);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.about_btn_License:
				// DialogLicense licenseDialog = new DialogLicense(this.getContext());
				// licenseDialog.show();
				mWebView.loadUrl(LICENSE_URL);
				break;
			case R.id.about_btn_Cancel:
				// close dialog
				this.cancel();
				break;
		}
	}

	private class SimpleWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			Log.d(PaintroidApplication.TAG, "ERROR: No Internet Connection code:" + errorCode + " " + description + " "
					+ failingUrl);
		}
	}
}
