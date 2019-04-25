package org.catrobat.paintroid.listener;

import org.catrobat.paintroid.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.contract.MainActivityContracts;

public class PresenterColorPickedListener implements ColorPickerDialog.OnColorPickedListener {
	private final MainActivityContracts.Presenter presenter;

	public PresenterColorPickedListener(MainActivityContracts.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void colorChanged(int color) {
		presenter.setTopBarColor(color);
	}
}
