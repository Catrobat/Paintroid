/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.util;

import android.graphics.PointF;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;

import androidx.test.espresso.action.CoordinatesProvider;

import static org.catrobat.paintroid.test.espresso.util.MainActivityHelper.getMainActivityFromView;
import static org.catrobat.paintroid.test.espresso.util.PositionCoordinatesProvider.calculateViewOffset;

public enum DrawingSurfaceLocationProvider implements CoordinatesProvider {
	MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, .5f);
		}
	},
	LEFT_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .0f, .5f);
		}
	},
	HALFWAY_LEFT_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .25f, .5f);
		}
	},
	RIGHT_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 1f, .5f);
		}
	},
	HALFWAY_RIGHT_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .75f, .5f);
		}
	},
	TOP_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, 0f);
		}
	},
	HALFWAY_TOP_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, .25f);
		}
	},
	HALFWAY_BOTTOM_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, .75f);
		}
	},
	HALFWAY_TOP_LEFT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .25f, .25f);
		}
	},
	HALFWAY_TOP_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .75f, .25f);
		}
	},
	HALFWAY_BOTTOM_LEFT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .25f, .75f);
		}
	},
	HALFWAY_BOTTOM_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .75f, .75f);
		}
	},
	BOTTOM_RIGHT_CLOSE_CENTER {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .55f, .55f);
		}
	},
	BOTTOM_RIGHT_CORNER {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 0.95f, .9f);
		}
	},
	BOTTOM_MIDDLE {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, 0.9f);
		}
	},
	OUTSIDE_MIDDLE_RIGHT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 1.5f, .5f);
		}
	},
	OUTSIDE_MIDDLE_LEFT {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, -.3f, .5f);
		}
	},
	OUTSIDE_MIDDLE_BOTTOM {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, 1.3f, .5f);
		}
	},
	OUTSIDE_MIDDLE_TOP {
		@Override
		public float[] calculateCoordinates(View view) {
			return calculatePercentageOffset(view, .5f, -.3f);
		}
	},
	TOOL_POSITION {
		@Override
		public float[] calculateCoordinates(View view) {
			MainActivity mainActivity = getMainActivityFromView(view);
			Workspace workspace = mainActivity.workspace;
			PointF toolPosition = ((BaseToolWithShape) mainActivity.toolReference.getTool()).toolPosition;
			PointF point = workspace.getSurfacePointFromCanvasPoint(toolPosition);
			return calculateViewOffset(view, point.x, point.y);
		}
	};

	private static float[] calculatePercentageOffset(View view, float percentageX, float percentageY) {
		MainActivity mainActivity = getMainActivityFromView(view);
		Workspace workspace = mainActivity.workspace;
		float pointX = workspace.getWidth() * percentageX;
		float pointY = workspace.getHeight() * percentageY;
		PointF point = workspace.getSurfacePointFromCanvasPoint(new PointF(pointX, pointY));
		return calculateViewOffset(view, point.x, point.y);
	}
}
