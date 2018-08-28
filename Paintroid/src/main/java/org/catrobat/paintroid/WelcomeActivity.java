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
package org.catrobat.paintroid;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.paintroid.intro.IntroPageViewAdapter;
import org.catrobat.paintroid.intro.TapTargetBottomBar;
import org.catrobat.paintroid.intro.TapTargetStyle;
import org.catrobat.paintroid.intro.TapTargetTopBar;

import static org.catrobat.paintroid.intro.helper.WelcomeActivityHelper.getSpFromDimension;
import static org.catrobat.paintroid.intro.helper.WelcomeActivityHelper.isRTL;
import static org.catrobat.paintroid.intro.helper.WelcomeActivityHelper.reverseArray;

public class WelcomeActivity extends AppCompatActivity {

	@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
	public int colorActive;
	@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
	public int colorInactive;
	@VisibleForTesting
	public ViewPager viewPager;
	private LinearLayout dotsLayout;
	@VisibleForTesting
	public int[] layouts;
	private Button btnSkip;
	private Button btnNext;
	ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
		int pos;
		int state;

		@Override
		public void onPageSelected(int position) {
			pos = position;
			addBottomDots(position);

			if (getDotsIndex(position) == layouts.length - 1) {
				btnNext.setText(R.string.lets_go);
				btnSkip.setVisibility(View.GONE);
			} else {
				btnNext.setText(R.string.next);
				btnSkip.setVisibility(View.VISIBLE);
			}

			if (layouts[position] == R.layout.pocketpaint_slide_intro_tools) {

				View layout = findViewById(R.id.pocketpaint_intro_tools_bottom_bar);
				LinearLayout mToolsLayout = layout.findViewById(R.id.pocketpaint_tools_layout);
				final View fadeView = findViewById(R.id.pocketpaint_intro_tools_textview);

				TapTargetBottomBar tapTargetBottomBar = new TapTargetBottomBar(mToolsLayout,
						fadeView, WelcomeActivity.this, R.id.pocketpaint_intro_tools_bottom_bar);

				tapTargetBottomBar.initTargetView();
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			pos = position;
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			this.state = state;
			if (state == ViewPager.SCROLL_STATE_IDLE
					&& layouts[pos] == R.layout.pocketpaint_slide_intro_possibilities) {
				View layout = findViewById(R.id.pocketpaint_intro_possibilites_topbar);
				LinearLayout view = layout.findViewById(R.id.pocketpaint_top_bar_buttons);
				final View fadeView = findViewById(R.id.pocketpaint_intro_possibilities_textview);

				TapTargetTopBar target = new TapTargetTopBar(view, fadeView,
						WelcomeActivity.this, R.id.pocketpaint_intro_possibilities_bottom_bar);
				target.initTargetView();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.PocketPaintWelcomeActivityTheme);
		super.onCreate(savedInstanceState);

		getStyleAttributesFromXml();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}

		setContentView(R.layout.activity_pocketpaint_welcome);

		viewPager = findViewById(R.id.pocketpaint_view_pager);
		dotsLayout = findViewById(R.id.pocketpaint_layout_dots);
		btnSkip = findViewById(R.id.pocketpaint_btn_skip);
		btnNext = findViewById(R.id.pocketpaint_btn_next);

		colorActive = ContextCompat.getColor(this, R.color.pocketpaint_welcome_dot_active);
		colorInactive = ContextCompat.getColor(this, R.color.pocketpaint_welcome_dot_inactive);

		layouts = new int[]{
				R.layout.pocketpaint_slide_intro_welcome,
				R.layout.pocketpaint_slide_intro_tools,
				R.layout.pocketpaint_slide_intro_possibilities,
				R.layout.pocketpaint_slide_intro_landscape,
				R.layout.pocketpaint_slide_intro_getstarted};

		changeStatusBarColor();
		initViewPager();

		if (isRTL(this)) {
			addBottomDots(layouts.length - 1);
		} else {
			addBottomDots(0);
		}

		btnSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean finished;
				int current = getItem(+1);

				finished = current > layouts.length - 1;

				if (isRTL(WelcomeActivity.this)) {
					current = getItem(-1);
					finished = current < 0;
				}

				if (finished) {
					finish();
				} else {
					viewPager.setCurrentItem(current);
				}
			}
		});
	}

	private void initViewPager() {
		if (isRTL(this)) {
			reverseArray(layouts);
		}

		viewPager.setAdapter(new IntroPageViewAdapter(layouts));
		viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

		if (isRTL(this)) {
			int pos = layouts.length;
			viewPager.setCurrentItem(pos);
		}
	}

	private void addBottomDots(int currentPage) {
		TextView[] dots = new TextView[layouts.length];
		int currentIndex = getDotsIndex(currentPage);

		dotsLayout.removeAllViews();
		for (int i = 0; i < dots.length; i++) {
			dots[i] = new TextView(this);
			dots[i].setText("•");
			dots[i].setTextSize(30);
			dots[i].setTextColor(colorInactive);
			dotsLayout.addView(dots[i]);
		}

		if (dots.length > 0) {
			dots[currentIndex].setTextColor(colorActive);
		}
	}

	private int getItem(int i) {
		return viewPager.getCurrentItem() + i;
	}

	private void changeStatusBarColor() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
	}

	private void getStyleAttributesFromXml() {
		final DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
		for (TapTargetStyle text : TapTargetStyle.values()) {
			TypedArray attribute = obtainStyledAttributes(text.getResourceId(), R.styleable.PocketPaintWelcomeAttributes);

			int textSizeDp = (int) attribute.getDimension(R.styleable.PocketPaintWelcomeAttributes_android_textSize, 16);
			int textStyle = attribute.getInt(R.styleable.PocketPaintWelcomeAttributes_android_textStyle, 0);
			int color = attribute.getColor(R.styleable.PocketPaintWelcomeAttributes_android_textColor, Color.WHITE);
			String fontFamilyName = attribute.getString(R.styleable.PocketPaintWelcomeAttributes_android_fontFamily);
			Typeface typeface = Typeface.create(fontFamilyName, textStyle);

			text.setTextColor(color);
			text.setTextSize(getSpFromDimension(textSizeDp, metrics));
			text.setTypeface(typeface);

			attribute.recycle();
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		TapTargetTopBar.resetSequenceState();
	}

	int getDotsIndex(int position) {
		if (isRTL(this)) {
			return layouts.length - position - 1;
		}
		return position;
	}
}
