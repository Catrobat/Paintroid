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

	public static enum ToolButtonIDs {
		BUTTON_ID_PARAMETER_TOP_1, BUTTON_ID_PARAMETER_TOP_2, BUTTON_ID_TOOL, BUTTON_ID_OTHER, BUTTON_ID_PARAMETER_BOTTOM_1, BUTTON_ID_PARAMETER_BOTTOM_2
	}

	private static final int BORDER_SIZE = 1;
	private static final int BORDER_COLOR = Color.GRAY;
	protected Toolbar toolbar;
	protected ToolButtonIDs mButtonNumber;
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

				mButtonNumber = ToolButtonIDs.BUTTON_ID_PARAMETER_TOP_1;
				break;
			case R.id.btn_status_parameter2:
				mButtonNumber = ToolButtonIDs.BUTTON_ID_PARAMETER_TOP_2;
				break;
			case R.id.btn_status_tool:
				mButtonNumber = ToolButtonIDs.BUTTON_ID_TOOL;
				break;
			default:
				mButtonNumber = ToolButtonIDs.BUTTON_ID_OTHER;
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
		currentTool.attributeButtonClick(mButtonNumber);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (!mUsesBackgroundResource) {
			int currentColor = toolbar.getCurrentTool().getAttributeButtonColor(mButtonNumber);
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
		int resource = currentTool.getAttributeButtonResource(mButtonNumber);
		if (resource == R.drawable.icon_menu_no_icon) {
			int color = currentTool.getAttributeButtonColor(mButtonNumber);
			this.setBackgroundColor(color);
			mUsesBackgroundResource = false;
		} else {
			mUsesBackgroundResource = true;
			this.setBackgroundResource(resource);
		}
	}
}
