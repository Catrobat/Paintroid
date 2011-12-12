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
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.ui;

import android.graphics.Point;

public interface Perspective {

	/**
	 * Apply a scale to the DrawingSurface's Canvas.
	 * 
	 * @param scale The amount to scale [1.0...*]
	 */
	public void scale(float scale);

	/**
	 * Performs a translation on the DrawingSurface's Canvas. The change is additive.
	 * 
	 * @param dy Translation-offset in x.
	 * @param dy Translation-offset in y.
	 */
	public void translate(float dx, float dy);

	/**
	 * Translates screen-coordinates to coordinates on the SurfaceHolder's Canvas.
	 * 
	 * @param coords Screen-coordinates that will be translated.
	 */
	public void translateScreenToCanvas(Point coords);
}
