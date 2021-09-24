/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.tools.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.serialization.SerializableTypeface
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.TextToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController
import kotlin.Exception
import kotlin.math.max

@VisibleForTesting
const val TEXT_SIZE_MAGNIFICATION_FACTOR = 3f

@VisibleForTesting
const val BOX_OFFSET = 20

@VisibleForTesting
const val MARGIN_TOP = 50.0f

private const val ROTATION_ENABLED = true
private const val RESIZE_POINTS_VISIBLE = true
private const val ITALIC_TEXT_SKEW = -0.25f
private const val DEFAULT_TEXT_SKEW = 0.0f
private const val DEFAULT_TEXT_SIZE = 20
private const val BUNDLE_TOOL_UNDERLINED = "BUNDLE_TOOL_UNDERLINED"
private const val BUNDLE_TOOL_ITALIC = "BUNDLE_TOOL_ITALIC"
private const val BUNDLE_TOOL_BOLD = "BUNDLE_TOOL_BOLD"
private const val BUNDLE_TOOL_TEXT = "BUNDLE_TOOL_TEXT"
private const val BUNDLE_TOOL_TEXT_SIZE = "BUNDLE_TOOL_TEXT_SIZE"
private const val BUNDLE_TOOL_FONT = "BUNDLE_TOOL_FONT"
private const val TAG = "Can't set custom font"

