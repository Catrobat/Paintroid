/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.paintroid

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.catrobat.paintroid.common.RESULT_INTRO_MW_NOT_SUPPORTED
import org.catrobat.paintroid.intro.IntroPageViewAdapter
import org.catrobat.paintroid.tools.ToolType
import java.util.Locale

private const val DEFAULT_TEXT_SIZE = 30f

class WelcomeActivity : AppCompatActivity() {
    private var colorActive = 0
    private var colorInactive = 0

    @VisibleForTesting
    lateinit var viewPager: ViewPager

    @VisibleForTesting
    lateinit var layouts: IntArray

    private lateinit var dotsLayout: LinearLayoutCompat
    private lateinit var btnSkip: AppCompatButton
    private lateinit var btnNext: AppCompatButton

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.SimpleOnPageChangeListener() {
            var pos = 0

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pos = position
                addBottomDots(position)
                if (getDotsIndex(position) == layouts.size - 1) {
                    btnNext.setText(R.string.lets_go)
                    btnSkip.visibility = View.GONE
                } else {
                    btnNext.setText(R.string.next)
                    btnSkip.visibility = View.VISIBLE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    val toolTypes = ToolType.values()
                    if (layouts[pos] == R.layout.pocketpaint_slide_intro_possibilities) {
                        val head: AppCompatTextView =
                            findViewById(R.id.pocketpaint_intro_possibilities_head)
                        val description: AppCompatTextView =
                            findViewById(R.id.pocketpaint_intro_possibilities_text)
                        setUpUndoAndRedoButtons(head, description)
                        setUpNavigationView(head, description)
                    } else if (layouts[pos] == R.layout.pocketpaint_slide_intro_tools_selection) {
                        val view = findViewById<View>(R.id.pocketpaint_intro_bottom_bar)
                        for (type in toolTypes) {
                            val toolButton = view.findViewById<View>(type.toolButtonID) ?: continue
                            toolButton.setOnClickListener {
                                val toolName =
                                    findViewById<AppCompatTextView>(R.id.pocketpaint_textview_intro_tools_header)
                                toolName.setText(type.nameResource)
                                val toolDescription =
                                    findViewById<AppCompatTextView>(R.id.pocketpaint_tools_info_description)
                                toolDescription.setText(type.helpTextResource)
                                val icon =
                                    findViewById<AppCompatImageView>(R.id.pocketpaint_tools_info_icon)
                                icon.setImageResource(type.drawableResource)
                            }
                        }
                    }
                }
            }
        }

    private fun setUpUndoAndRedoButtons(head: AppCompatTextView, description: AppCompatTextView) {
        val topBar: AppBarLayout =
            findViewById(R.id.pocketpaint_intro_possibilities_topbar)
        val undo: AppCompatImageButton =
            topBar.findViewById(R.id.pocketpaint_btn_top_undo)
        val redo: AppCompatImageButton =
            topBar.findViewById(R.id.pocketpaint_btn_top_redo)
        undo.setOnClickListener {
            head.setText(ToolType.UNDO.nameResource)
            description.setText(ToolType.UNDO.helpTextResource)
        }
        redo.setOnClickListener {
            head.setText(ToolType.REDO.nameResource)
            description.setText(ToolType.REDO.helpTextResource)
        }
    }

    private fun setUpNavigationView(head: AppCompatTextView, description: AppCompatTextView) {
        val relativeLayout: RelativeLayout =
            findViewById(R.id.pocketpaint_intro_possibilities_bottom_bar)
        val navigationView: BottomNavigationView =
            relativeLayout.findViewById(R.id.pocketpaint_bottom_navigation)
        navigationView.setOnNavigationItemSelectedListener { menuItem ->
            head.text = menuItem.title
            when (menuItem.itemId) {
                R.id.action_tools ->
                    description.text =
                        resources.getText(R.string.intro_bottom_navigation_tools_description)
                R.id.action_current_tool ->
                    description.text =
                        resources.getText(R.string.intro_bottom_navigation_current_description)
                R.id.action_color_picker ->
                    description.text =
                        resources.getText(R.string.intro_bottom_navigation_color_description)
                else ->
                    description.text =
                        resources.getText(R.string.intro_bottom_navigation_layers_description)
            }
            false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.PocketPaintWelcomeActivityTheme)
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isInMultiWindowMode) {
            setResult(RESULT_INTRO_MW_NOT_SUPPORTED)
            finish()
            return
        }
        setContentView(R.layout.activity_pocketpaint_welcome)
        viewPager = findViewById(R.id.pocketpaint_view_pager)
        dotsLayout = findViewById(R.id.pocketpaint_layout_dots)
        btnSkip = findViewById(R.id.pocketpaint_btn_skip)
        btnNext = findViewById(R.id.pocketpaint_btn_next)
        colorActive = ContextCompat.getColor(this, R.color.pocketpaint_welcome_dot_active)
        colorInactive = ContextCompat.getColor(this, R.color.pocketpaint_welcome_dot_inactive)
        layouts = intArrayOf(
            R.layout.pocketpaint_slide_intro_welcome,
            R.layout.pocketpaint_slide_intro_possibilities,
            R.layout.pocketpaint_slide_intro_tools_selection,
            R.layout.pocketpaint_slide_intro_landscape,
            R.layout.pocketpaint_slide_intro_getstarted
        )
        changeStatusBarColor()
        initViewPager()
        btnSkip.setOnClickListener { finish() }
        btnNext.setOnClickListener {
            var finished: Boolean
            var current = getItem(1)
            finished = current > layouts.size - 1
            if (isRTL(this@WelcomeActivity)) {
                current = getItem(-1)
                finished = current < 0
            }
            if (finished) {
                finish()
            } else {
                viewPager.currentItem = current
            }
        }
    }

    private fun initViewPager() {
        if (isRTL(this)) {
            layouts.reverse()
        }
        viewPager.adapter = IntroPageViewAdapter(layouts)
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)
        if (isRTL(this)) {
            val pos = layouts.size
            viewPager.currentItem = pos
            addBottomDots(layouts.size - 1)
        } else {
            addBottomDots(0)
        }
    }

    private fun addBottomDots(currentPage: Int) {
        val dots = arrayOfNulls<AppCompatTextView>(layouts.size)
        val currentIndex = getDotsIndex(currentPage)
        dotsLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = AppCompatTextView(this).apply {
                text = "•"
                textSize = DEFAULT_TEXT_SIZE
                setTextColor(colorInactive)
            }
            dotsLayout.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            dots[currentIndex]?.setTextColor(colorActive)
        }
    }

    private fun getItem(i: Int): Int = viewPager.currentItem + i

    private fun changeStatusBarColor() {
        window.run {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun defaultLocaleIsRTL(): Boolean {
        val locale = Locale.getDefault()
        if (locale.toString().isEmpty()) {
            return false
        }
        val directionality = Character.getDirectionality(locale.displayName[0]).toInt()
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt() || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
    }

    private fun isRTL(context: Context): Boolean {
        val layoutDirection = context.resources.configuration.layoutDirection
        val layoutDirectionIsRTL = layoutDirection == View.LAYOUT_DIRECTION_RTL
        return layoutDirectionIsRTL || defaultLocaleIsRTL()
    }

    private fun getDotsIndex(position: Int): Int =
        if (isRTL(this)) layouts.size - position - 1 else position

    override fun onBackPressed() {
        finish()
    }
}
