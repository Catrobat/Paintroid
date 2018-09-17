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

package org.catrobat.paintroid.dialog.colorpicker;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;

import java.util.ArrayList;
import java.util.List;

public final class ColorPickerDialog extends AppCompatDialogFragment implements ColorPickerContract.ColorPickerParentFragment {
	private static final String INITIAL_COLOR_KEY = "InitialColor";
	@VisibleForTesting
	public List<OnColorPickedListener> onColorPickedListener = new ArrayList<>();
	private int selectedColor;
	private Button buttonNewColor;
	private final List<ColorPickerContract.ColorPickerFragment> fragments = new ArrayList<>();

	public static ColorPickerDialog newInstance(@ColorInt int initialColor) {
		ColorPickerDialog dialog = new ColorPickerDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(INITIAL_COLOR_KEY, initialColor);
		dialog.setArguments(bundle);
		return dialog;
	}

	@Override
	public void addOnColorPickedListener(OnColorPickedListener listener) {
		onColorPickedListener.add(listener);
	}

	private void notifyListeners(int color) {
		for (OnColorPickedListener listener : onColorPickedListener) {
			listener.colorChanged(color);
		}
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL, R.style.PocketPaintAlertDialog);

		if (savedInstanceState != null) {
			selectedColor = savedInstanceState.getInt(INITIAL_COLOR_KEY, Color.BLACK);
		} else {
			Bundle arguments = getArguments();
			selectedColor = arguments != null ? arguments.getInt(INITIAL_COLOR_KEY, Color.BLACK) : Color.BLACK;
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dialog_color_chooser, container);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		buttonNewColor = view.findViewById(R.id.color_chooser_button_ok);
		buttonNewColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

		ViewPager viewPager = view.findViewById(R.id.color_chooser_view_pager);
		TabLayout tabLayout = view.findViewById(R.id.color_chooser_tab_layout);

		ColorPickerPagerAdapter adapter = new ColorPickerPagerAdapter(getChildFragmentManager());
		adapter.addItem(new ColorPickerFragmentBuilder(R.layout.color_chooser_view_preset),
				R.drawable.ic_color_chooser_tab_preset);
		adapter.addItem(new ColorPickerFragmentBuilder(R.layout.color_chooser_view_hsv),
				R.drawable.ic_color_chooser_tab_hsv);
		adapter.addItem(new ColorPickerFragmentBuilder(R.layout.color_chooser_view_rgba),
				R.drawable.ic_color_chooser_tab_rgba);

		viewPager.setOffscreenPageLimit(adapter.getCount());
		viewPager.setAdapter(adapter);
		tabLayout.setupWithViewPager(viewPager);
		IconPagerAdapterHelper.setupIconsWithTabLayout(adapter, tabLayout);
		setButtonColor(selectedColor);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setTitle(R.string.color_chooser_title);
		return dialog;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt(INITIAL_COLOR_KEY, selectedColor);
	}

	private void setButtonColor(int color) {
		int referenceColor = (Color.red(color) + Color.blue(color) + Color.green(color)) / 3;
		boolean invertContrast = referenceColor <= 128 && Color.alpha(color) > 5;

		if (invertContrast) {
			buttonNewColor.setTextColor(Color.WHITE);
		} else {
			buttonNewColor.setTextColor(Color.BLACK);
		}

		buttonNewColor.setBackground(CustomColorDrawable.newCustomColorDrawable(color));
	}

	@Override
	public void colorChanged(ColorPickerContract.ColorPickerFragment sender, int color) {
		selectedColor = color;
		setButtonColor(color);
		notifyFragments(sender, color);
		notifyListeners(color);
	}

	private void notifyFragments(ColorPickerContract.ColorPickerFragment sender, int color) {
		for (ColorPickerContract.ColorPickerFragment fragment : fragments) {
			if (fragment != sender) {
				fragment.setColor(color);
			}
		}
	}

	@Override
	public void registerColorPickerFragment(ColorPickerContract.ColorPickerFragment fragment) {
		fragments.add(fragment);
	}

	@Override
	public void unregisterColorPickerFragment(ColorPickerContract.ColorPickerFragment fragment) {
		fragments.remove(fragment);
	}

	@Override
	public int getCurrentColor() {
		return selectedColor;
	}

	public interface OnColorPickedListener {
		void colorChanged(int color);
	}

	private static final class ColorPickerFragmentBuilder implements ColorPickerPagerAdapter.FragmentBuilder {
		@LayoutRes
		private int layoutId;

		ColorPickerFragmentBuilder(int layoutId) {
			this.layoutId = layoutId;
		}

		@Override
		public Fragment create() {
			return ColorPickerFragment.newInstance(layoutId);
		}
	}

	static final class CustomColorDrawable extends ColorDrawable {
		private Paint backgroundPaint;

		private CustomColorDrawable(@ColorInt int color) {
			super(color);

			if (Color.alpha(getColor()) != 0xff) {
				Shader backgroundShader = new BitmapShader(
						PaintroidApplication.checkeredBackgroundBitmap, TileMode.REPEAT, TileMode.REPEAT);
				backgroundPaint = new Paint();
				backgroundPaint.setShader(backgroundShader);
			}
		}

		public static Drawable newCustomColorDrawable(@ColorInt int color) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
				return new CustomColorDrawable(color);
			} else {
				return new RippleDrawable(ColorStateList.valueOf(getRippleColor(color)),
						new CustomColorDrawable(color), null);
			}
		}

		public static int getRippleColor(int color) {
			float grayColor = Color.red(color) * 0.299f + Color.green(color) * 0.587f + Color.blue(color) * 0.114f;
			boolean invertContrast = grayColor < 150;
			return invertContrast ? Color.WHITE : Color.GRAY;
		}

		@Override
		public void draw(Canvas canvas) {
			if (backgroundPaint != null) {
				canvas.drawRect(getBounds(), backgroundPaint);
			}
			super.draw(canvas);
		}
	}
}
