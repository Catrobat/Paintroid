package org.catrobat.paintroid.dialog.colorpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HSVSelectorView extends LinearLayout {

	private HSVColorPickerView hsvColorPickerView;

	public HSVSelectorView(Context context) {
		super(context);
		init();
	}

	public HSVSelectorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		hsvColorPickerView = new HSVColorPickerView(getContext());
		hsvColorPickerView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		addView(hsvColorPickerView);
	}
	public HSVColorPickerView getHsvColorPickerView() {
		return hsvColorPickerView;
	}


}
