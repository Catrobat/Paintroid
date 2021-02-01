/*
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

package org.catrobat.paintroid.test.espresso.util;

import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.tools.Workspace;

import androidx.test.espresso.action.CoordinatesProvider;

import static org.catrobat.paintroid.test.espresso.util.MainActivityHelper.getMainActivityFromView;

public enum BitmapLocationProvider implements CoordinatesProvider{
	MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, .5f);
		}
	},
	MIDDLE_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 1f, .5f);
		}
	},
	OUTSIDE_MIDDLE_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 1.5f, .5f);
		}
	},
	HALFWAY_RIGHT_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .75f, .5f);
		}
	},
	HALFWAY_LEFT_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .25f, .5f);
		}
	},
	HALFWAY_BOTTOM_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, .75f);
		}
	},
	HALFWAY_TOP_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, .25f);
		}
	},
	HALFWAY_TOP_LEFT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .25f, .25f);
		}
	},
	HALFWAY_BOTTOM_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .75f, .75f);
		}
	};

	private static float[] calculatePercentageOffset(View view, float percentageX, float percentageY) {
		MainActivity mainActivity = getMainActivityFromView(view);
		Workspace workspace = mainActivity.workspace;
		float pointX = (workspace.getWidth() - 1) * percentageX;
		float pointY = workspace.getHeight() * percentageY;
		return new float[] {pointX, pointY};
	}
}
