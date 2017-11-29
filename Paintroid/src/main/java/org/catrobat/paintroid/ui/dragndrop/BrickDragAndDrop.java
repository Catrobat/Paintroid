/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.dragndrop;

import android.view.View;

public abstract class BrickDragAndDrop {

	public abstract void moveOrMerge(View v, float x, float y);

	public abstract void showOptionFromCurrentPosition(float x, float y);

	public abstract void dragEnded();

	public abstract void resetMoveAlreadyAnimated();

	public abstract void setupProperties();

	public abstract void goOutsideView(boolean isOutside, float x, float y);
}
