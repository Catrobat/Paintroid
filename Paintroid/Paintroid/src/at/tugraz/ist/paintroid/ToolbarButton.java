/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *   
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *   
 *   You should have received a copy of the GNU Affero General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class ToolbarButton extends ImageButton {
	static final String TAG = "PAINTROID";

	int imageNormal;
	int imageActive;

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
		// reflection code taken from http://goo.gl/SG2V4
		TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.ToolbarButton);

		final int N = styledAttributes.getIndexCount();
		for (int i = 0; i < N; i++) {
			int attr = styledAttributes.getIndex(i);
			switch (attr) {

				case R.styleable.ToolbarButton_onLongClick:
					if (context.isRestricted()) {
						throw new IllegalStateException("The paintroid:onLongClick attribute cannot "
								+ "be used within a restricted context");
					}

					final String handlerName = styledAttributes.getString(attr);
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
					imageNormal = styledAttributes.getResourceId(R.styleable.ToolbarButton_imageNormal, 0);
					Log.d(TAG, Integer.toString(imageNormal));
					setBackgroundResource(imageNormal);
					break;

				case R.styleable.ToolbarButton_imageActive:
					imageActive = styledAttributes.getResourceId(R.styleable.ToolbarButton_imageActive, 0);
					break;
			}
		}
	}

	public void activate() {
		if (imageActive != 0) {
			setBackgroundResource(imageActive);
		} else {
			Log.e(TAG, "ERROR: imageActive is 0. ", new NullPointerException());
		}
	}

	public void deactivate() {
		if (imageNormal != 0) {
			setBackgroundResource(imageNormal);
		} else {
			Log.e(TAG, "ERROR: imageNormal is 0. ", new NullPointerException());
		}
	}
}
