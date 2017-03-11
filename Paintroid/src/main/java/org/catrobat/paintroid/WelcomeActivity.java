package org.catrobat.paintroid;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.DisplayContext;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.BottomBarHorizontalScrollView;

import static android.R.attr.textStyle;

/**
 * Created by Akshay Raj on 7/28/2016.
 * Snow Corporation Inc.
 * www.snowcorp.org
 */
public class WelcomeActivity extends AppCompatActivity {

    final static String TAG = "Intro";
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private Session session;
    private LinearLayout mToolsLayout;
    private BottomBarHorizontalScrollView bottomScrollBar;
    private final int RADIUS_OFFSET = 2;
    private int topBarCircleRadius;
    private int bottomBarCircleRadius;
    private  boolean firstSequnceStart=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Checking for first time launch - before calling setContentView()
        // Flag is 1 when help button in navigation drawer is clicked
        session = new Session(this);
        if (!session.isFirstTimeLaunch() && getIntent().getFlags() != 1) {
            launchHomeScreen();
            finish();
        }
        getIntent().setFlags(0);


        topBarCircleRadius = getDpFromDimension((int) getResources().
                getDimension(R.dimen.top_bar_height)) / 2 - RADIUS_OFFSET;

        bottomBarCircleRadius = getDpFromDimension((int) getResources().
                getDimension(R.dimen.top_bar_height)) / 2 - RADIUS_OFFSET;

