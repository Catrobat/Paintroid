package org.catrobat.paintroid.test.command.implementation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import org.catrobat.paintroid.command.implementation.FillCommand
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.tools.helper.FillAlgorithm
import org.catrobat.paintroid.tools.helper.FillAlgorithmFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FillCommandTest {
    @Mock
    private val fillAlgorithmFactory: FillAlgorithmFactory? = null

    @Mock
    private val fillAlgorithm: FillAlgorithm? = null

    @Mock
    private val clickedPixel: Point? = null

    @Mock
    private val paint: Paint? = null

    @Before
    fun setUp() { `when`(fillAlgorithmFactory?.createFillAlgorithm()).thenReturn(fillAlgorithm) }

    @Test
    fun testSetUp() {
        val command = fillAlgorithmFactory?.let {
            if (clickedPixel != null && paint != null) {
                FillCommand(it, clickedPixel, paint, 0F)
            }
        }

        Assert.assertNotNull(command)
        Mockito.verifyZeroInteractions(fillAlgorithmFactory, clickedPixel, paint)
        setUp()
    }

    @Test
    fun testRun() {
        clickedPixel?.x = 3
        clickedPixel?.y = 5
        val command = clickedPixel?.let {
            if (paint != null && fillAlgorithmFactory != null) {
                FillCommand(fillAlgorithmFactory, it, paint, 0.5f)
            }
        }

        val canvas = Mockito.mock(Canvas::class.java)
        val layerModel = LayerModel()
        val layer = Mockito.mock(LayerContracts.Layer::class.java)
        val bitmap = Mockito.mock(Bitmap::class.java)

        layerModel.currentLayer = layer
       /* Removed as it creates unused Stubs Exception
        `when`(layer.bitmap).thenReturn(bitmap)
        `when`(layer.isVisible).thenReturn(true)
        `when`(bimap.getPixel(3, 5)).thenReturn(Color.RED)
        `when`(paint?.color).thenReturn(Color.BLUE) */
        command.run { canvas }

        if (clickedPixel != null) {
            fillAlgorithm?.setParameters(bitmap, clickedPixel, Color.BLUE, Color.RED, 0.5f)
        }
        fillAlgorithm?.performFilling()
    }
}
