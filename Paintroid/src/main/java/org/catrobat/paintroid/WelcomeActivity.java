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

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.catrobat.paintroid.intro.IntroPageViewAdapter;
import org.catrobat.paintroid.tools.ToolType;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import static org.catrobat.paintroid.common.MainActivityConstants.RESULT_INTRO_MW_NOT_SUPPORTED;

public class WelcomeActivity extends AppCompatActivity {

	private int colorActive;
	private int colorInactive;
	@VisibleForTesting
	public ViewPager viewPager;
	private LinearLayout dotsLayout;
	@VisibleForTesting
	public int[] layouts;
	private Button btnSkip;
	private Button btnNext;

	ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		int pos;

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
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				final ToolType[] toolTypes = ToolType.values();
				if (layouts[pos] == R.layout.pocketpaint_slide_intro_possibilities) {
					final TextView head = findViewById(R.id.pocketpaint_intro_possibilities_head);
					final TextView description = findViewById(R.id.pocketpaint_intro_possibilities_text);

					LinearLayout topbar = findViewById(R.id.pocketpaint_intro_possibilities_topbar);
					final ImageButton undo = topbar.findViewById(R.id.pocketpaint_btn_top_undo);
					final ImageButton redo = topbar.findViewById(R.id.pocketpaint_btn_top_redo);

					undo.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							head.setText(ToolType.UNDO.getNameResource());
							description.setText(ToolType.UNDO.getHelpTextResource());
						}
					});

					redo.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							head.setText(ToolType.REDO.getNameResource());
							description.setText(ToolType.REDO.getHelpTextResource());
						}
					});

					RelativeLayout relativeLayout = findViewById(R.id.pocketpaint_intro_possibilities_bottom_bar);
					BottomNavigationView navigationView = relativeLayout.findViewById(R.id.pocketpaint_bottom_navigation);

					navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
						@Override
						public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
							head.setText(menuItem.getTitle());

							if (menuItem.getItemId() == R.id.action_tools) {
								description.setText(getResources().getText(R.string.intro_bottom_navigation_tools_description));
							} else if (menuItem.getItemId() == R.id.action_current_tool) {
								description.setText(getResources().getText(R.string.intro_bottom_navigation_current_description));
							} else if (menuItem.getItemId() == R.id.action_color_picker) {
								description.setText(getResources().getText(R.string.intro_bottom_navigation_color_description));
							} else {
								description.setText(getResources().getText(R.string.intro_bottom_navigation_layers_description));
							}

							return false;
						}
					});
				} else if (layouts[pos] == R.layout.pocketpaint_slide_intro_tools_selection) {

					View view = findViewById(R.id.pocketpaint_intro_bottom_bar);
					for (final ToolType type : toolTypes) {
						View toolButton = view.findViewById(type.getToolButtonID());
						if (toolButton == null) {
							continue;
						}

						toolButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								TextView toolName = findViewById(R.id.pocketpaint_textview_intro_tools_header);
								toolName.setText(type.getNameResource());
								TextView toolDescription = findViewById(R.id.pocketpaint_tools_info_description);
								toolDescription.setText(type.getHelpTextResource());
								ImageView icon = findViewById(R.id.pocketpaint_tools_info_icon);
								icon.setImageResource(type.getDrawableResource());
							}
						});
					}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.PocketPaintWelcomeActivityTheme);
		super.onCreate(savedInstanceState);

		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode()) {
			setResult(RESULT_INTRO_MW_NOT_SUPPORTED);
			finish();
			return;
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
				R.layout.pocketpaint_slide_intro_possibilities,
				R.layout.pocketpaint_slide_intro_tools_selection,
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

	public static void reverseArray(int[] array) {
		for (int i = 0; i < array.length / 2; i++) {
			int temp = array[i];
			array[i] = array[array.length - i - 1];
			array[array.length - i - 1] = temp;
		}
	}

	private static boolean defaultLocaleIsRTL() {
		Locale locale = Locale.getDefault();
		if (locale.toString().isEmpty()) {
			return false;
		}
		final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT
				|| directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}

	public static boolean isRTL(Context context) {
		final int layoutDirection = context.getResources().getConfiguration().getLayoutDirection();
		boolean layoutDirectionIsRTL = (layoutDirection == View.LAYOUT_DIRECTION_RTL);
		return layoutDirectionIsRTL || defaultLocaleIsRTL();
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
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(Color.TRANSPARENT);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	int getDotsIndex(int position) {
		if (isRTL(this)) {
			return layouts.length - position - 1;
		}
		return position;
	}
}
