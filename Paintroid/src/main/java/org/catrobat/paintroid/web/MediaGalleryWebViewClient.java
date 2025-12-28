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

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;

public class MediaGalleryWebViewClient extends WebViewClient {
	private AlertDialog loadingDialog;
	private final WebClientCallback callback;

	public interface WebClientCallback {
		void finish();
	}

	public MediaGalleryWebViewClient(WebClientCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		showLoading(view);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		dismissLoading();
	}

	// New API (M+) — preferred
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
		// Return false to let the WebView load the URL
		return false;
	}

	// Legacy shim for < M
	@SuppressWarnings("deprecation")
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		return false;
	}

	// New API (M+) — preferred
	@Override
	public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
		dismissLoading();
		callback.finish();
	}

	// Legacy shim for < M
	@SuppressWarnings("deprecation")
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		dismissLoading();
		callback.finish();
	}

	private void showLoading(View anchor) {
		if (loadingDialog != null) {
			return;
		}

		ProgressBar progress = new ProgressBar(anchor.getContext());
		// make it large enough and accessible
		int padding = (int) (anchor.getResources().getDisplayMetrics().density * 24);
		progress.setPadding(padding, padding, padding, padding);
		ViewCompat.setImportantForAccessibility(progress, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

		loadingDialog = new AlertDialog.Builder(anchor.getContext())
				.setView(progress)
				.setCancelable(true)
				.create();
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.show();
	}

	private void dismissLoading() {
		if (loadingDialog != null) {
			loadingDialog.dismiss();
			loadingDialog = null;
		}
	}
}
