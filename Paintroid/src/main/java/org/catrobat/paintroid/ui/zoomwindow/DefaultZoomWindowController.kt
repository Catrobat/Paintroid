package org.catrobat.paintroid.ui.zoomwindow

import android.app.Activity
import android.graphics.PointF
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import org.catrobat.paintroid.R
import org.catrobat.paintroid.contract.LayerContracts

class DefaultZoomWindowController(val activity: Activity, val layerModel: LayerContracts.Model) : ZoomWindowController {

    private val zoomWindow: RelativeLayout =
        activity.findViewById(R.id.pocketpaint_zoom_window)
    private val zoomWindowShape: ViewGroup =
        activity.findViewById(R.id.pocketpaint_zoom_window_inner)

    override fun show(coordinates: PointF) {
        if(isPointOnCanvas(coordinates.x, coordinates.y)){
            if(shouldBeInTheRight(coordinates = coordinates)) {
                Log.d("TAG", "YES")
                setLayoutAlignment(right = true)
            }
            else {
                setLayoutAlignment(right = false)
            }

            zoomWindow.visibility = View.VISIBLE
        }
    }

    override fun dismiss() {
        zoomWindow.visibility = View.GONE
    }

    override fun dismissOnPinch() {
        zoomWindow.visibility = View.GONE
    }

    private fun isPointOnCanvas(pointX: Float, pointY: Float): Boolean =
        pointX > 0 && pointX < layerModel.width && pointY > 0 && pointY < layerModel.height

    private fun shouldBeInTheRight(coordinates: PointF) : Boolean {
        if(coordinates.x < layerModel.width / 3 && coordinates.y < layerModel.height / 2)
            return true
        return false
    }

    private fun setLayoutAlignment(right: Boolean) {
        val params : RelativeLayout.LayoutParams =
            zoomWindowShape.layoutParams as RelativeLayout.LayoutParams
        if(right) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("TAG", params.getRule(RelativeLayout.ALIGN_PARENT_RIGHT).toString())
        }

        Log.d("TAG", params.rules.contentToString())
        Log.d("TAG", params.rules.indexOf(-1).toString())
        zoomWindowShape.layoutParams = params
    }
}