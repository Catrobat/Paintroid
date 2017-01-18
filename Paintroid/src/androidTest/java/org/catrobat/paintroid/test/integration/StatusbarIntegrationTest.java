/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.test.integration;

import java.util.ArrayList;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.TopBar;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class StatusbarIntegrationTest extends BaseIntegrationTestClass {

	private static final String PRIVATE_ACCESS_STATUSBAR_NAME = "mTopBar";


	public StatusbarIntegrationTest() throws Exception {
		super();
	}

	public void testAllButtonsAreVisible() {
		ArrayList<Integer> expectedButtons = new ArrayList<Integer>();
		expectedButtons.add(R.id.btn_top_undo);
		expectedButtons.add(R.id.btn_top_redo);
		expectedButtons.add(R.id.btn_top_color);
		expectedButtons.add(R.id.btn_top_layers);

		ArrayList<ImageButton> imageButtons = mSolo.getCurrentViews(ImageButton.class);
		for (ImageButton button : imageButtons) {
			expectedButtons.remove((Object) button.getId());
		}

		assertEquals("all buttons should be found", 0, expectedButtons.size());
	}

}
