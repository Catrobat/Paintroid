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

package org.catrobat.paintroid.ui;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.contract.MainActivityContracts;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;

import static org.catrobat.paintroid.common.Constants.COLOR_PICKER_DIALOG_TAG;

public class MainActivityNavigator implements MainActivityContracts.Navigator {
	private MainActivity mainActivity;

	public MainActivityNavigator(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void showColorPickerDialog() {
		ColorPickerDialog dialog = ColorPickerDialog.newInstance(PaintroidApplication.currentTool.getDrawPaint().getColor());
		dialog.addOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
			@Override
			public void colorChanged(int color) {
				PaintroidApplication.currentTool.changePaintColor(color);
			}
		});
		dialog.addOnColorPickedListener(mainActivity.topBar);
		dialog.show(mainActivity.getSupportFragmentManager(), COLOR_PICKER_DIALOG_TAG);
	}
}
