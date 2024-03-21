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
package org.catrobat.paintroid.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.fragment.app.FragmentManager
import org.catrobat.paintroid.R
import org.catrobat.paintroid.UserPreferences
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.listener.DrawingSurfaceListener
import org.catrobat.paintroid.listener.DrawingSurfaceListener.DrawingSurfaceListenerCallback
import org.catrobat.paintroid.model.LayerModel
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.zoomwindow.ZoomWindowController
import org.catrobat.paintroid.ui.viewholder.DrawerLayoutViewHolder
import org.catrobat.paintroid.ui.zoomwindow.DefaultZoomWindowController.Constants

open class DrawingSurface : SurfaceView, SurfaceHolder.Callback {
    private val canvasRect = Rect()
    private val framePaint = Paint()
    private val checkeredPattern = Paint()
    private var surfaceDirty = false
    private var surfaceReady = false
    private var bgColor = 0
    private var surfaceLock: Object? = null
    private var drawingThread: DrawingSurfaceThread? = null
    private var drawingSurfaceListener: DrawingSurfaceListener
    private lateinit var layerModel: LayerContracts.Model
    private lateinit var perspective: Perspective
    private lateinit var toolReference: ToolReference
    private lateinit var toolOptionsViewController: ToolOptionsViewController
    private lateinit var drawerLayoutViewHolder: DrawerLayoutViewHolder
    private lateinit var fragmentManager: FragmentManager
    private lateinit var idlingResource: CountingIdlingResource
    private lateinit var zoomController: ZoomWindowController
    private lateinit var sharedPreferences: UserPreferences

    constructor(context: Context?, attrSet: AttributeSet?) : super(context, attrSet)

    constructor(context: Context?) : super(context)

