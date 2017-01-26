package org.catrobat.paintroid.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * Created by Clemens on 28.12.2016.
 */
public class BottomBarHorizontalScrollView extends HorizontalScrollView {

	private IScrollStateListener scrollStateListener;

	public BottomBarHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BottomBarHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BottomBarHorizontalScrollView(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		prepare();
	}

	private void prepare() {
		if (scrollStateListener != null) {
			View content = this.getChildAt(0);
			if (content.getLeft() >= 0)
				scrollStateListener.onScrollMostLeft();
			if (content.getLeft() < 0)
				scrollStateListener.onScrollFromMostLeft();

			if (content.getRight() <= getWidth())
				scrollStateListener.onScrollMostRight();
			if (content.getLeft() > getWidth())
				scrollStateListener.onScrollFromMostRight();
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollStateListener != null) {
			if (l == 0)
				scrollStateListener.onScrollMostLeft();
			else if (oldl == 0)
				scrollStateListener.onScrollFromMostLeft();
			int mostRightL = this.getChildAt(0).getWidth() - getWidth();
			if (l >= mostRightL)
				scrollStateListener.onScrollMostRight();
			if (oldl >= mostRightL && l < mostRightL)
				scrollStateListener.onScrollFromMostRight();
		}
	}

	public void setScrollStateListener(IScrollStateListener listener) {
		scrollStateListener = listener;
	}

	public interface IScrollStateListener {
		void onScrollMostLeft();

		void onScrollFromMostLeft();

		void onScrollMostRight();

		void onScrollFromMostRight();
	}
}