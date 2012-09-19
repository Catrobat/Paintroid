/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.tools.implementation;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.catrobat.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

public class StampTool extends BaseToolWithRectangleShape {

	private static final boolean ROTATION_ENABLED = true;
	private static final boolean RESPECT_BORDER = false;

	public StampTool(Context context, ToolType toolType) {
		super(context, toolType, ROTATION_ENABLED, RESPECT_BORDER);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getAttributeButtonColor(ToolButtonIDs buttonNumber) {
		switch (buttonNumber) {
		case BUTTON_ID_PARAMETER_TOP_1:
		case BUTTON_ID_PARAMETER_TOP_2:
			return Color.TRANSPARENT;
		default:
			return super.getAttributeButtonColor(buttonNumber);
		}
	}

	@Override
	public void attributeButtonClick(ToolButtonIDs buttonNumber) {
		// no clicks wanted
	}

	@Override
	protected void createAndSetBitmap(DrawingSurface drawingSurface) {
		Log.d(PaintroidApplication.TAG, "clip bitmap");
		Point left_top_box_bitmapcoordinates = new Point((int) mToolPosition.x
				- (int) mBoxWidth / 2, (int) mToolPosition.y - (int) mBoxHeight
				/ 2);
		Point right_bottom_box_bitmapcoordinates = new Point(
				(int) mToolPosition.x + (int) mBoxWidth / 2,
				(int) mToolPosition.y + (int) mBoxHeight / 2);
		try {
			mDrawingBitmap = Bitmap.createBitmap(drawingSurface.getBitmap(),
					left_top_box_bitmapcoordinates.x,
					left_top_box_bitmapcoordinates.y,
					right_bottom_box_bitmapcoordinates.x
							- left_top_box_bitmapcoordinates.x,
					right_bottom_box_bitmapcoordinates.y
							- left_top_box_bitmapcoordinates.y);
			Log.d(PaintroidApplication.TAG, "created bitmap");
		} catch (IllegalArgumentException e) {
			// floatingBox is outside of image
			Log.e(PaintroidApplication.TAG,
					"error clip bitmap " + e.getMessage());
			Log.e(PaintroidApplication.TAG, "left top box coord : "
					+ left_top_box_bitmapcoordinates.toString());
			Log.e(PaintroidApplication.TAG, "right bottom box coord : "
					+ right_bottom_box_bitmapcoordinates.toString());
			Log.e(PaintroidApplication.TAG,
					"drawing surface bitmap size : "
							+ drawingSurface.getBitmapHeight() + " x "
							+ drawingSurface.getBitmapWidth());

			if (mDrawingBitmap != null) {
				mDrawingBitmap.recycle();
				mDrawingBitmap = null;
			}
		}
	}

	@Override
	protected void onClickInBox() {
		if (mDrawingBitmap == null) {
			createAndSetBitmap(PaintroidApplication.DRAWING_SURFACE);
		} else {
			Point intPosition = new Point((int) mToolPosition.x,
					(int) mToolPosition.y);
			Command command = new StampCommand(mDrawingBitmap, intPosition,
					mBoxWidth, mBoxHeight, mBoxRotation);
			((StampCommand) command).addObserver(this);
			mProgressDialog.show();
			PaintroidApplication.COMMAND_MANAGER.commitCommand(command);
		}
		setMoveBorder(true);
	}
}
