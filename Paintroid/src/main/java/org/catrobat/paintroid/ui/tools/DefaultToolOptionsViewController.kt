/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid.ui.tools

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.ANIMATION_DURATION
import org.catrobat.paintroid.presenter.MainActivityPresenter
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController

class DefaultToolOptionsViewController(
    val activity: Activity,
    val idlingResource: CountingIdlingResource
) : ToolOptionsViewController {
    private val bottomNavigation: ViewGroup =
        activity.findViewById(R.id.pocketpaint_main_bottom_navigation)
    private val mainToolOptions: ViewGroup =
        activity.findViewById(R.id.pocketpaint_layout_tool_options)
    private val topBarSpecificViewCheckmark: View =
        activity.findViewById(R.id.pocketpaint_btn_top_checkmark)
    private val topBar: View =
        activity.findViewById(R.id.pocketpaint_layout_top_bar)

    private var toolOptionsShown = false
    private var enabled = true
    private var hideButtonsEnabled = true
    private var callback: ToolOptionsVisibilityController.Callback? = null

    override val toolSpecificOptionsLayout: ViewGroup
        get() = activity.findViewById(R.id.pocketpaint_layout_tool_specific_options)

    override val isVisible: Boolean
        get() = toolOptionsShown

    init {
        mainToolOptions.visibility = View.INVISIBLE
    }

    private fun notifyHide() {
        callback?.onHide()
    }

    private fun notifyShow() {
        callback?.onShow()
    }

    override fun resetToOrigin() {
        toolOptionsShown = false
        mainToolOptions.visibility = View.INVISIBLE
        mainToolOptions.y = bottomNavigation.y + bottomNavigation.height
    }

    override fun hide() {
        if (!enabled || !hideButtonsEnabled) {
            return
        }
        idlingResource.increment()
        toolOptionsShown = false
        mainToolOptions.animate().y(bottomNavigation.y + bottomNavigation.height)
        mainToolOptions.visibility = View.GONE
        notifyHide()
        idlingResource.decrement()
    }

    override fun disable() {
        enabled = false
        if (isVisible) {
            resetToOrigin()
        }
    }

    override fun enable() {
        enabled = true
    }

    override fun show() {
        if (!enabled || !hideButtonsEnabled) {
            return
        }
        idlingResource.increment()
        toolOptionsShown = true
        mainToolOptions.visibility = View.INVISIBLE
        mainToolOptions.post {
            val yPos = bottomNavigation.y - mainToolOptions.height
            mainToolOptions.animate().y(yPos)
            mainToolOptions.visibility = View.VISIBLE
        }
        notifyShow()
        idlingResource.decrement()
    }

    override fun showDelayed() {
        toolSpecificOptionsLayout.post { show() }
    }

    override fun removeToolViews() {
        toolSpecificOptionsLayout.removeAllViews()
        callback = null
    }

    override fun setCallback(callback: ToolOptionsVisibilityController.Callback) {
        this.callback = callback
    }

    override fun showCheckmark() {
        topBarSpecificViewCheckmark.visibility = View.VISIBLE
    }

    override fun hideCheckmark() {
        topBarSpecificViewCheckmark.visibility = View.GONE
    }

    override fun enableHide() {
        hideButtonsEnabled = true
    }

    override fun disableHide() {
        hideButtonsEnabled = false
    }

    override fun slideUp(view: View, willHide: Boolean, showOptionsView: Boolean) {
        if (!enabled || !hideButtonsEnabled) {
            return
        }

        if (!willHide) {
            view.visibility = View.VISIBLE
            toolOptionsShown = showOptionsView
        }

        val animation: TranslateAnimation = if (willHide) {
            TranslateAnimation(
                0F,
                0F,
                0F,
                -view.height.toFloat()
            )
        } else {
            TranslateAnimation(
                0F,
                0F,
                view.height.toFloat(),
                0F
            )
        }

        animation.duration = ANIMATION_DURATION
        view.startAnimation(animation)
        if (willHide) {
            view.visibility = View.INVISIBLE
            toolOptionsShown = showOptionsView
            notifyHide()
        } else {
            notifyShow()
        }
    }

    override fun slideDown(view: View, willHide: Boolean, showOptionsView: Boolean) {
        if (!enabled || !hideButtonsEnabled) {
            return
        }

        val animation: TranslateAnimation = if (willHide) {
            TranslateAnimation(
                0F,
                0F,
                0F,
                view.height.toFloat()
            )
        } else {
            TranslateAnimation(
                0F,
                0F,
                -view.height.toFloat(),
                0F
            )
        }

        animation.duration = ANIMATION_DURATION
        view.startAnimation(animation)
        if (willHide) {
            view.visibility = View.INVISIBLE
            toolOptionsShown = showOptionsView
            notifyHide()
        } else {
            view.visibility = View.VISIBLE
            toolOptionsShown = showOptionsView
            notifyShow()
        }
    }

    override fun animateBottomAndTopNavigation(hide: Boolean) {
        val mainActivityPresenter = (activity as MainActivity).presenter as MainActivityPresenter
        mainActivityPresenter.hideBottomBarViewHolder()
        when (hide) {
            true -> {
                if (topBar.visibility == View.VISIBLE) {
                    slideUp(topBar, willHide = true, showOptionsView = toolOptionsShown)
                }
                if (bottomNavigation.visibility == View.VISIBLE) {
                    slideDown(bottomNavigation, willHide = true, showOptionsView = toolOptionsShown)
                }
            }
            false -> {
                if (topBar.visibility == View.INVISIBLE) {
                    slideDown(topBar, willHide = false, showOptionsView = toolOptionsShown)
                }
                if (bottomNavigation.visibility == View.INVISIBLE) {
                    slideUp(bottomNavigation, willHide = false, showOptionsView = toolOptionsShown)
                }
            }
        }
    }
}
