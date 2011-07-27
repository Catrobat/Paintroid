package at.tugraz.ist.paintroid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

public class ToolbarButton extends ImageButton {
	Drawable imageNormal;
	Drawable imageActive;

	public ToolbarButton(Context context) {
		super(context);
	}

	public ToolbarButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ToolbarButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		// some code taken from
		// http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/2.3.4_r1/android/view/View.java#2110

		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ToolbarButton);

		final int N = a.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = a.getIndex(i);

			switch (attr) {
				case R.styleable.ToolbarButton_onLongClick:
					if (context.isRestricted()) {
						throw new IllegalStateException("The android:onClick attribute cannot "
								+ "be used within a restricted context");
					}

					final String handlerName = a.getString(attr);
					if (handlerName != null) {
						setOnLongClickListener(new OnLongClickListener() {
							private Method mHandler;

							@Override
							public boolean onLongClick(View v) {
								if (mHandler == null) {
									try {
										mHandler = getContext().getClass().getMethod(handlerName, View.class);
									} catch (NoSuchMethodException e) {
										int id = getId();
										String idText = id == NO_ID ? "" : " with id '"
												+ getContext().getResources().getResourceEntryName(id) + "'";
										throw new IllegalStateException("Could not find a method " + handlerName
												+ "(View) in the activity " + getContext().getClass()
												+ " for onLongClick handler" + " on view "
												+ ToolbarButton.this.getClass() + idText, e);
									}
								}
								try {
									mHandler.invoke(getContext(), ToolbarButton.this);
								} catch (IllegalAccessException e) {
									throw new IllegalStateException("Could not execute non "
											+ "public method of the activity", e);
								} catch (InvocationTargetException e) {
									throw new IllegalStateException("Could not execute " + "method of the activity", e);
								}
								return true;
							}
						});
					}
					break;

				case R.styleable.ToolbarButton_imageNormal:
					imageNormal = a.getDrawable(R.styleable.ToolbarButton_imageNormal);
					setBackgroundDrawable(imageNormal);
					break;

				case R.styleable.ToolbarButton_imageActive:
					imageActive = a.getDrawable(R.styleable.ToolbarButton_imageActive);
					break;
			}
		}
	}

	public void activate() {
		setBackgroundDrawable(imageActive);
	}

	public void deactivate() {
		setBackgroundDrawable(imageNormal);
	}
}
