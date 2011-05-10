package at.tugraz.ist.paintroid.animation;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class menuSliderAnimation {

	public static Animation noAnimation() {
		Animation noAnimation = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		noAnimation.setDuration(500);
		noAnimation.setInterpolator(new AccelerateInterpolator());
		return noAnimation;
	}
	
	public static Animation showAnimation() {
		Animation showAnimation = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  +1.0f, Animation.RELATIVE_TO_PARENT,   0.0f
		);
		showAnimation.setDuration(500);
		showAnimation.setInterpolator(new AccelerateInterpolator());
		return showAnimation;
	}
	
	public static Animation hideAnimation() {
		Animation hideAnimation = new TranslateAnimation(
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  0.0f,
			Animation.RELATIVE_TO_PARENT,  0.0f, Animation.RELATIVE_TO_PARENT,  +1.0f
		);
		hideAnimation.setDuration(500);
		hideAnimation.setInterpolator(new AccelerateInterpolator());
		return hideAnimation;
	}
}
