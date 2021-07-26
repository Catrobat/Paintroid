/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.web;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.catrobat.paintroid.R;

public class MediaGalleryWebViewClient extends WebViewClient {
	private ProgressDialog webViewLoadingDialog;
	private WebClientCallback callback;

	public interface WebClientCallback {
		void finish();
	}

	public MediaGalleryWebViewClient(WebClientCallback callback) {
		super();
		this.callback = callback;
	}

	@Override
	public void onPageStarted(WebView view, String urlClient, Bitmap favicon) {
		if (webViewLoadingDialog == null && !urlClient.matches("https://share.catrob.at/pocketcode/")) {
			webViewLoadingDialog = new ProgressDialog(view.getContext(), R.style.WebViewLoadingCircle);
			webViewLoadingDialog.setCancelable(true);
			webViewLoadingDialog.setCanceledOnTouchOutside(false);
			webViewLoadingDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
			webViewLoadingDialog.show();
		} else {
			callback.finish();
		}
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		if (webViewLoadingDialog != null) {
			webViewLoadingDialog.dismiss();
			webViewLoadingDialog = null;
		}
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	}

	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		callback.finish();
	}
}
