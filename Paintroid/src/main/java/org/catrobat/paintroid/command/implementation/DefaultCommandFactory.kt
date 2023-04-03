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
package org.catrobat.paintroid.command.implementation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandFactory
import org.catrobat.paintroid.command.implementation.FlipCommand.FlipDirection
import org.catrobat.paintroid.command.implementation.RotateCommand.RotateDirection
import org.catrobat.paintroid.command.serialization.SerializablePath
import org.catrobat.paintroid.command.serialization.SerializableTypeface
import org.catrobat.paintroid.common.CommonFactory
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.tools.drawable.ShapeDrawable
import org.catrobat.paintroid.tools.helper.JavaFillAlgorithmFactory
import org.catrobat.paintroid.tools.helper.toPoint

class DefaultCommandFactory : CommandFactory {
    private val commonFactory = CommonFactory()
    override fun createInitCommand(width: Int, height: Int): Command = CompositeCommand().apply {
        addCommand(SetDimensionCommand(width, height))
        addCommand(AddEmptyLayerCommand(commonFactory))
    }

    override fun createInitCommand(bitmap: Bitmap): Command = CompositeCommand().apply {
        addCommand(SetDimensionCommand(bitmap.width, bitmap.height))
        addCommand(LoadCommand(bitmap))
    }

    override fun createInitCommand(layers: List<LayerContracts.Layer>): Command = CompositeCommand().apply {
        layers[0].let {
            val bitmap = it.bitmap
            addCommand(SetDimensionCommand(bitmap.width, bitmap.height))
        }
        addCommand(LoadLayerListCommand(layers))
    }

    override fun createResetCommand(): Command = CompositeCommand().apply {
        addCommand(ResetCommand())
        addCommand(AddEmptyLayerCommand(commonFactory))
    }

    override fun createAddEmptyLayerCommand(): Command = AddEmptyLayerCommand(commonFactory)

    override fun createSelectLayerCommand(position: Int): Command = SelectLayerCommand(position)

    override fun createLayerOpacityCommand(position: Int, opacityPercentage: Int): Command = LayerOpacityCommand(position, opacityPercentage)

    override fun createRemoveLayerCommand(index: Int): Command = RemoveLayerCommand(index)

    override fun createReorderLayersCommand(position: Int, swapWith: Int): Command =
        ReorderLayersCommand(position, swapWith)

    override fun createMergeLayersCommand(position: Int, mergeWith: Int): Command =
        MergeLayersCommand(position, mergeWith)

    override fun createRotateCommand(rotateDirection: RotateDirection): Command =
        RotateCommand(rotateDirection)

    override fun createFlipCommand(flipDirection: FlipDirection): Command =
        FlipCommand(flipDirection)

    override fun createCropCommand(
        resizeCoordinateXLeft: Int,
        resizeCoordinateYTop: Int,
        resizeCoordinateXRight: Int,
        resizeCoordinateYBottom: Int,
        maximumBitmapResolution: Int
    ): Command = CropCommand(
        resizeCoordinateXLeft,
        resizeCoordinateYTop,
        resizeCoordinateXRight,
        resizeCoordinateYBottom,
        maximumBitmapResolution
    )

    override fun createPointCommand(paint: Paint, coordinate: PointF): Command = PointCommand(
        commonFactory.createPaint(paint),
        commonFactory.createPointF(coordinate)
    )

    override fun createFillCommand(x: Int, y: Int, paint: Paint, colorTolerance: Float): Command =
        FillCommand(
            JavaFillAlgorithmFactory(),
            commonFactory.createPoint(x, y),
            commonFactory.createPaint(paint),
            colorTolerance
        )

    override fun createGeometricFillCommand(
        shapeDrawable: ShapeDrawable,
        position: Point,
        box: RectF,
        boxRotation: Float,
        paint: Paint
    ): Command {
        val destRectF = commonFactory.createRectF(box)
        return GeometricFillCommand(
            shapeDrawable,
            position.x,
            position.y,
            destRectF,
            boxRotation,
            commonFactory.createPaint(paint)
        )
    }

    override fun createPathCommand(paint: Paint, path: SerializablePath): Command = PathCommand(
        commonFactory.createPaint(paint),
        commonFactory.createSerializablePath(path)
    )

    override fun createSmudgePathCommand(
        bitmap: Bitmap,
        pointPath: MutableList<PointF>,
        maxPressure: Float,
        maxSize: Float,
        minSize: Float
    ): Command {
        val copy = mutableListOf<PointF>()

        pointPath.forEach {
            copy.add(commonFactory.createPointF(it))
        }

        return SmudgePathCommand(bitmap.copy(Bitmap.Config.ARGB_8888, false), copy, maxPressure, maxSize, minSize)
    }

    override fun createTextToolCommand(
        multilineText: Array<String>,
        textPaint: Paint,
        boxOffset: Int,
        boxWidth: Float,
        boxHeight: Float,
        toolPosition: PointF,
        boxRotation: Float,
        typefaceInfo: SerializableTypeface
    ): Command = TextToolCommand(
        multilineText, commonFactory.createPaint(textPaint),
        boxOffset.toFloat(), boxWidth, boxHeight, commonFactory.createPointF(toolPosition),
        boxRotation, typefaceInfo
    )

    override fun createResizeCommand(newWidth: Int, newHeight: Int): Command =
        ResizeCommand(newWidth, newHeight)

    override fun createClipboardCommand(
        bitmap: Bitmap,
        toolPosition: PointF,
        boxWidth: Float,
        boxHeight: Float,
        boxRotation: Float
    ): Command = ClipboardCommand(
        bitmap,
        toPoint(toolPosition),
        boxWidth,
        boxHeight,
        boxRotation
    )

    override fun createClippingCommand(bitmap: Bitmap, pathBitmap: Bitmap): Command =
        ClippingCommand(
            bitmap,
            pathBitmap
        )

    override fun createSprayCommand(sprayedPoints: FloatArray, paint: Paint): Command =
        SprayCommand(sprayedPoints, paint)

    override fun createCutCommand(
        toolPosition: PointF,
        boxWidth: Float,
        boxHeight: Float,
        boxRotation: Float
    ): Command = CutCommand(toPoint(toolPosition), boxWidth, boxHeight, boxRotation)

    override fun createColorChangedCommand(
        toolReference: ToolReference,
        context: Context,
        color: Int
    ): Command =
        ColorChangedCommand(toolReference, context, color)
}
