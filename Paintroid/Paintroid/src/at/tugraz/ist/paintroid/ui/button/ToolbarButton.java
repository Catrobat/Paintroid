/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.ui.button;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.ui.Toolbar;

public class ToolbarButton extends TextView implements OnClickListener, OnLongClickListener, Observer {

	private static final int BORDER_SIZE = 1;
	private static final int BORDER_COLOR = Color.GRAY;
	protected Toolbar toolbar;
	protected int buttonNumber;
	private boolean mUsesBackgroundResource = true;

	public ToolbarButton(Context context) {
		super(context);
		init(context);
	}

	public ToolbarButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ToolbarButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	protected void init(Context context) {
		this.setOnClickListener(this);
		this.setOnLongClickListener(this);
		switch (this.getId()) {
			case R.id.btn_status_parameter1:
				buttonNumber = 0;
				break;
			case R.id.btn_status_parameter2:
				buttonNumber = 1;
				break;
			case R.id.btn_status_tool:
				buttonNumber = 2;
				break;
			default:
				buttonNumber = -1;
				break;
		}
	}

	public void setToolbar(Toolbar toolbar) {
		this.toolbar = toolbar;
		((Observable) toolbar).addObserver(this);
		update((Observable) toolbar, null);
	}

	@Override
	public boolean onLongClick(View view) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(final View view) {
		final Tool currentTool = toolbar.getCurrentTool();
		currentTool.attributeButtonClick(buttonNumber);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (!mUsesBackgroundResource) {
			int currentColor = toolbar.getCurrentTool().getAttributeButtonColor(buttonNumber);
			if (currentColor == Color.TRANSPARENT) {
				return;
			}
			Rect rectangle = new Rect(0, 0, getWidth(), getHeight());
			Paint paint = new Paint();
			paint.setColor(BORDER_COLOR);
			canvas.drawRect(rectangle, paint);
			Rect smallerRectangle = new Rect(BORDER_SIZE, BORDER_SIZE, getWidth() - BORDER_SIZE, getHeight()
					- BORDER_SIZE);

			paint.setColor(currentColor);
			canvas.drawRect(smallerRectangle, paint);
		}
	}

	@Override
	public void update(Observable observable, Object argument) {
		if (observable instanceof Toolbar) {
			Observable tool = (Observable) toolbar.getCurrentTool();
			tool.addObserver(this);
		}
		final Tool currentTool = toolbar.getCurrentTool();
		int resource = currentTool.getAttributeButtonResource(buttonNumber);
		if (resource == 0) {
			int color = currentTool.getAttributeButtonColor(buttonNumber);
			this.setBackgroundColor(color);
			mUsesBackgroundResource = false;
		} else {
			mUsesBackgroundResource = true;
			this.setBackgroundResource(resource);
		}
	}
}
