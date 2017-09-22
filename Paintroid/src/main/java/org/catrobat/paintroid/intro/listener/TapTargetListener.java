package org.catrobat.paintroid.intro.listener;

import android.view.View;

import com.getkeepsafe.taptargetview.TapTargetView;

import static org.catrobat.paintroid.intro.helper.IntroAnimation.fadeIn;

/**
 * Created by Clemens on 15.03.2017.
 */

public class TapTargetListener extends TapTargetView.Listener {

    private View fadeView;

    public TapTargetListener(View fadeView) {
        this.fadeView = fadeView;
    }

    @Override
    public void onTargetClick(TapTargetView view) {
        super.onTargetClick(view);      // This call is optional
        fadeIn(fadeView);
    }

    @Override
    public void onTargetCancel(TapTargetView view) {
        super.onTargetCancel(view);
        fadeIn(fadeView);
    }

    @Override
    public void onOuterCircleClick(TapTargetView view) {
        onTargetClick(view);
    }
}
