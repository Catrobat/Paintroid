/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.ui.dragndrop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import org.catrobat.paintroid.presenter.LayerPresenter
import kotlin.math.max
import kotlin.math.min

private const val ALPHA_VALUE = 192

class DragAndDropListView : ListView, ListItemLongClickHandler {
    private var view: View? = null
    private var hoveringListItem: BitmapDrawable? = null
    private var viewBounds: Rect? = null
    private var presenter: DragAndDropPresenter? = null
    private var position = 0
    private var initialPosition = 0
    private var mergePosition = 0
    private var downY = 0f
    private var offsetToCenter = 0

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attributes: AttributeSet?) : super(context, attributes)

    constructor(context: Context?, attributes: AttributeSet?, defStyle: Int) : super(
        context,
        attributes,
        defStyle
    )

    init {
        onItemLongClickListener = OnItemLongClickListener { _, view, position, _ ->
            presenter?.onLongClickLayerAtPosition(position, view)
            true
        }
        onItemClickListener = OnItemClickListener { _, view, position, _ ->
            presenter?.onClickLayerAtPosition(position, view)
        }
    }

    fun setPresenter(presenter: DragAndDropPresenter) {
        this.presenter = presenter
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        hoveringListItem ?: return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_UP -> handleTouchUp()
            MotionEvent.ACTION_CANCEL -> stopDragging()
            MotionEvent.ACTION_DOWN -> downY = event.y
            MotionEvent.ACTION_MOVE -> {
                val dY = event.y - downY
                downY += dY
                downY -= offsetToCenter
                viewBounds?.let {
                    it.offsetTo(it.left, downY.toInt())
                    val top = min(height - it.height(), max(0, it.top))
                    val bottom = top + it.height()
                    hoveringListItem?.setBounds(it.left, top, it.right, bottom)
                }
                invalidate()
                swapListItems()
            }
        }
        return true
    }

    override fun handleOnItemLongClick(position: Int, view: View) {
        this.view?.visibility = VISIBLE
        this.view = view
        initialPosition = position
        this.position = position
        view.visibility = INVISIBLE
        hoveringListItem = getHoveringListItem(view)
        setOffsetToCenter(viewBounds)
        invalidate()
    }

    override fun stopDragging() {
        hoveringListItem ?: return
        if (presenter is LayerPresenter) {
            (presenter as LayerPresenter).resetMergeColor(mergePosition)
        }
        mergePosition = -1
        view?.visibility = VISIBLE
        view = null
        hoveringListItem = null
        invalidate()
    }

    public override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        hoveringListItem?.draw(canvas)
    }

    private fun getHoveringListItem(view: View): BitmapDrawable {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        val drawable = BitmapDrawable(resources, bitmap)
        viewBounds = Rect(view.left, view.top, view.right, view.bottom)
        viewBounds?.let {
            drawable.bounds = it
        }
        drawable.alpha = ALPHA_VALUE
        return drawable
    }

    private fun setOffsetToCenter(viewBounds: Rect?) {
        viewBounds ?: return
        offsetToCenter = viewBounds.height() / 2
    }

    private fun getItemAbove(itemPositionAbove: Int): View? =
        if (isPositionValid(itemPositionAbove)) getChildAt(itemPositionAbove) else null

    private fun getItemBelow(itemPositionBelow: Int): View? =
        if (isPositionValid(itemPositionBelow)) getChildAt(itemPositionBelow) else null

    private fun swapItems(swapWith: Int) {
        presenter?.let { position = it.swapItemsVisually(position, swapWith) }
        view?.visibility = VISIBLE
        view = getChildAt(position)
        view?.visibility = INVISIBLE
        mergePosition = -1
        invalidateViews()
    }

    private fun mergeItems(mergeWith: Int) {
        presenter?.markMergeable(position, mergeWith)
        mergePosition = mergeWith
    }

    private fun swapListItems() {
        val itemPositionAbove = position - 1
        val itemPositionBelow = position + 1
        val itemBelow = getItemBelow(itemPositionBelow)
        val itemAbove = getItemAbove(itemPositionAbove)
        var canMergeUpwards = false
        var canMergeDownwards = false
        var isAbove = false
        var isBelow = false
        if (itemAbove != null) {
            isBelow = downY < itemAbove.y
            view?.let {
                canMergeUpwards = downY < itemAbove.y + it.height / 2f
            }
        }
        if (itemBelow != null) {
            isAbove = downY > itemBelow.y
            view?.let {
                canMergeDownwards = downY > itemBelow.y - it.height / 2f
            }
        }
        if (isAbove || isBelow) {
            val swapWith = if (isAbove) itemPositionBelow else itemPositionAbove
            swapItems(swapWith)
        } else if (canMergeUpwards || canMergeDownwards) {
            val mergeWith = if (canMergeUpwards) itemPositionAbove else itemPositionBelow
            mergeItems(mergeWith)
        } else if (mergePosition != -1) {
            mergePosition = -1
            invalidateViews()
        }
    }

    private fun isPositionValid(position: Int): Boolean = position in 0 until count

    private fun handleTouchUp() {
        if (mergePosition != -1) {
            presenter?.mergeItems(initialPosition, mergePosition)
        } else {
            presenter?.reorderItems(initialPosition, position)
        }
        stopDragging()
    }
}
