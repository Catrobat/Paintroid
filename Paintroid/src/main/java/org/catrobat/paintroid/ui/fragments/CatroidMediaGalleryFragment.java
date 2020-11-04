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
package org.catrobat.paintroid.ui.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.web.MediaGalleryWebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static org.catrobat.paintroid.common.Constants.MEDIA_GALLEY_URL;

public class CatroidMediaGalleryFragment extends Fragment implements MediaGalleryWebViewClient.WebClientCallback {
	private WebView webView;
	private MediaGalleryListener listener;

	public interface MediaGalleryListener {
		void bitmapLoadedFromSource(Bitmap loadedBitmap);

		void showProgressDialog();

		void dissmissProgressDialog();
	}

	public void setMediaGalleryListener(MediaGalleryListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_pocketpaint_webview, container, false);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		webView = view.findViewById(R.id.webview);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new MediaGalleryWebViewClient(this));
		webView.getSettings().setUserAgentString("Catrobat");
		webView.loadUrl(MEDIA_GALLEY_URL);

		webView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
				ImageLoader imageLoader = ImageLoader.getInstance();
				imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
				listener.showProgressDialog();
				imageLoader.loadImage(url, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						if (loadedImage != null) {
							listener.bitmapLoadedFromSource(loadedImage);
						}
						listener.dissmissProgressDialog();
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						listener.dissmissProgressDialog();
					}

					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						listener.dissmissProgressDialog();
					}
				});
				finish();
			}
		});
	}

	@Override
	public void onDestroy() {
		webView.setDownloadListener(null);
		webView.destroy();
		super.onDestroy();
	}

	@Override
	public void finish() {
		getActivity().getSupportFragmentManager().popBackStack();
	}
}