class TextTool(
    private val textToolOptionsView: TextToolOptionsView,
    contextCallback: ContextCallback,
    toolOptionsViewController: ToolOptionsVisibilityController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    commandManager
) {
    @VisibleForTesting
    @JvmField
    val textPaint: Paint

    @VisibleForTesting
    @JvmField
    var text = ""

    @VisibleForTesting
    @JvmField
    var font = "Sans Serif"

    @VisibleForTesting
    @JvmField
    var underlined = false

    @VisibleForTesting
    @JvmField
    var italic = false

    @VisibleForTesting
    @JvmField
    var bold = false

    @VisibleForTesting
    @JvmField
    var textSize = DEFAULT_TEXT_SIZE

    private val stc: Typeface?
    private val dubai: Typeface?

    @get:VisibleForTesting
    val multilineText: Array<String>
        get() = text.split("\n").toTypedArray()

    override val toolType: ToolType
        get() = ToolType.TEXT

    init {
        rotationEnabled = ROTATION_ENABLED
        resizePointsVisible = RESIZE_POINTS_VISIBLE
        stc = contextCallback.getFont(R.font.stc_regular)
        dubai = contextCallback.getFont(R.font.dubai)
        textPaint = Paint()
        initializePaint()
        createAndSetBitmap()
        resetBoxPosition()
        toolOptionsViewController.setCallback(object : ToolOptionsVisibilityController.Callback {
            override fun onHide() {
                createAndSetBitmap()
            }

            override fun onShow() {
                createAndSetBitmap()
            }
        })
        val callback: TextToolOptionsView.Callback = object : TextToolOptionsView.Callback {
            override fun setText(text: String) {
                this@TextTool.text = text
                createAndSetBitmap()
            }

            override fun setFont(font: String) {
                this@TextTool.font = font
                updateTypeface()
                val width = boxWidth
                val height = boxHeight
                val position = PointF(toolPosition.x, toolPosition.y)
                createAndSetBitmap()
                toolPosition.set(position)
                boxWidth = width
                boxHeight = height
            }

            override fun setUnderlined(underlined: Boolean) {
                this@TextTool.underlined = underlined
                textPaint.isUnderlineText = this@TextTool.underlined
                val width = boxWidth
                val height = boxHeight
                val position = PointF(toolPosition.x, toolPosition.y)
                createAndSetBitmap()
                toolPosition.set(position)
                boxWidth = width
                boxHeight = height
            }

            override fun setItalic(italic: Boolean) {
                this@TextTool.italic = italic
                updateTypeface()
                val width = boxWidth
                val height = boxHeight
                val position = PointF(toolPosition.x, toolPosition.y)
                createAndSetBitmap()
                toolPosition.set(position)
                boxWidth = width
                boxHeight = height
            }

            override fun setBold(bold: Boolean) {
                this@TextTool.bold = bold
                textPaint.isFakeBoldText = this@TextTool.bold
                val width = boxWidth
                val height = boxHeight
                val position = PointF(toolPosition.x, toolPosition.y)
                createAndSetBitmap()
                toolPosition.set(position) 
                boxWidth = width
                boxHeight = height
            }

            override fun setTextSize(size: Int) {
                textSize = size
                textPaint.textSize = textSize * TEXT_SIZE_MAGNIFICATION_FACTOR
                createAndSetBitmap()
            }

            override fun hideToolOptions() {
                this@TextTool.toolOptionsViewController.hide()
            }
        }
        textToolOptionsView.setCallback(callback)
        toolOptionsViewController.showDelayed()
    }

    private fun initializePaint() {
        textPaint.isAntiAlias = DEFAULT_ANTIALIASING_ON
        textPaint.color = toolPaint.previewColor
        textPaint.textSize = textSize * TEXT_SIZE_MAGNIFICATION_FACTOR
        textPaint.isUnderlineText = underlined
        textPaint.isFakeBoldText = bold
        updateTypeface()
    }

    private fun createAndSetBitmap() {
        val multilineText = multilineText
        val textDescent = textPaint.descent()
        val textAscent = textPaint.ascent()
        val upperBoxEdge = toolPosition.y - boxHeight / 2.0f
        val textHeight = textDescent - textAscent
        boxHeight = textHeight * multilineText.size + 2 * BOX_OFFSET
        toolPosition.y = upperBoxEdge + boxHeight / 2.0f
        var maxTextWidth = 0f
        for (str in multilineText) {
            maxTextWidth = max(maxTextWidth, textPaint.measureText(str))
        }
        boxWidth = maxTextWidth + 2 * BOX_OFFSET
        val bitmap =
            Bitmap.createBitmap(boxWidth.toInt(), boxHeight.toInt(), Bitmap.Config.ARGB_8888)
        val drawCanvas = Canvas(bitmap)
        for (i in multilineText.indices) {
            drawCanvas.drawText(
                multilineText[i],
                BOX_OFFSET.toFloat(),
                BOX_OFFSET - textAscent + textHeight * i,
                textPaint
            )
        }
        setBitmap(bitmap)
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.apply {
            putBoolean(BUNDLE_TOOL_UNDERLINED, underlined)
            putBoolean(BUNDLE_TOOL_ITALIC, italic)
            putBoolean(BUNDLE_TOOL_BOLD, bold)
            putString(BUNDLE_TOOL_TEXT, text)
            putInt(BUNDLE_TOOL_TEXT_SIZE, textSize)
            putString(BUNDLE_TOOL_FONT, font)
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle?) {
        super.onRestoreInstanceState(bundle)
        bundle?.apply {
            underlined = getBoolean(BUNDLE_TOOL_UNDERLINED, underlined)
            italic = getBoolean(BUNDLE_TOOL_ITALIC, italic)
            bold = getBoolean(BUNDLE_TOOL_BOLD, bold)
            text = getString(BUNDLE_TOOL_TEXT, text)
            textSize = getInt(BUNDLE_TOOL_TEXT_SIZE, textSize)
            font = getString(BUNDLE_TOOL_FONT, font)
        }
        textToolOptionsView.setState(bold, italic, underlined, text, textSize, font)
        textPaint.isUnderlineText = underlined
        textPaint.isFakeBoldText = bold
        updateTypeface()
        createAndSetBitmap()
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun updateTypeface() {
        val style = if (italic) Typeface.ITALIC else Typeface.NORMAL
        val textSkewX = if (italic) ITALIC_TEXT_SKEW else DEFAULT_TEXT_SKEW
        when (font) {
            "Sans Serif" -> textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, style)
            "Serif" -> textPaint.typeface = Typeface.create(Typeface.SERIF, style)
            "Monospace" -> textPaint.typeface = Typeface.create(Typeface.MONOSPACE, style)
            "STC" ->
                try {
                    textPaint.typeface = stc
                    textPaint.textSkewX = textSkewX
                } catch (e: Exception) {
                    Log.e(TAG, "stc_regular")
                }
            "Dubai" ->
                try {
                    textPaint.typeface = dubai
                    textPaint.textSkewX = textSkewX
                } catch (e: Exception) {
                    Log.e(TAG, "dubai")
                }
        }
    }

    private fun changeTextColor() {
        val width = boxWidth
        val height = boxHeight
        val position = PointF(toolPosition.x, toolPosition.y)
        textPaint.color = toolPaint.previewColor
        createAndSetBitmap()
        toolPosition.set(position)
        boxWidth = width
        boxHeight = height
    }

    override fun resetInternalState() = Unit

    override fun onClickOnButton() {
        highlightBox()
        val toolPosition = PointF(toolPosition.x, toolPosition.y)
        val typeFaceInfo = SerializableTypeface(
            font,
            bold,
            underlined,
            italic,
            textPaint.textSize,
            textPaint.textSkewX
        )
        val command = commandFactory.createTextToolCommand(
            multilineText,
            textPaint,
            BOX_OFFSET,
            boxWidth,
            boxHeight,
            toolPosition,
            boxRotation,
            typeFaceInfo
        )
        commandManager.addCommand(command)
    }

    @VisibleForTesting
    fun resetBoxPosition() {
        if (workspace.scale <= 1) {
            toolPosition.x = workspace.width / 2.0f
            toolPosition.y = boxHeight / 2.0f + MARGIN_TOP
        }
    }

    override fun changePaintColor(color: Int) {
        super.changePaintColor(color)
        changeTextColor()
    }
}
