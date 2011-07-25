/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.zoomscroll;

import java.util.Observable;

public class ZoomStatus extends Observable {
	private float zoomLevel;
	private float scrollX;
	private float scrollY;
	private float X;
	private float Y;

	public float getX() {
		return X;
	}

	public float getY() {
		return Y;
	}

	public float getScrollX() {
		return scrollX;
	}

	public float getScrollY() {
		return scrollY;
	}

	public float getZoomLevel() {
		return zoomLevel;
	}

	public void setScrollX(float x) {
		if (scrollX != x) {
			scrollX = x;
			setChanged();
		}
	}

	public void setScrollY(float y) {
		if (scrollY != y) {
			scrollY = y;
			setChanged();
		}
	}

	public void setZoomLevel(float Zoom) {
		if (zoomLevel != Zoom) {
			zoomLevel = Zoom;
			//Limit Zoom Level to max and min
			if (zoomLevel < 1) {
				zoomLevel = 1f;
			}
			if (zoomLevel > 400) {
				zoomLevel = 400;
			}
			setChanged();
		}
	}

	public void setX(float x) {
		if (X != x) {
			X = x;
			setChanged();
		}
	}

	public void setY(float y) {
		if (Y != y) {
			Y = y;
			setChanged();
		}
	}

	//Return the current Zoom-Value (aspect) in x/y Dimensions
	//aspect = (Aspect-ratio Content) / (Aspect-ratio View)
	public float getZoomInX(float aspect) {
		return Math.min(zoomLevel, zoomLevel * aspect);
	}

	public float getZoomInY(float aspect) {
		return Math.min(zoomLevel, zoomLevel / aspect);
	}

	public void resetZoomState() {
		setScrollX(0.5f);
		setScrollY(0.5f);
		setZoomLevel(1f);
		notifyObservers();
		setChanged();
	}
}
