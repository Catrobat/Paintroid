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

package at.tugraz.ist.paintroid.deprecated.graphic.utilities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Cap;
import at.tugraz.ist.paintroid.deprecated.graphic.DrawingSurface;

@Deprecated
public abstract class Tool {

	protected ToolState state;

	protected Point position;
	protected Point startPosition;
	protected Point zoomedPosition;

	public enum ToolState {
		INACTIVE, ACTIVE, DRAW;
	}

	protected Point surfaceSize;

	protected Paint linePaint;

	protected final int primaryColor = Color.BLACK;

	protected final int secundaryColor = Color.YELLOW;

	protected final int toolStrokeWidth = 5;

	protected int distanceFromScreenEdgeToScroll;

	protected final int scrollSpeed = 20;

	public Tool(Tool tool) {
		initialize();
		this.surfaceSize = tool.surfaceSize;
		this.position.x = this.surfaceSize.x / 2;
		this.position.y = this.surfaceSize.y / 2;
		this.zoomedPosition.x = Math.round(position.x / DrawingSurface.Perspective.zoom);
		this.zoomedPosition.y = Math.round(position.y / DrawingSurface.Perspective.zoom);
		this.distanceFromScreenEdgeToScroll = (int) (this.surfaceSize.x * 0.1);
		setStartPosition();
	}

	public Tool() {
		initialize();
	}

	private void initialize() {
		this.position = new Point(0, 0);
		this.zoomedPosition = new Point(0, 0);
		this.startPosition = new Point(0, 0);
		this.state = ToolState.INACTIVE;
		this.surfaceSize = new Point(0, 0);
		this.linePaint = new Paint();
		this.linePaint.setDither(true);
		this.linePaint.setStyle(Paint.Style.STROKE);
		this.linePaint.setStrokeJoin(Paint.Join.ROUND);
	}

	public ToolState getState() {
		return state;
	}

	public void movePosition(float delta_x, float delta_y, Point delta_to_scroll) {
		position.x += (int) delta_x;
		position.y += (int) delta_y;
		if (position.x < 0) {
			position.x = 0;
		}
		if (position.y < 0) {
			position.y = 0;
		}
		if (position.x >= this.surfaceSize.x) {
			position.x = this.surfaceSize.x - 1;
		}
		if (position.y >= this.surfaceSize.y) {
			position.y = this.surfaceSize.y - 1;
		}
		if (position.x < distanceFromScreenEdgeToScroll) {
			delta_to_scroll.x = -scrollSpeed;
		} else if (position.x >= this.surfaceSize.x - distanceFromScreenEdgeToScroll) {
			delta_to_scroll.x = scrollSpeed;
		}
		if (position.y < distanceFromScreenEdgeToScroll) {
			delta_to_scroll.y = -scrollSpeed;
		} else if (position.y >= this.surfaceSize.y - distanceFromScreenEdgeToScroll) {
			delta_to_scroll.y = scrollSpeed;
		}
		zoomedPosition.x = Math.round(position.x / DrawingSurface.Perspective.zoom);
		zoomedPosition.y = Math.round(position.y / DrawingSurface.Perspective.zoom);
	}

	public boolean singleTapEvent(DrawingSurface drawingSurface) {
		return false;
	}

	public boolean doubleTapEvent(int x, int y) {
		return false;
	}

	public void activate() {
		this.state = ToolState.ACTIVE;
		this.position = new Point(surfaceSize.x / 2, surfaceSize.y / 2);
		this.startPosition = new Point(surfaceSize.x / 2, surfaceSize.y / 2);
	}

	public void activate(Point coordinates) {
		this.state = ToolState.ACTIVE;
		this.position = coordinates;
		setStartPosition();
	}

	public void deactivate() {
		this.state = ToolState.INACTIVE;
	}

	public Point getPosition() {
		return position;
	}

	/**
	 * set start position
	 */
	public void setStartPosition() {
		startPosition.x = position.x;
		startPosition.y = position.y;
	}

	public void setSurfaceSize(Point size) {
		this.surfaceSize = size;
	}

	/**
	 * resets position to startPosition
	 * 
	 */
	public void reset() {
		position.x = startPosition.x;
		position.y = startPosition.y;
	}

	/**
	 * returns the screen size
	 * 
	 * @return screen size
	 */
	public Point getScreenSize() {
		return this.surfaceSize;
	}

	public abstract void draw(Canvas view_canvas, Cap shape, int stroke_width, int color);

}
