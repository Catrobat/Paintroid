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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
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

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private Session session;
    private WelcomeActivity activity;
    int colorActive;
    int colorInactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        session = new Session(this);
       if (!session.isFirstTimeLaunch() && getIntent().getFlags() != Intent.FLAG_GRANT_READ_URI_PERMISSION) {
            launchHomeScreen();
            finish();
        }
        getIntent().setFlags(0);

        getStyleAttributesFromXml();

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);
        activity = this;

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


        if (isRTL()) {
            addBottomDots(layouts.length-1);
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

                if (isRTL()) {
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
        if (isRTL(getApplicationContext())) {
            reverseArray(layouts);
        }

        viewPager.setAdapter(new IntroPageViewAdapter(getBaseContext(), layouts));
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        if (isRTL(getApplicationContext())) {
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
            dots[i].setText(fromHtml("&#8226;"));
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
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        int pos;
        int state;

        @Override
        public void onPageSelected(int position) {
            pos = position;
            addBottomDots(position);

            if (getDotsIndex(position) == layouts.length - 1) {
                btnNext.setText(R.string.got_it);
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText(R.string.next);
                btnSkip.setVisibility(View.VISIBLE);
            }

            if (layouts[position] == R.layout.islide_tools) {

                LinearLayout layout = (LinearLayout) findViewById(R.id.intro_tools_bottom_bar);
                LinearLayout mToolsLayout = (LinearLayout) layout.findViewById(R.id.tools_layout);
                final View fadeView = findViewById(R.id.intro_tools_textview);

                TapTargetBottomBar tapTargetBottomBar = new TapTargetBottomBar(mToolsLayout,
                        fadeView, activity, R.id.intro_tools_bottom_bar);

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
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (layouts[pos] == R.layout.islide_possibilities) {
                    View layout = findViewById(R.id.intro_possibilites_topbar);
                    LinearLayout view = (LinearLayout) layout.findViewById(R.id.layout_top_bar);
                    final View fadeView = findViewById(R.id.intro_possibilities_textview);

                    TapTargetTopBar target = new TapTargetTopBar(view, fadeView, activity,
                            R.id.intro_possibilities_bottom_bar);
                    target.initTargetView();
                }
            }
        }
    };


    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void getStyleAttributesFromXml() {
        for (TapTargetStyle text : TapTargetStyle.values()) {
            TypedArray attribute = obtainStyledAttributes(text.getResourceId(), R.styleable.IntroAttributes);

            int textSizeDp = (int) attribute.getDimension(R.styleable.IntroAttributes_android_textSize, 16);
            int textStyle = attribute.getInt(R.styleable.IntroAttributes_android_textStyle, 0);
            int color = attribute.getColor(R.styleable.IntroAttributes_android_textColor, Color.WHITE);
            String fontFamilyName = attribute.getString(R.styleable.IntroAttributes_android_fontFamily);
            Typeface typeface = Typeface.create(fontFamilyName, textStyle);

            text.setTextColor(color);
            text.setTextSize(getSpFromDimension(textSizeDp, getBaseContext()));
            text.setTypeface(typeface);

            attribute.recycle();

        }
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
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
        if (isRTL(getApplicationContext())) {
            return layouts.length - position - 1;
        }
        return position;
    }

}
