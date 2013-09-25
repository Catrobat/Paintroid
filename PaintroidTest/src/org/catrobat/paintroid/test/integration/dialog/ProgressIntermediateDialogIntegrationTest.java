/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.integration.dialog;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.dialog.ProgressIntermediateDialog;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.tools.ToolType;

import android.graphics.Color;
import android.graphics.PointF;

public class ProgressIntermediateDialogIntegrationTest extends BaseIntegrationTestClass {

	public ProgressIntermediateDialogIntegrationTest() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	public void testDialogIsNotCancelable() {

		PointF point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2);

		mSolo.clickOnScreen(point.x, point.y);

		selectTool(ToolType.FILL);

		PaintroidApplication.currentTool.changePaintColor(Color.BLUE);

		point = new PointF(mCurrentDrawingSurfaceBitmap.getWidth() / 4, mCurrentDrawingSurfaceBitmap.getHeight() / 4);

		mSolo.clickOnScreen(point.x, point.y);

		assertTrue("Progress Dialog is not showing", ProgressIntermediateDialog.getInstance().isShowing());

		mSolo.clickOnScreen(point.x, point.y);

		assertTrue("Progress Dialog is not showing", ProgressIntermediateDialog.getInstance().isShowing());

	}
}
