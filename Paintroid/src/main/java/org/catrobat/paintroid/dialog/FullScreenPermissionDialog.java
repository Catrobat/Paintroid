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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import org.catrobat.paintroid.R;

public class FullScreenPermissionDialog extends MainActivityDialogFragment {

    public static FullScreenPermissionDialog newInstance() {
        return new FullScreenPermissionDialog();
    }

    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(Bundle savedInsance) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        getPresenter().enterFullscreenClicked();
                        dismiss();
                    case DialogInterface.BUTTON_NEGATIVE:
                        dismiss();
                }
            }
        };

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.pocketpaint_permission_title)
                .setMessage(R.string.permission_info_full_screen_text)
                .setPositiveButton(R.string.pocketpaint_accept, listener)
                .setNegativeButton(R.string.pocketpaint_decline, listener)
                .create();
    }
}