    init {
        surfaceLock = Object()
        bgColor = ContextCompat.getColor(
            context,
            R.color.pocketpaint_main_drawing_surface_background
        )
        framePaint.color = Color.BLACK
        framePaint.style = Paint.Style.STROKE
        framePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        val checkerboard =
            BitmapFactory.decodeResource(resources, R.drawable.pocketpaint_checkeredbg)
        val shader = BitmapShader(checkerboard, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        checkeredPattern.shader = shader
        checkeredPattern.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
        val density = resources.displayMetrics.density
        val callback: DrawingSurfaceListenerCallback = object : DrawingSurfaceListenerCallback {
            override fun getCurrentTool(): Tool? = toolReference.tool

            override fun multiplyPerspectiveScale(factor: Float) {
                perspective.multiplyScale(factor)
            }

            override fun translatePerspective(x: Float, y: Float) {
                perspective.translate(x, y)
            }

            override fun convertToCanvasFromSurface(surfacePoint: PointF) {
                perspective.convertToCanvasFromSurface(surfacePoint)
            }

            override fun getToolOptionsViewController(): ToolOptionsViewController =
                toolOptionsViewController
        }
        drawingSurfaceListener = DrawingSurfaceListener(callback, density)
        setOnTouchListener(drawingSurfaceListener)
    }

    companion object {
        private val TAG = DrawingSurface::class.java.name
    }

    fun setArguments(
        layerModel: LayerContracts.Model,
        perspective: Perspective,
        toolReference: ToolReference,
        idlingResource: CountingIdlingResource,
        fragmentManager: FragmentManager,
        toolOptionsViewController: ToolOptionsViewController,
        drawerLayoutViewHolder: DrawerLayoutViewHolder,
        zoomController: ZoomWindowController,
        sharedPreferences: UserPreferences
    ) {
        this.layerModel = layerModel
        this.perspective = perspective
        this.toolReference = toolReference
        this.toolOptionsViewController = toolOptionsViewController
        this.idlingResource = idlingResource
        this.fragmentManager = fragmentManager
        this.drawerLayoutViewHolder = drawerLayoutViewHolder
        this.zoomController = zoomController
        drawingSurfaceListener.setZoomController(zoomController, sharedPreferences)
        this.sharedPreferences = sharedPreferences
    }

    @Synchronized
    private fun doDraw(surfaceViewCanvas: Canvas) {
        synchronized(layerModel) {
            if (surfaceReady) {
                prepareCanvas(surfaceViewCanvas)
                drawBackground(surfaceViewCanvas)
                val currentBitmap = drawLayers(surfaceViewCanvas)
                handleZoomCompatibility(currentBitmap)
            }
        }
    }

    private fun prepareCanvas(surfaceViewCanvas: Canvas) {
        canvasRect.set(0, 0, layerModel.width, layerModel.height)
        perspective.applyToCanvas(surfaceViewCanvas)
    }

    private fun drawBackground(surfaceViewCanvas: Canvas) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            surfaceViewCanvas.drawColor(bgColor, PorterDuff.Mode.SRC)
        } else {
            surfaceViewCanvas.save()
            surfaceViewCanvas.clipOutRect(canvasRect)
            surfaceViewCanvas.drawColor(bgColor, PorterDuff.Mode.SRC)
            surfaceViewCanvas.restore()
        }
        surfaceViewCanvas.drawRect(canvasRect, checkeredPattern)
        surfaceViewCanvas.drawRect(canvasRect, framePaint)
    }

    private fun drawLayers(surfaceViewCanvas: Canvas): Bitmap? {
        val currentTool = toolReference.tool
        val bitmapOfDrawingBoard = Bitmap.createBitmap(layerModel.width, layerModel.height, Bitmap.Config.ARGB_8888)
        val currentLayerIndex = layerModel.currentLayer?.let { layerModel.getLayerIndexOf(it) }

        if (currentTool?.toolType?.name.equals(ToolType.ERASER.name)) {
            (layerModel as LayerModel).drawLayersOntoCanvas(surfaceViewCanvas)
            surfaceViewCanvas.setBitmap(bitmapOfDrawingBoard)
            (layerModel as LayerModel).drawLayersOntoCanvasCorrectOrderEraser(surfaceViewCanvas, currentLayerIndex, currentTool)
        } else {
            val drawingBoardCanvas = Canvas(bitmapOfDrawingBoard)
            (layerModel as LayerModel).drawLayersOntoCanvasCorrectOrder(surfaceViewCanvas, currentLayerIndex, drawingBoardCanvas, currentTool)
        }
        return bitmapOfDrawingBoard
    }

    private fun handleZoomCompatibility(currentBitmap: Bitmap?) {
        val currentTool = toolReference.tool
        if (zoomController.checkIfToolCompatibleWithZoomWindow(currentTool) == Constants.COMPATIBLE) {
            handler.post {
                zoomController.setBitmap(currentBitmap)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        holder.addCallback(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        holder.removeCallback(this)
    }

    fun refreshDrawingSurface() {
        surfaceLock?.let {
            synchronized(it) {
                surfaceDirty = true
                it.notify()
            }
        }
    }

    @Synchronized
    fun setBitmap(bitmap: Bitmap?) {
        layerModel.currentLayer?.bitmap = bitmap!!
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        surfaceReady = true

        if (perspective.callResetScaleAndTransformationOnStartUp != 2) {
            perspective.resetScaleAndTranslation()
            perspective.callResetScaleAndTransformationOnStartUp++
        }

        perspective.setSurfaceFrame(holder.surfaceFrame)
        drawingThread?.start()
        refreshDrawingSurface()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        holder.setFormat(PixelFormat.RGBA_8888)
        drawingThread?.stop()
        drawingThread = DrawingSurfaceThread(this, DrawLoop())
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceReady = false
        drawingThread?.stop()
    }

    fun copyBitmap() {

    }

    private inner class DrawLoop : Runnable {
        val holder: SurfaceHolder = getHolder()
        override fun run() {
            surfaceLock?.let {
                synchronized(it) {
                    if (!surfaceDirty && surfaceReady) {
                        try {
                            it.wait()
                        } catch (e: InterruptedException) {
                            return
                        }
                    } else {
                        surfaceDirty = false
                    }
                    if (!surfaceReady) {
                        return
                    }
                }
            }

            var canvas: Canvas? = null
            synchronized(holder) {
                try {
                    idlingResource.increment()
                    canvas = holder.lockCanvas()
                    canvas?.let {
                        doDraw(it)
                    }
                    canvas
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "run: ", e)
                } finally {
                    idlingResource.decrement()
                    canvas?.let {
                        holder.unlockCanvasAndPost(it)
                    }
                }
            }
        }
    }
}
