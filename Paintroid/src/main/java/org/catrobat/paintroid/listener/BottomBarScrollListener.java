package org.catrobat.paintroid.listener;

import android.view.View;

import org.catrobat.paintroid.ui.BottomBarHorizontalScrollView;

/**
 * Created by Clemens on 21.02.2017.
 */

public class BottomBarScrollListener implements BottomBarHorizontalScrollView.IScrollStateListener {
    private View next;
    private View previous;

    public BottomBarScrollListener(View previous, View next) {
        this.next = next;
        this.previous = previous;
    }


    public void onScrollMostLeft() {
        next.setVisibility(View.GONE);
    }

    public void onScrollFromMostLeft() {
        previous.setVisibility(View.GONE);
    }

    public void onScrollMostRight() { previous.setVisibility(View.VISIBLE); }

    public void onScrollFromMostRight() {
        next.setVisibility(View.VISIBLE);
    }
}
