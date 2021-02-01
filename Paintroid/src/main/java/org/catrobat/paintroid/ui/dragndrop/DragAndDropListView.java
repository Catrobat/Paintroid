/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.dragndrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DragAndDropListView extends ListView implements ListItemLongClickHandler {

	public static final String TAG = DragAndDropListView.class.getSimpleName();

	private View view;
	private BitmapDrawable hoveringListItem;
	private int position;
	private int initialPosition;
	private int mergePosition;

	private Rect viewBounds;
	private float downY = 0;
	private int offsetToCenter = 0;

	private DragAndDropPresenter presenter;

	public DragAndDropListView(Context context) {
		super(context);
		init();
	}

	public DragAndDropListView(Context context, AttributeSet attributes) {
		super(context, attributes);
		init();
	}

	public DragAndDropListView(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);
		init();
	}

	private void init() {
		setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				presenter.onLongClickLayerAtPosition(position, view);
				return true;
			}
		});

		setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				presenter.onClickLayerAtPosition(position, view);
			}
		});
	}

	public void setPresenter(DragAndDropPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (hoveringListItem == null) {
			return super.onTouchEvent(event);
		}

		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				handleTouchUp();
				break;
			case MotionEvent.ACTION_CANCEL:
				stopDragging();
				break;
			case MotionEvent.ACTION_DOWN:
				downY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				float dY = event.getY() - downY;
				downY += dY;
				downY -= offsetToCenter;

				viewBounds.offsetTo(viewBounds.left, (int) downY);
				int top = Math.min(getHeight() - viewBounds.height(), Math.max(0, viewBounds.top));
				int bottom = top + viewBounds.height();
				hoveringListItem.setBounds(viewBounds.left, top, viewBounds.right, bottom);

				invalidate();
				swapListItems();
				break;
		}
		return true;
	}

	@Override
	public void handleOnItemLongClick(int position, View view) {
		if (this.view != null) {
			this.view.setVisibility(VISIBLE);
		}
		this.view = view;
		this.initialPosition = position;
		this.position = position;
		view.setVisibility(INVISIBLE);
		hoveringListItem = getHoveringListItem(view);
		setOffsetToCenter(viewBounds);
		invalidate();
	}

	@Override
	public void stopDragging() {
		if (hoveringListItem != null) {
			mergePosition = -1;
			view.setVisibility(VISIBLE);
			view = null;
			hoveringListItem = null;
			invalidate();
		}
	}

	@Override
	public void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (hoveringListItem != null) {
			hoveringListItem.draw(canvas);
		}
	}

	private BitmapDrawable getHoveringListItem(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);

		viewBounds = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
		drawable.setBounds(viewBounds);
		drawable.setAlpha(192);

		return drawable;
	}

	private void setOffsetToCenter(Rect viewBounds) {
		offsetToCenter = (viewBounds.height() / 2);
	}

	private void swapListItems() {
		int itemPositionAbove = position - 1;
		int itemPositionBelow = position + 1;

		View itemBelow = null;
		View itemAbove = null;

		if (isPositionValid(itemPositionAbove)) {
			itemAbove = getChildAt(itemPositionAbove);
		}

		if (isPositionValid(itemPositionBelow)) {
			itemBelow = getChildAt(itemPositionBelow);
		}

		boolean canMergeUpwards = (itemAbove != null) && (downY < itemAbove.getY() + view.getHeight() / 2.f);
		boolean canMergeDownwards = (itemBelow != null) && (downY > itemBelow.getY() - view.getHeight() / 2.f);

		boolean isAbove = (itemBelow != null) && (downY > itemBelow.getY());
		boolean isBelow = (itemAbove != null) && (downY < itemAbove.getY());

		if (isAbove || isBelow) {
			int swapWith = isAbove ? itemPositionBelow : itemPositionAbove;
			position = presenter.swapItemsVisually(position, swapWith);

			view.setVisibility(VISIBLE);
			view = getChildAt(position);
			view.setVisibility(INVISIBLE);

			mergePosition = -1;

			invalidateViews();
		} else if (canMergeUpwards || canMergeDownwards) {
			int mergeWith = canMergeUpwards ? itemPositionAbove : itemPositionBelow;
			presenter.markMergeable(position, mergeWith);

			mergePosition = mergeWith;
		} else if (mergePosition != -1) {
			mergePosition = -1;
			invalidateViews();
		}
	}

	private boolean isPositionValid(int position) {
		return (position >= 0 && position < getCount());
	}

	private void handleTouchUp() {
		if (mergePosition != -1) {
			presenter.mergeItems(initialPosition, mergePosition);
		} else {
			presenter.reorderItems(initialPosition, position);
		}

		stopDragging();
	}
}
