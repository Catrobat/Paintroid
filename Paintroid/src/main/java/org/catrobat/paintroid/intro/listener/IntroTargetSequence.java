package org.catrobat.paintroid.intro.listener;

import android.util.Log;
import android.view.View;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import static org.catrobat.paintroid.intro.helper.IntroAnimationHelper.fadeIn;

/**
 * Created by Clemens on 16.03.2017.
 */

public class IntroTargetSequence implements TapTargetSequence.Listener {


    private static final String TAG = "Sequence Listener";
    private View fadeView;

    public IntroTargetSequence(View fadeView) {
        this.fadeView = fadeView;
    }

    @Override
    public void onSequenceFinish() {
        Log.d(TAG, "Possibilities Sequence Finished");
        fadeIn(fadeView);
    }

    @Override
    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

    }


    @Override
    public void onSequenceCanceled(TapTarget lastTarget) {

    }

}
