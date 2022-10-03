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
package org.catrobat.paintroid.test.junit.serialization

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.implementation.AddEmptyLayerCommand
import org.catrobat.paintroid.command.implementation.AsyncCommandManager
import org.catrobat.paintroid.command.implementation.CompositeCommand
import org.catrobat.paintroid.command.implementation.CropCommand
import org.catrobat.paintroid.command.implementation.CutCommand
import org.catrobat.paintroid.command.implementation.DefaultCommandFactory
import org.catrobat.paintroid.command.implementation.FillCommand
import org.catrobat.paintroid.command.implementation.FlipCommand
import org.catrobat.paintroid.command.implementation.GeometricFillCommand
import org.catrobat.paintroid.command.implementation.LoadLayerListCommand
import org.catrobat.paintroid.command.implementation.LoadCommand
import org.catrobat.paintroid.command.implementation.MergeLayersCommand
import org.catrobat.paintroid.command.implementation.PathCommand
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.command.implementation.RemoveLayerCommand
import org.catrobat.paintroid.command.implementation.ReorderLayersCommand
import org.catrobat.paintroid.command.implementation.ResetCommand
import org.catrobat.paintroid.command.implementation.ResizeCommand
import org.catrobat.paintroid.command.implementation.RotateCommand
import org.catrobat.paintroid.command.implementation.SelectLayerCommand
import org.catrobat.paintroid.command.implementation.SetDimensionCommand
import org.catrobat.paintroid.command.implementation.SprayCommand
import org.catrobat.paintroid.command.implementation.StampCommand
import org.catrobat.paintroid.command.implementation.TextToolCommand
import org.catrobat.paintroid.command.implementation.SmudgePathCommand
import org.catrobat.paintroid.command.implementation.LayerOpacityCommand
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.command.serialization.SerializablePath
import org.catrobat.paintroid.command.serialization.SerializableTypeface
import org.catrobat.paintroid.model.CommandManagerModel
import org.catrobat.paintroid.model.Layer
import org.catrobat.paintroid.model.MainActivityModel
import org.catrobat.paintroid.tools.FontType
import org.catrobat.paintroid.tools.drawable.HeartDrawable
import org.catrobat.paintroid.tools.drawable.OvalDrawable
import org.catrobat.paintroid.tools.drawable.RectangleDrawable
import org.catrobat.paintroid.tools.drawable.StarDrawable
import org.catrobat.paintroid.tools.implementation.DefaultToolPaint
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class CommandSerializationTest {

    private lateinit var commandSerializer: CommandSerializer
    private lateinit var expectedModel: CommandManagerModel
    private lateinit var paint: Paint
    private lateinit var model: MainActivityModel
    private val commandFactory = DefaultCommandFactory()

    companion object {
        private const val WORKSPACE_WIDTH = 500
        private const val WORKSPACE_HEIGHT = 1000
    }

    @Before
    fun setUp() {
        val context = mock(Context::class.java)
        val resources = mock(Resources::class.java)
        val commandManger = mock(AsyncCommandManager::class.java)
        val model = mock(MainActivityModel::class.java)

        commandSerializer = CommandSerializer(context, commandManger, model)
        val initialCommand: Command =
            commandFactory.createInitCommand(WORKSPACE_WIDTH, WORKSPACE_HEIGHT)
        expectedModel = CommandManagerModel(initialCommand, ArrayList())

        `when`(context.resources).thenReturn(resources)
        `when`(commandManger.commandManagerModel).thenReturn(expectedModel)
        paint = DefaultToolPaint(context).apply {
            strokeWidth = 25f
            strokeCap = Paint.Cap.SQUARE
            color = 50
        }.paint
    }

    @Test
    fun testSerializeAddLayerCommand() {
        expectedModel.commands.add(commandFactory.createAddEmptyLayerCommand())
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeOpacityCommand() {
        expectedModel.commands.add(commandFactory.createLayerOpacityCommand(0, 50))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializePointCommand() {
        expectedModel.commands.add(commandFactory.createPointCommand(paint, PointF(30.43f, 40.28f)))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeSprayCommand() {
        expectedModel.commands.add(
            commandFactory.createSprayCommand(
                floatArrayOf(20f, 347.5f, 99.239f),
                paint
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeSelectCommand() {
        expectedModel.commands.add(commandFactory.createSelectLayerCommand(3))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeDefaultInitCommand() {
        expectedModel.commands.add(
            commandFactory.createInitCommand(
                WORKSPACE_WIDTH,
                WORKSPACE_HEIGHT
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeLoadInitCommand() {
        expectedModel.commands.add(
            commandFactory.createInitCommand(
                Bitmap.createBitmap(
                    WORKSPACE_WIDTH,
                    WORKSPACE_HEIGHT,
                    Bitmap.Config.ARGB_8888
                )
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeLoadListInitCommand() {
        val layerList = ArrayList<Layer>()
        layerList.add(
            Layer(
                Bitmap.createBitmap(
                    WORKSPACE_WIDTH,
                    WORKSPACE_HEIGHT,
                    Bitmap.Config.ARGB_8888
                )
            )
        )
        expectedModel.commands.add(commandFactory.createInitCommand(layerList))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeTextToolCommand() {
        val typeface = SerializableTypeface(
            FontType.MONOSPACE,
            bold = false,
            underline = false,
            italic = true,
            textSize = 25f,
            textSkewX = -0.25f
        )

        expectedModel.commands.add(
            commandFactory.createTextToolCommand(
                arrayOf("Serialization", "is", "fun", "!.?)4`\""),
                paint, 20, 50.3f, 40.5f, PointF(30f, 25.243f), 10.23f, typeface
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeHeartGeometricCommand() {
        expectedModel.commands.add(
            commandFactory.createGeometricFillCommand(
                HeartDrawable(),
                Point(10, 20),
                RectF(10f, 20f, 30f, 40f),
                25f,
                paint
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeOvalGeometricCommand() {
        expectedModel.commands.add(
            commandFactory.createGeometricFillCommand(
                OvalDrawable(),
                Point(10, 20),
                RectF(10f, 20f, 30f, 40f),
                25f,
                paint
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeRectangleGeometricCommand() {
        expectedModel.commands.add(
            commandFactory.createGeometricFillCommand(
                RectangleDrawable(),
                Point(10, 20),
                RectF(10f, 20f, 30f, 40f),
                25f,
                paint
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeStarGeometricCommand() {
        expectedModel.commands.add(
            commandFactory.createGeometricFillCommand(
                StarDrawable(),
                Point(10, 20),
                RectF(10f, 20f, 30f, 40f),
                25f,
                paint
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeFillToolCommand() {
        expectedModel.commands.add(commandFactory.createFillCommand(10, 30, paint, 0.5f))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeFlipCommand() {
        expectedModel.commands.add(commandFactory.createFlipCommand(FlipCommand.FlipDirection.FLIP_VERTICAL))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeCropCommand() {
        expectedModel.commands.add(commandFactory.createCropCommand(1, 2, 3, 4, 5))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeCutCommand() {
        expectedModel.commands.add(commandFactory.createCutCommand(PointF(20f, 40f), 5f, 10f, 15f))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeMergeLayersCommand() {
        expectedModel.commands.add(commandFactory.createMergeLayersCommand(2, 1))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializePathCommand() {
        val path = SerializablePath().apply {
            moveTo(20f, 30f)
            lineTo(30f, 10f)
            quadTo(10f, 20f, 30f, 40f)
            cubicTo(10f, 20f, 30f, 3f, 19f, 20f)
        }
        expectedModel.commands.add(commandFactory.createPathCommand(paint, path))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeSmudgePathCommand() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val pointArray = mutableListOf<PointF>()
        pointArray.add(PointF(0f, 0f))
        pointArray.add(PointF(1f, 1f))
        pointArray.add(PointF(2f, 2f))
        pointArray.add(PointF(3f, 3f))
        val pressure = 1f
        val maxSize = 100f
        val minSize = 1f
        expectedModel.commands.add(commandFactory.createSmudgePathCommand(bitmap, pointArray, pressure, maxSize, minSize))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeRemoveLayerCommand() {
        expectedModel.commands.add(commandFactory.createRemoveLayerCommand(4))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeReorderLayersCommand() {
        expectedModel.commands.add(commandFactory.createReorderLayersCommand(4, 10))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeResetCommand() {
        expectedModel.commands.add(commandFactory.createResetCommand())
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeResizeCommand() {
        expectedModel.commands.add(commandFactory.createResizeCommand(400, 200))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeRotateCommand() {
        expectedModel.commands.add(commandFactory.createRotateCommand(RotateCommand.RotateDirection.ROTATE_RIGHT))
        assertSerializeAndDeserialize()
    }

    @Test
    fun testSerializeStampCommand() {
        expectedModel.commands.add(
            commandFactory.createStampCommand(
                Bitmap.createBitmap(WORKSPACE_WIDTH, WORKSPACE_HEIGHT, Bitmap.Config.ARGB_8888),
                PointF(20f, 30f), 40f, 50f, 60f
            )
        )
        assertSerializeAndDeserialize()
    }

    @Test
    fun testMultipleCommands() {
        with(expectedModel.commands) {
            add(commandFactory.createSprayCommand(floatArrayOf(20f, 347.5f, 99.239f), paint))
            add(commandFactory.createResizeCommand(400, 200))
            add(commandFactory.createRotateCommand(RotateCommand.RotateDirection.ROTATE_RIGHT))
            add(commandFactory.createAddEmptyLayerCommand())
            add(commandFactory.createPointCommand(paint, PointF(30.43f, 40.28f)))
        }
    }

    private fun assertSerializeAndDeserialize() {
        val resultModel = with(commandSerializer.kryo) {
            Output(DEFAULT_BUFFER_SIZE, -1).use { output ->
                Input(output.buffer).use { input ->
                    writeObject(output, expectedModel)
                    readObject(input, CommandManagerModel::class.java)
                }
            }
        }
        assertCommandManagerModelEquals(resultModel)
    }

    private fun assertCommandManagerModelEquals(actualModel: CommandManagerModel) {
        Assert.assertTrue(equalsCommand(expectedModel.initialCommand, actualModel.initialCommand))
        Assert.assertEquals(expectedModel.commands.size, actualModel.commands.size)
        actualModel.commands.reverse()
        expectedModel.commands.zip(actualModel.commands).forEach { commandPair ->
            Assert.assertTrue(equalsCommand(commandPair.component1(), commandPair.component2()))
        }
    }

    private fun equalsCommand(expectedCommand: Command, actualCommand: Command): Boolean {
        if (expectedCommand.javaClass != actualCommand.javaClass) {
            return false
        }
        return when (expectedCommand) {
            is AddEmptyLayerCommand -> true
            is CompositeCommand -> equalsCompositeCommand(
                expectedCommand, actualCommand as CompositeCommand
            )
            is SetDimensionCommand -> equalsDimensionCommand(
                expectedCommand, actualCommand as SetDimensionCommand
            )
            is PointCommand -> equalsPointCommand(
                expectedCommand, actualCommand as PointCommand
            )
            is SprayCommand -> equalsSprayCommand(
                expectedCommand, actualCommand as SprayCommand
            )
            is SelectLayerCommand -> equalsSelectLayerCommand(
                expectedCommand, actualCommand as SelectLayerCommand
            )
            is MergeLayersCommand -> equalsMergeLayersCommand(
                expectedCommand, actualCommand as MergeLayersCommand
            )
            is RemoveLayerCommand -> equalsRemoveLayerCommand(
                expectedCommand, actualCommand as RemoveLayerCommand
            )
            is ReorderLayersCommand -> equalsReorderLayersCommand(
                expectedCommand, actualCommand as ReorderLayersCommand
            )
            is ResetCommand -> true
            is ResizeCommand -> equalsResizeCommand(
                expectedCommand, actualCommand as ResizeCommand
            )
            is RotateCommand -> equalsRotateCommand(
                expectedCommand, actualCommand as RotateCommand
            )
            is CropCommand -> equalsCropCommand(
                expectedCommand, actualCommand as CropCommand
            )
            is CutCommand -> equalsCutCommand(expectedCommand, actualCommand as CutCommand)
            is FillCommand -> equalsFillCommand(
                expectedCommand, actualCommand as FillCommand
            )
            is FlipCommand -> equalsFlipCommand(
                expectedCommand, actualCommand as FlipCommand
            )
            is LoadCommand -> equalsLoadCommand(
                expectedCommand, actualCommand as LoadCommand
            )
            is StampCommand -> equalsStampCommand(
                expectedCommand, actualCommand as StampCommand
            )
            is LoadLayerListCommand -> equalsLoadBitmapListCommand(
                expectedCommand, actualCommand as LoadLayerListCommand
            )
            is TextToolCommand -> equalsTextToolCommand(
                expectedCommand, actualCommand as TextToolCommand
            )
            is GeometricFillCommand -> equalsGeometricFillCommand(
                expectedCommand, actualCommand as GeometricFillCommand
            )
            is PathCommand -> equalsPathCommand(
                expectedCommand, actualCommand as PathCommand
            )
            is SmudgePathCommand -> equalsSmudgePathCommand(
                expectedCommand, actualCommand as SmudgePathCommand
            )
            is LayerOpacityCommand -> equalsLayerOpacityCommand(
                expectedCommand, actualCommand as LayerOpacityCommand
            )
            else -> false
        }
    }

    private fun equalsLayerOpacityCommand(
        expectedCommand: LayerOpacityCommand,
        actualCommand: LayerOpacityCommand
    ) =
        expectedCommand.position == actualCommand.position &&
            expectedCommand.opacityPercentage == actualCommand.opacityPercentage

    private fun equalsCompositeCommand(
        expectedCommand: CompositeCommand,
        actualCommand: CompositeCommand
    ): Boolean {
        if (expectedCommand.commands.size != actualCommand.commands.size) {
            return false
        }
        expectedCommand.commands.zip(actualCommand.commands).forEach { commandPair ->
            if (!equalsCommand(commandPair.component1(), commandPair.component2())) {
                return false
            }
        }
        return true
    }

    private fun equalsDimensionCommand(
        expectedCommand: SetDimensionCommand,
        actualCommand: SetDimensionCommand
    ) =
        expectedCommand.width == actualCommand.width && expectedCommand.height == actualCommand.height

    private fun equalsPointCommand(expectedCommand: PointCommand, actualCommand: PointCommand) =
        DefaultToolPaint.arePaintEquals(
            expectedCommand.paint,
            actualCommand.paint
        ) && expectedCommand.point.equals(actualCommand.point)

    private fun equalsSprayCommand(expectedCommand: SprayCommand, actualCommand: SprayCommand) =
        DefaultToolPaint.arePaintEquals(
            expectedCommand.paint,
            actualCommand.paint
        ) && expectedCommand.sprayedPoints.contentEquals(actualCommand.sprayedPoints)

    private fun equalsSelectLayerCommand(
        expectedCommand: SelectLayerCommand,
        actualCommand: SelectLayerCommand
    ) =
        expectedCommand.position == actualCommand.position

    private fun equalsMergeLayersCommand(
        expectedCommand: MergeLayersCommand,
        actualCommand: MergeLayersCommand
    ) =
        expectedCommand.mergeWith == actualCommand.mergeWith && expectedCommand.position == actualCommand.position

    private fun equalsRemoveLayerCommand(
        expectedCommand: RemoveLayerCommand,
        actualCommand: RemoveLayerCommand
    ) =
        expectedCommand.position == actualCommand.position

    private fun equalsReorderLayersCommand(
        expectedCommand: ReorderLayersCommand,
        actualCommand: ReorderLayersCommand
    ) =
        expectedCommand.destination == actualCommand.destination && expectedCommand.position == actualCommand.position

    private fun equalsResizeCommand(expectedCommand: ResizeCommand, actualCommand: ResizeCommand) =
        expectedCommand.newHeight == actualCommand.newHeight && expectedCommand.newWidth == actualCommand.newWidth

    private fun equalsRotateCommand(expectedCommand: RotateCommand, actualCommand: RotateCommand) =
        expectedCommand.rotateDirection == actualCommand.rotateDirection

    private fun equalsCropCommand(expectedCommand: CropCommand, actualCommand: CropCommand) =
        expectedCommand.resizeCoordinateXLeft == actualCommand.resizeCoordinateXLeft && expectedCommand.resizeCoordinateYTop == actualCommand.resizeCoordinateYTop &&
            expectedCommand.resizeCoordinateXRight == actualCommand.resizeCoordinateXRight && expectedCommand.resizeCoordinateYBottom == actualCommand.resizeCoordinateYBottom &&
            expectedCommand.maximumBitmapResolution == actualCommand.maximumBitmapResolution

    private fun equalsCutCommand(expectedCommand: CutCommand, actualCommand: CutCommand) =
        expectedCommand.toolPosition == actualCommand.toolPosition && expectedCommand.boxWidth == actualCommand.boxWidth &&
            expectedCommand.boxHeight == actualCommand.boxHeight && expectedCommand.boxRotation == actualCommand.boxRotation

    private fun equalsFillCommand(expectedCommand: FillCommand, actualCommand: FillCommand) =
        DefaultToolPaint.arePaintEquals(
            expectedCommand.paint,
            actualCommand.paint
        ) && expectedCommand.clickedPixel == actualCommand.clickedPixel &&
            expectedCommand.colorTolerance == actualCommand.colorTolerance

    private fun equalsFlipCommand(expectedCommand: FlipCommand, actualCommand: FlipCommand) =
        expectedCommand.flipDirection == actualCommand.flipDirection

    private fun equalsLoadCommand(expectedCommand: LoadCommand, actualCommand: LoadCommand) =
        expectedCommand.loadedBitmap.sameAs(actualCommand.loadedBitmap)

    private fun equalsLoadBitmapListCommand(
        expectedCommand: LoadLayerListCommand,
        actualCommand: LoadLayerListCommand
    ): Boolean {
        if (expectedCommand.loadedLayers.size != actualCommand.loadedLayers.size) {
            return false
        }
        expectedCommand.loadedLayers.zip(actualCommand.loadedLayers).forEach { commandPair ->
            if (!commandPair.component1().bitmap.sameAs(commandPair.component2().bitmap)) {
                return false
            }
        }
        return true
    }

    private fun equalsStampCommand(expectedCommand: StampCommand, actualCommand: StampCommand) =
        expectedCommand.bitmap!!.sameAs(actualCommand.bitmap) && expectedCommand.coordinates == actualCommand.coordinates &&
            expectedCommand.boxWidth == actualCommand.boxWidth && expectedCommand.boxHeight == actualCommand.boxHeight &&
            expectedCommand.boxRotation == actualCommand.boxRotation

    private fun equalsTextToolCommand(
        expectedCommand: TextToolCommand,
        actualCommand: TextToolCommand
    ) =
        DefaultToolPaint.arePaintEquals(
            expectedCommand.textPaint,
            actualCommand.textPaint
        ) && expectedCommand.multilineText.contentEquals(actualCommand.multilineText) &&
            expectedCommand.boxOffset == actualCommand.boxOffset && expectedCommand.boxWidth == actualCommand.boxWidth && expectedCommand.boxHeight == actualCommand.boxHeight &&
            expectedCommand.toolPosition == actualCommand.toolPosition && expectedCommand.rotationAngle == actualCommand.rotationAngle &&
            equalsSerializableTypeFace(expectedCommand.typeFaceInfo, actualCommand.typeFaceInfo)

    private fun equalsSerializableTypeFace(
        actualTypeFace: SerializableTypeface,
        expectedTypeFace: SerializableTypeface
    ) =
        actualTypeFace.font == expectedTypeFace.font &&
            actualTypeFace.bold == expectedTypeFace.bold &&
            actualTypeFace.underline == expectedTypeFace.underline &&
            actualTypeFace.italic == expectedTypeFace.italic &&
            actualTypeFace.textSize == expectedTypeFace.textSize &&
            actualTypeFace.textSkewX == expectedTypeFace.textSkewX

    private fun equalsGeometricFillCommand(
        expectedCommand: GeometricFillCommand,
        actualCommand: GeometricFillCommand
    ) =
        expectedCommand.pointX == actualCommand.pointX && expectedCommand.pointY == actualCommand.pointY &&
            DefaultToolPaint.arePaintEquals(
                expectedCommand.paint,
                actualCommand.paint
            ) && expectedCommand.boxRect == actualCommand.boxRect &&
            expectedCommand.boxRotation == actualCommand.boxRotation && expectedCommand.shapeDrawable.javaClass == actualCommand.shapeDrawable.javaClass

    private fun equalsPathCommand(expectedCommand: PathCommand, actualCommand: PathCommand) =
        equalsSerializablePath(
            expectedCommand.path as SerializablePath,
            actualCommand.path as SerializablePath
        ) &&
            DefaultToolPaint.arePaintEquals(expectedCommand.paint, actualCommand.paint)

    private fun equalsSmudgePathCommand(expectedCommand: SmudgePathCommand, actualCommand: SmudgePathCommand) =
        expectedCommand.maxPressure == actualCommand.maxPressure &&
            expectedCommand.maxSize == actualCommand.maxSize &&
            expectedCommand.minSize == actualCommand.minSize &&
            expectedCommand.originalBitmap.sameAs(actualCommand.originalBitmap) &&
            expectedCommand.pointPath == actualCommand.pointPath

    private fun equalsSerializablePath(
        expectedPath: SerializablePath,
        actualPath: SerializablePath
    ): Boolean {
        if (expectedPath.serializableActions.size != actualPath.serializableActions.size) {
            return false
        }
        expectedPath.serializableActions.zip(actualPath.serializableActions).forEach { actionPair ->
            if (!equalsSerializableAction(actionPair.component1(), actionPair.component2())) {
                return false
            }
        }
        return true
    }

    private fun equalsSerializableAction(
        expectedAction: SerializablePath.SerializableAction,
        actualAction: SerializablePath.SerializableAction
    ): Boolean {
        if (expectedAction.javaClass != actualAction.javaClass) {
            return false
        }
        when (expectedAction) {
            is SerializablePath.Move -> return equalsActionMove(
                expectedAction,
                actualAction as SerializablePath.Move
            )
            is SerializablePath.Line -> return equalsActionLine(
                expectedAction,
                actualAction as SerializablePath.Line
            )
            is SerializablePath.Quad -> return equalsActionQuad(
                expectedAction,
                actualAction as SerializablePath.Quad
            )
            is SerializablePath.Cube -> return equalsActionCube(
                expectedAction,
                actualAction as SerializablePath.Cube
            )
            is SerializablePath.Rewind -> return true
        }
        return false
    }

    private fun equalsActionMove(
        expectedAction: SerializablePath.Move,
        actualAction: SerializablePath.Move
    ) =
        expectedAction.x == actualAction.x && expectedAction.y == actualAction.y

    private fun equalsActionLine(
        expectedAction: SerializablePath.Line,
        actualAction: SerializablePath.Line
    ) =
        expectedAction.x == actualAction.x && expectedAction.y == actualAction.y

    private fun equalsActionQuad(
        expectedAction: SerializablePath.Quad,
        actualAction: SerializablePath.Quad
    ) =
        expectedAction.x1 == actualAction.x1 && expectedAction.y1 == actualAction.y1 &&
            expectedAction.x2 == actualAction.x2 && expectedAction.y2 == actualAction.y2

    private fun equalsActionCube(
        expectedAction: SerializablePath.Cube,
        actualAction: SerializablePath.Cube
    ) =
        expectedAction.x1 == actualAction.x1 && expectedAction.y1 == actualAction.y1 &&
            expectedAction.x2 == actualAction.x2 && expectedAction.y2 == actualAction.y2 &&
            expectedAction.x3 == actualAction.x3 && expectedAction.y3 == actualAction.y3
}
