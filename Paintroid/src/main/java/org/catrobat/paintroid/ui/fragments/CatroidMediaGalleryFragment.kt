/*
 * Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.ui.fragments

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.MEDIA_GALLEY_URL
import org.catrobat.paintroid.web.MediaGalleryWebViewClient
import org.catrobat.paintroid.web.MediaGalleryWebViewClient.WebClientCallback

class CatroidMediaGalleryFragment : Fragment(), WebClientCallback {
    private var webView: WebView? = null
    private var listener: MediaGalleryListener? = null

    fun setMediaGalleryListener(listener: MediaGalleryListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_pocketpaint_webview, container, false)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)
        webView?.apply {
            settings.javaScriptEnabled = true
            webViewClient = MediaGalleryWebViewClient(this@CatroidMediaGalleryFragment)
            settings.userAgentString = "Catrobat"
            loadUrl(MEDIA_GALLEY_URL)
            setDownloadListener { url, _, _, _, _ ->
                val imageLoader = ImageLoader.getInstance()
                imageLoader.init(ImageLoaderConfiguration.createDefault(activity))
                listener?.showProgressDialog()
                imageLoader.loadImage(
                    url,
                    object : SimpleImageLoadingListener() {
                        override fun onLoadingComplete(
                            imageUri: String,
                            view: View?,
                            loadedImage: Bitmap?
                        ) {
                            if (loadedImage != null) {
                                listener?.bitmapLoadedFromSource(loadedImage)
                            }
                            listener?.dismissProgressDialog()
                        }

                        override fun onLoadingCancelled(imageUri: String, view: View?) {
                            listener?.dismissProgressDialog()
                        }

                        override fun onLoadingFailed(
                            imageUri: String,
                            view: View?,
                            failReason: FailReason
                        ) {
                            listener?.dismissProgressDialog()
                        }
                    }
                )
                finish()
            }
        }
    }

    override fun onDestroy() {
        webView?.setDownloadListener(null)
        webView?.destroy()
        super.onDestroy()
    }

    override fun finish() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    interface MediaGalleryListener {
        fun bitmapLoadedFromSource(loadedBitmap: Bitmap)

        fun showProgressDialog()

        fun dismissProgressDialog()
    }
}
