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
package org.catrobat.paintroid.tools.implementation

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.test.espresso.idling.CountingIdlingResource
import org.catrobat.paintroid.R
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.serialization.SerializableTypeface
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.FontType
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.options.TextToolOptionsView
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

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
    toolOptionsViewController: ToolOptionsViewController,
    toolPaint: ToolPaint,
    workspace: Workspace,
    idlingResource: CountingIdlingResource,
    commandManager: CommandManager,
    override var drawTime: Long
) : BaseToolWithRectangleShape(
    contextCallback,
    toolOptionsViewController,
    toolPaint,
    workspace,
    idlingResource,
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
    var font = FontType.SANS_SERIF

    @VisibleForTesting
    @JvmField
    var underlined = false

    @VisibleForTesting
    @JvmField
    var italic = false

    @VisibleForTesting
    @JvmField
    var bold = false

    private var textSize = DEFAULT_TEXT_SIZE
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
        resetPreview()
        resetBoxPosition()

        val callback: TextToolOptionsView.Callback = object : TextToolOptionsView.Callback {
            override fun setText(text: String) {
                this@TextTool.text = text
                resetPreview()
                workspace.invalidate()
            }

            override fun setFont(fontType: FontType) {
                this@TextTool.font = fontType
                updateTypeface()
                resetPreview()
                workspace.invalidate()
            }

            override fun setUnderlined(underlined: Boolean) {
                this@TextTool.underlined = underlined
                textPaint.isUnderlineText = this@TextTool.underlined
                resetPreview()
                workspace.invalidate()
            }

            override fun setItalic(italic: Boolean) {
                this@TextTool.italic = italic
                updateTypeface()
                resetPreview()
                workspace.invalidate()
            }

            override fun setBold(bold: Boolean) {
                this@TextTool.bold = bold
                textPaint.isFakeBoldText = this@TextTool.bold
                resetPreview()
                workspace.invalidate()
            }

            override fun setTextSize(size: Int) {
                textSize = size
                textPaint.textSize = textSize * TEXT_SIZE_MAGNIFICATION_FACTOR
                resetPreview()
                workspace.invalidate()
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
        textPaint.textSize = DEFAULT_TEXT_SIZE * TEXT_SIZE_MAGNIFICATION_FACTOR
        textPaint.isUnderlineText = underlined
        textPaint.isFakeBoldText = bold
        updateTypeface()
    }

    override fun drawBitmap(canvas: Canvas, boxWidth: Float, boxHeight: Float) {
        val textAscent = textPaint.ascent()
        val textDescent = textPaint.descent()
        val textHeight = (textDescent - textAscent) * multilineText.size
        val lineHeight = textHeight / multilineText.size
        val maxTextWidth = multilineText.maxOf { line ->
            textPaint.measureText(line)
        }

        canvas.save()

        val widthScaling = (boxWidth - 2 * BOX_OFFSET) / maxTextWidth
        val heightScaling = (boxHeight - 2 * BOX_OFFSET) / textHeight

        canvas.scale(widthScaling, heightScaling)

        val scaledHeightOffset = BOX_OFFSET / heightScaling
        val scaledWidthOffset = BOX_OFFSET / widthScaling
        val scaledBoxWidth = boxWidth / widthScaling
        val scaledBoxHeight = boxHeight / heightScaling

        multilineText.forEachIndexed { index, textLine ->
            canvas.drawText(
                textLine,
                -(scaledBoxWidth / 2) + scaledWidthOffset,
                -(scaledBoxHeight / 2) + scaledHeightOffset - textAscent + lineHeight * index,
                textPaint
            )
        }

        canvas.restore()
    }

    private fun resetPreview() {
        val textDescent = textPaint.descent()
        val textAscent = textPaint.ascent()
        val textHeight = textDescent - textAscent

        val maxTextWidth = multilineText.maxOf { line ->
            textPaint.measureText(line)
        }

        boxHeight = textHeight * multilineText.size + 2 * BOX_OFFSET
        boxWidth = maxTextWidth + 2 * BOX_OFFSET
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        super.onSaveInstanceState(bundle)
        bundle?.apply {
            putBoolean(BUNDLE_TOOL_UNDERLINED, underlined)
            putBoolean(BUNDLE_TOOL_ITALIC, italic)
            putBoolean(BUNDLE_TOOL_BOLD, bold)
            putString(BUNDLE_TOOL_TEXT, text)
            putInt(BUNDLE_TOOL_TEXT_SIZE, textSize)
            putString(BUNDLE_TOOL_FONT, font.name)
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
            font = FontType.valueOf(getString(BUNDLE_TOOL_FONT, font.name))
        }
        textToolOptionsView.setState(bold, italic, underlined, text, textSize, font)
        textPaint.isUnderlineText = underlined
        textPaint.isFakeBoldText = bold
        updateTypeface()
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    private fun updateTypeface() {
        val style = if (italic) Typeface.ITALIC else Typeface.NORMAL
        val textSkewX = if (italic) ITALIC_TEXT_SKEW else DEFAULT_TEXT_SKEW
        when (font) {
            FontType.SANS_SERIF -> textPaint.typeface = Typeface.create(Typeface.SANS_SERIF, style)
            FontType.SERIF -> textPaint.typeface = Typeface.create(Typeface.SERIF, style)
            FontType.MONOSPACE -> textPaint.typeface = Typeface.create(Typeface.MONOSPACE, style)
            FontType.STC ->
                try {
                    textPaint.typeface = stc
                    textPaint.textSkewX = textSkewX
                } catch (e: Exception) {
                    Log.e(TAG, "stc_regular")
                }
            FontType.DUBAI ->
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
        toolPosition.set(position)
        boxWidth = width
        boxHeight = height
        workspace.invalidate()
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
