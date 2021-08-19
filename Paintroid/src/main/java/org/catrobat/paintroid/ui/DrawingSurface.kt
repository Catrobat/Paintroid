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
package org.catrobat.paintroid.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.contract.MainActivityContracts
import org.catrobat.paintroid.listener.DrawingSurfaceListener
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTask
import org.catrobat.paintroid.listener.DrawingSurfaceListener.AutoScrollTaskCallback
import org.catrobat.paintroid.listener.DrawingSurfaceListener.DrawingSurfaceListenerCallback
import org.catrobat.paintroid.tools.Tool
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.options.ToolOptionsViewController

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
    private lateinit var presenter: MainActivityContracts.Presenter

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
        val handler = Handler(Looper.getMainLooper())
        val autoScrollTask = AutoScrollTask(handler, AutoScrollTaskCallbackImpl())
        val density = resources.displayMetrics.density
        val callback: DrawingSurfaceListenerCallback = object : DrawingSurfaceListenerCallback {
            override fun getCurrentTool(): Tool = toolReference.get()

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
        drawingSurfaceListener = DrawingSurfaceListener(autoScrollTask, callback, density, presenter)
        setOnTouchListener(drawingSurfaceListener)
    }

    fun setArguments(
            layerModel: LayerContracts.Model,
            perspective: Perspective,
            toolReference: ToolReference,
            toolOptionsViewController: ToolOptionsViewController,
            presenter: MainActivityContracts.Presenter,
    ) {
        this.layerModel = layerModel
        this.perspective = perspective
        this.toolReference = toolReference
        this.toolOptionsViewController = toolOptionsViewController
        this.presenter = presenter
    }

    @Synchronized
    private fun doDraw(surfaceViewCanvas: Canvas) {
        synchronized(layerModel) {
            if (surfaceReady) {
                canvasRect.set(0, 0, layerModel.width, layerModel.height)
                perspective.applyToCanvas(surfaceViewCanvas)

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

                val iterator = layerModel.listIterator(layerModel.layerCount)

                while (iterator.hasPrevious()) {
                    iterator.previous().bitmap?.let { surfaceViewCanvas.drawBitmap(it, 0f, 0f, null) }
                }

                val tool = toolReference.get()
                tool?.draw(surfaceViewCanvas)
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

    fun enableAutoScroll() {
        drawingSurfaceListener.enableAutoScroll()
    }

    fun disableAutoScroll() {
        drawingSurfaceListener.disableAutoScroll()
    }

    @Synchronized
    fun setBitmap(bitmap: Bitmap?) {
        layerModel.currentLayer?.bitmap = bitmap
    }

    fun isPointOnCanvas(pointX: Int, pointY: Int): Boolean =
        pointX > 0 && pointX < layerModel.width && pointY > 0 && pointY < layerModel.height

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        surfaceReady = true
        val currentToolType = toolReference.get().toolType
        if (currentToolType != ToolType.IMPORTPNG && currentToolType != ToolType.TRANSFORM && currentToolType != ToolType.TEXT) {
            perspective.resetScaleAndTranslation()
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

    private open inner class AutoScrollTaskCallbackImpl : AutoScrollTaskCallback {
        override fun isPointOnCanvas(pointX: Int, pointY: Int): Boolean =
            this@DrawingSurface.isPointOnCanvas(pointX, pointY)

        override fun refreshDrawingSurface() {
            this@DrawingSurface.refreshDrawingSurface()
        }

        override fun handleToolMove(coordinate: PointF) {
            toolReference.get().handleMove(coordinate)
        }

        override fun getToolAutoScrollDirection(
            pointX: Float,
            pointY: Float,
            screenWidth: Int,
            screenHeight: Int
        ): Point = toolReference.get()
            .getAutoScrollDirection(pointX, pointY, screenWidth, screenHeight)

        override fun getPerspectiveScale(): Float = perspective.scale

        override fun translatePerspective(dx: Float, dy: Float) {
            perspective.translate(dx, dy)
        }

        override fun convertToCanvasFromSurface(surfacePoint: PointF) {
            perspective.convertToCanvasFromSurface(surfacePoint)
        }

        override fun getCurrentToolType(): ToolType = toolReference.get().toolType
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
                    canvas = holder.lockCanvas()
                    canvas?.let {
                        doDraw(it)
                    }
                } finally {
                    canvas?.let {
                        holder.unlockCanvasAndPost(it)
                    }
                }
            }
        }
    }
}
