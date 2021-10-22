package org.catrobat.paintroid.listener

import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import org.catrobat.paintroid.ui.LayerAdapter

class DrawerLayoutListener(private val view: DrawerLayout, private val adapter: LayerAdapter) : DrawerLayout.DrawerListener {
    override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

    override fun onDrawerOpened(drawerView: View) = Unit

    override fun onDrawerClosed(drawerView: View) {
        adapter.setDrawerLayoutOpen(false)
    }

    override fun onDrawerStateChanged(newState: Int) {
        if (newState == DrawerLayout.STATE_DRAGGING && !view.isDrawerOpen(GravityCompat.END)) {
            adapter.setDrawerLayoutOpen(true)
            for (i in 0 until adapter.count) {
                adapter.getViewHolderAt(i)?.let { holder ->
                    holder.bitmap?.let { bitmapImageView ->
                        holder.updateImageView(bitmapImageView, true)
                    }
                }
            }
        }
    }
}
