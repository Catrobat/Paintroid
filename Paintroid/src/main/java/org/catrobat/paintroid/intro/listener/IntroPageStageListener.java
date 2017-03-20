package org.catrobat.paintroid.intro.listener;

import android.app.Activity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import org.catrobat.paintroid.R;

/**
 * Created by Clemens on 15.03.2017.
 */

public class IntroPageStageListener implements ViewPager.OnPageChangeListener {
    private static final String TAG = "INTRO";
    int pos;
    int state;
    private int[] layouts;
    private Button btnNext;
    private Button btnSkip;
    private boolean firstSequenceStart=true;
    private Activity context;

    @Override
    public void onPageSelected(int position) {
        pos = position;
        addBottomDots(position);
        Log.d(TAG, "select page " + position + " state " + state);

        // changing the next button text 'NEXT' / 'GOT IT'

        if (position == layouts.length - 1) {
            btnNext.setText(R.string.got_it);
            btnSkip.setVisibility(View.GONE);
        } else {
            btnNext.setText(R.string.next);
            btnSkip.setVisibility(View.VISIBLE);
        }

        if (layouts[position] == R.layout.islide_tools) {
            Log.d(TAG, "start tools " + position + " state " + state);
            initBottomBar();
        }

    }

    private void initBottomBar() {

    }

    private void addBottomDots(int position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        pos = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        this.state = state;
        Log.d(TAG, "state " + String.valueOf(state));
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (layouts[pos] == R.layout.islide_possibilities) {
                Log.d(TAG, "start possibilites " + pos);
               /* if (firstSequenceStart) {
                    final View introText = context.findViewById(R.id.intro_possibilities_text);
                    fadeOut(introText);

                    createPossibilitiesSequence().start();
                    firstSequenceStart = false;
                } else {
                    initTopBarTaps();
                }*/

            }
        }

    }

    private void initTopBarTaps() {
    }

    private Animation createPossibilitiesSequence() {
        return null;
    }
}
