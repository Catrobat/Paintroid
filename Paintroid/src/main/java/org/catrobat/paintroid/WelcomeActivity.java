/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid;

import android.content.Intent;
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

			if (layouts[position] == R.layout.islide_tools) {

				View layout = findViewById(R.id.intro_tools_bottom_bar);
				LinearLayout mToolsLayout = (LinearLayout) layout.findViewById(R.id.tools_layout);
				final View fadeView = findViewById(R.id.intro_tools_textview);

				TapTargetBottomBar tapTargetBottomBar = new TapTargetBottomBar(mToolsLayout,
						fadeView, WelcomeActivity.this, R.id.intro_tools_bottom_bar);

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
					&& layouts[pos] == R.layout.islide_possibilities) {
				View layout = findViewById(R.id.intro_possibilites_topbar);
				LinearLayout view = (LinearLayout) layout.findViewById(R.id.top_bar_buttons);
				final View fadeView = findViewById(R.id.intro_possibilities_textview);

				TapTargetTopBar target = new TapTargetTopBar(view, fadeView,
						WelcomeActivity.this, R.id.intro_possibilities_bottom_bar);
				target.initTargetView();
			}
		}
	};
	private Session session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.WelcomeActivityTheme);
		super.onCreate(savedInstanceState);

		session = new Session(this);
		if (!session.isFirstTimeLaunch() && getIntent().getFlags() != Intent.FLAG_GRANT_READ_URI_PERMISSION) {
			launchHomeScreen();
		}
		getIntent().setFlags(0);

		getStyleAttributesFromXml();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
		}

		MultilingualActivity.setToChosenLanguage(this);
		setContentView(R.layout.activity_welcome);

		viewPager = (ViewPager) findViewById(R.id.view_pager);
		dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
		btnSkip = (Button) findViewById(R.id.btn_skip);
		btnNext = (Button) findViewById(R.id.btn_next);

		colorActive = ContextCompat.getColor(getApplicationContext(), R.color.dot_active);
		colorInactive = ContextCompat.getColor(getApplicationContext(), R.color.dot_inactive);

		layouts = new int[]{
				R.layout.islide_welcome,
				R.layout.islide_tools,
				R.layout.islide_possibilities,
				R.layout.islide_landscape,
				R.layout.islide_getstarted};

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
				launchHomeScreen();
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
					launchHomeScreen();
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

	private void launchHomeScreen() {
		session.setFirstTimeLaunch(false);
		Intent mainActivityIntent = new Intent(this, MainActivity.class);
		mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(mainActivityIntent);
		finish();
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
			TypedArray attribute = obtainStyledAttributes(text.getResourceId(), R.styleable.IntroAttributes);

			int textSizeDp = (int) attribute.getDimension(R.styleable.IntroAttributes_android_textSize, 16);
			int textStyle = attribute.getInt(R.styleable.IntroAttributes_android_textStyle, 0);
			int color = attribute.getColor(R.styleable.IntroAttributes_android_textColor, Color.WHITE);
			String fontFamilyName = attribute.getString(R.styleable.IntroAttributes_android_fontFamily);
			Typeface typeface = Typeface.create(fontFamilyName, textStyle);

			text.setTextColor(color);
			text.setTextSize(getSpFromDimension(textSizeDp, metrics));
			text.setTypeface(typeface);

			attribute.recycle();
		}
	}

	@Override
	public void onBackPressed() {
		launchHomeScreen();
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
