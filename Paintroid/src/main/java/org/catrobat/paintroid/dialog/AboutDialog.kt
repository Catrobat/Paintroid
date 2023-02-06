/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.text.HtmlCompat
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.databinding.DialogPocketpaintAboutBinding

private lateinit var binding:DialogPocketpaintAboutBinding
class AboutDialog : AppCompatDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        return if (showsDialog) {
            super.onCreateView(inflater, container, savedInstanceState)
        } else {
            inflater.inflate(R.layout.dialog_pocketpaint_about, container, false)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogPocketpaintAboutBinding.bind(view)
        val aboutVersionView = binding.pocketpaintAboutVersion
        val aboutContentView = binding.pocketpaintAboutContent
        val aboutLicenseView = binding.pocketpaintAboutLicenseUrl
        val aboutCatrobatView = binding.pocketpaintAboutCatrobatUrl
        val activity = requireActivity() as MainActivity
        val aboutVersion = getString(R.string.pocketpaint_about_version, activity.getVersionCode())
        aboutVersionView.text = aboutVersion
        val aboutContent = getString(
            R.string.pocketpaint_about_content,
            getString(R.string.pocketpaint_about_license)
        )
        aboutContentView.text = aboutContent
        val licenseUrl = getString(
            R.string.pocketpaint_about_url_license,
            getString(R.string.pocketpaint_about_url_license_description)
        )
        aboutLicenseView.text = HtmlCompat.fromHtml(licenseUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        aboutLicenseView.movementMethod = LinkMovementMethod.getInstance()
        val catrobatUrl = getString(
            R.string.pocketpaint_about_url_catrobat,
            getString(R.string.pocketpaint_about_url_catrobat_description)
        )
        aboutCatrobatView.text = HtmlCompat.fromHtml(catrobatUrl, HtmlCompat.FROM_HTML_MODE_LEGACY)
        aboutCatrobatView.movementMethod = LinkMovementMethod.getInstance()
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val layout = inflater.inflate(R.layout.dialog_pocketpaint_about, null)
        onViewCreated(layout, savedInstanceState)
        return AlertDialog.Builder(requireContext(), R.style.PocketPaintAlertDialog)
            .setTitle(R.string.pocketpaint_about_title)
            .setView(layout)
            .setPositiveButton(R.string.done) { _, _ -> dismiss() }
            .create()
    }
}
