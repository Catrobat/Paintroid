/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.intro;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import org.catrobat.paintroid.intro.listener.TapTargetListener;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.tools.ToolType;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.catrobat.paintroid.intro.helper.IntroAnimation.fadeOut;
import static org.catrobat.paintroid.intro.helper.UnitConverter.getDpFromInt;

abstract class TapTargetBase {
    protected final static String TAG = "TapTarget";
    private static final int RADIUS_OFFSET = 2;
    protected final Context context;
    protected final WelcomeActivity activity;
    private final LinearLayout targetView;
    private final int radius;
    final HashMap<ToolType, TapTarget> tapTargetMap = new LinkedHashMap<>();
    final View fadeView;


    TapTargetBase(LinearLayout tapTargetView, View fadeView, WelcomeActivity activity) {
        this.targetView = tapTargetView;
        this.fadeView = fadeView;
        this.activity = activity;
        this.context = activity.getBaseContext();
        this.radius = getRadius();
        
    }

    private void addClickListener(View view, final ToolType toolType) {
     /*   view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                   performClick(view, toolType);
               }
                return true;
            }
        });
*/
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performClick(view, toolType);
            }
        });
    }

    private void performClick(View view, ToolType toolType) {

        fadeOut(fadeView);
        TapTarget tapTarget = createTapTarget(toolType, view, radius);

        TapTargetView.showFor(activity, tapTarget, new TapTargetListener(fadeView));
    }

    private ToolType getToolTypeFromView(View view) {
        ToolType toolType = null;

        for (ToolType type : ToolType.values()) {
            if (view.getId() == type.getToolButtonID()) {
                toolType = type;
                break;
            }
        }

        if(toolType == null && view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                toolType = getToolTypeFromView(viewGroup.getChildAt(i));
                if(toolType != null) {
                    return toolType;
                }
            }
        }

        return toolType;
    }

    public void initTargetView() {
        for (int i = 0; i < targetView.getChildCount(); i++) {
            View view = targetView.getChildAt(i);
            ToolType toolType = getToolTypeFromView(view);
            if(toolType == null) {
                continue;
            }

            tapTargetMap.put(toolType, createTapTarget(toolType, view, radius));
            addClickListener(view, toolType);
        }
    }


    private TapTarget createTapTarget(ToolType toolType, View targetView, int radius) {
        return TapTarget
                .forView(targetView, toolType.name(),
                        context.getResources().getString(toolType.getHelpTextResource()))
                .targetRadius(radius)
                .titleTextSize(TapTargetStyle.HEADER_STYLE.getTextSize())
                .titleTextColorInt(TapTargetStyle.HEADER_STYLE.getTextColor())
                .descriptionTextColorInt(TapTargetStyle.TEXT_STYLE.getTextColor())
                .descriptionTextSize(TapTargetStyle.TEXT_STYLE.getTextSize())
                .textTypeface(TapTargetStyle.TEXT_STYLE.getTypeface())
                .cancelable(true)
                .outerCircleColor(R.color.custom_background_color)
                .targetCircleColor(R.color.color_chooser_white);
    }

    private int getRadius() {
        return getDpFromInt(targetView.getHeight(), context) / 2 - RADIUS_OFFSET;
    }
}