        getStyleAttributesFromXml();

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.islide_welcome,
                R.layout.islide_tools,
                R.layout.islide_possibilities,
                R.layout.islide_landscape,
                R.layout.islide_getstarted};

		// adding bottom dots
		addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];


        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(30);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        session.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        int pos;

        @Override
        public void onPageSelected(int position) {
            pos = position;
            addBottomDots(position);
            Log.i(TAG, "onPageSelected " + position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText("Got It");
                btnSkip.setVisibility(View.GONE);
            } else {

                // still pages are left
                btnNext.setText("Next");
                btnSkip.setVisibility(View.VISIBLE);
            }

            if (layouts[position] == R.layout.islide_possibilities) {
                Log.i(TAG, "start possibilites " + position);
                //createPossibilitiesSequence().start();

            } else if (layouts[position] == R.layout.islide_tools) {
                Log.i(TAG, "select tools " + position);
                initBottomBar();
            }

        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            pos = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log.w(TAG, "state " + String.valueOf(state));
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (layouts[pos] == R.layout.islide_possibilities) {
                    Log.w(TAG, "start possibilites " + pos);
                    if(firstSequnceStart) {
                        createPossibilitiesSequence().start();
                        firstSequnceStart = false;
                    }
                }
            }

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            Log.i(TAG, "init " + position);

            if (layouts[position] == R.layout.islide_possibilities) {
                createPossibilitiesSequence();
            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i(TAG, "destroy " + position);
            View view = (View) object;
            container.removeView(view);
        }


    }

    private TapTargetSequence createPossibilitiesSequence() {
        Log.i(TAG, "createPossibilitiesSequence ");

        TapTargetSequence sequence = new TapTargetSequence(this);
        sequence.continueOnCancel(true);
        View topBarView = findViewById(R.id.intro_topbar);


        sequence.targets(
                TapTarget.forView(topBarView.findViewById(R.id.btn_top_undo), "Undo your action", "Insert Text here")
                        .targetRadius(topBarCircleRadius)                  // Specify the target radius (in dp)
                        .titleTextSize(StyleAttributes.HEADER_STYLE.getTextSize())
                        .titleTextColorInt(StyleAttributes.HEADER_STYLE.getTextColor())
                        .descriptionTextColorInt(StyleAttributes.TEXT_STYLE.getTextColor())
                        .descriptionTextSize(StyleAttributes.TEXT_STYLE.getTextSize())
                        .textTypeface(StyleAttributes.TEXT_STYLE.getTypeface())
                , TapTarget.forView(topBarView.findViewById(R.id.btn_top_redo), "Redo ", "Insert Text here")
                        .targetRadius(topBarCircleRadius)                  // Specify the target radius (in dp)
                        .titleTextSize(StyleAttributes.HEADER_STYLE.getTextSize())
                        .titleTextColorInt(StyleAttributes.HEADER_STYLE.getTextColor())
                        .descriptionTextColorInt(StyleAttributes.TEXT_STYLE.getTextColor())
                        .descriptionTextSize(StyleAttributes.TEXT_STYLE.getTextSize())
                        .textTypeface(StyleAttributes.TEXT_STYLE.getTypeface())
                , TapTarget.forView(topBarView.findViewById(R.id.btn_top_color), "Down", "Insert Text here")
                        .targetRadius(topBarCircleRadius)                  // Specify the target radius (in dp)
                        .titleTextSize(StyleAttributes.HEADER_STYLE.getTextSize())
                        .titleTextColorInt(StyleAttributes.HEADER_STYLE.getTextColor())
                        .descriptionTextColorInt(StyleAttributes.TEXT_STYLE.getTextColor())
                        .descriptionTextSize(StyleAttributes.TEXT_STYLE.getTextSize())
                        .textTypeface(StyleAttributes.TEXT_STYLE.getTypeface())
                , TapTarget.forView(topBarView.findViewById(R.id.btn_top_layers), "Uses Layers", "Insert Text here")
                        .targetRadius(topBarCircleRadius)                  // Specify the target radius (in dp)
                        .titleTextSize(StyleAttributes.HEADER_STYLE.getTextSize())
                        .titleTextColorInt(StyleAttributes.HEADER_STYLE.getTextColor())
                        .descriptionTextColorInt(StyleAttributes.TEXT_STYLE.getTextColor())
                        .descriptionTextSize(StyleAttributes.TEXT_STYLE.getTextSize())
                        .textTypeface(StyleAttributes.TEXT_STYLE.getTypeface())


        );

        return sequence;
    }

    private void initBottomBar() {
        Log.i(TAG, "initBottomBar()");

        bottomScrollBar = (BottomBarHorizontalScrollView) findViewById(R.id.bottom_bar_scroll_view);
        mToolsLayout = (LinearLayout) findViewById(R.id.tools_layout);

        setBottomBarListener();
        startBottomBarAnimation();
    }

    private void getStyleAttributesFromXml() {
        for (StyleAttributes text : StyleAttributes.values()) {
            TypedArray attribute = obtainStyledAttributes(text.getResourceId(), R.styleable.IntroAttributes);

            int textSizeDp = (int) attribute.getDimension(R.styleable.IntroAttributes_android_textSize, 16);
            int textStyle = attribute.getInt(R.styleable.IntroAttributes_android_textStyle, 0);
            int color = attribute.getColor(R.styleable.IntroAttributes_android_textColor, Color.WHITE);
            String fontFamilyName = attribute.getString(R.styleable.IntroAttributes_android_fontFamily);
            Typeface typeface = Typeface.create(fontFamilyName, textStyle);

            text.setTextColor(color);
            text.setTextSize(getSpFromDimension(textSizeDp));
            text.setTypeface(typeface);

            attribute.recycle();

        }
    }

    private void startBottomBarAnimation() {
        bottomScrollBar.post(new Runnable() {
            public void run() {
                bottomScrollBar.setScrollX(bottomScrollBar.getChildAt(0).getRight());
                ObjectAnimator.ofInt(bottomScrollBar, "scrollX", 0).setDuration(1000).start();
            }
        });
    }

    private void setBottomBarListener() {
        for (int i = 0; i < mToolsLayout.getChildCount(); i++) {
            View view = mToolsLayout.getChildAt(i);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performToolAction(view);
                }
            });
        }

        setBottomBarScrollerListener();
    }

    private void performToolAction(View view) {
        ToolType toolType = null;

        for (ToolType type : ToolType.values()) {
            if (view.getId() == type.getToolButtonID()) {
                toolType = type;
                break;
            }
        }


        final View introText = findViewById(R.id.intro_tool_text);


        fadeOut(introText);

        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(view, toolType.name(),
                        getResources().getString(toolType.getHelpTextResource()))
                    .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                    .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                    .targetRadius(bottomBarCircleRadius)                  // Specify the target radius (in dp)
                    .titleTextSize(StyleAttributes.HEADER_STYLE.getTextSize())
                    .titleTextColorInt(StyleAttributes.HEADER_STYLE.getTextColor())
                    .descriptionTextColorInt(StyleAttributes.TEXT_STYLE.getTextColor())
                    .descriptionTextSize(StyleAttributes.TEXT_STYLE.getTextSize())
                    .textTypeface(StyleAttributes.TEXT_STYLE.getTypeface())
                , new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        fadeIn(introText);
                    }

                    @Override
                    public void onTargetCancel(TapTargetView view) {
                        super.onTargetCancel(view);
                        fadeIn(introText);
                    }
                });


    }

    private void setBottomBarScrollerListener() {
        final ImageView next = (ImageView) findViewById(R.id.bottom_next);
        final ImageView previous = (ImageView) findViewById(R.id.bottom_previous);
        bottomScrollBar.setScrollStateListener(new BottomBarHorizontalScrollView.IScrollStateListener() {

            public void onScrollMostRight() {
                next.setVisibility(View.GONE);
            }

            public void onScrollMostLeft() {
                previous.setVisibility(View.GONE);
            }

            public void onScrollFromMostLeft() {
                previous.setVisibility(View.VISIBLE);
            }

            public void onScrollFromMostRight() {
                next.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fadeOut(final View view) {
        fadeAnimation(new AlphaAnimation(1, 0), view, View.INVISIBLE);
    }

    private void fadeIn(final View view) {
        fadeAnimation(new AlphaAnimation(0, 1), view, View.VISIBLE);
    }

    private void fadeAnimation(Animation animation, final View view, final int viability) {

        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(500);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(viability);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        view.startAnimation(animation);
    }

    private int getDpFromDimension(int dimension) {
        return (int) (dimension / getResources().getDisplayMetrics().density);
    }

    private int getSpFromDimension(int dimension) {
        return (int) (dimension / getResources().getDisplayMetrics().scaledDensity);
    }

    private enum StyleAttributes {
        HEADER_STYLE(R.style.IntroHeader), TEXT_STYLE(R.style.IntroText);

        private int resourceId;
        private int textColor;
        private int textSize;
        private Typeface typeface;

        StyleAttributes(int resourceId) {
            this.resourceId = resourceId;
        }


        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public int getResourceId() {
            return resourceId;
        }

        public Typeface getTypeface() {
            return typeface;
        }

        public void setTypeface(Typeface typeface) {
            this.typeface = typeface;
        }
    }
}
