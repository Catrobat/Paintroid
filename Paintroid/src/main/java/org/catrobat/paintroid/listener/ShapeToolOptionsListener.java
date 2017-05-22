/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.listener;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolFactory;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.GeometricFillTool;


public class ShapeToolOptionsListener{

    private static final String NOT_INITIALIZED_ERROR_MESSAGE = "ShapeToolDialog has not been initialized. Call init() first!";
    private Context mContext;
    private static ShapeToolOptionsListener instance;
    private OnShapeToolOptionsChangedListener mOnShapeToolOptionsChangedListener;
    private ImageButton mSquareButton;
    private ImageButton mCircleButton;
    private ImageButton mHeartButton;
    private ImageButton mStarButton;
    private GeometricFillTool.BaseShape mShape;

    public interface OnShapeToolOptionsChangedListener {
        void setToolType(GeometricFillTool.BaseShape shape);
    }


    public ShapeToolOptionsListener(Context context, View shapeToolOptionsView) {
        mContext = context;
        initializeListeners(shapeToolOptionsView);
    }

    public static ShapeToolOptionsListener getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
        }
        return instance;
    }

    public static void init(Context context, View shapeToolOptionsView) {
        instance = new ShapeToolOptionsListener(context, shapeToolOptionsView);
    }

    private void initializeListeners(final View shapeToolOptionsView) {
        setShapeActivated(shapeToolOptionsView, GeometricFillTool.BaseShape.RECTANGLE); //set default value
        mSquareButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_square_btn);
        mSquareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.RECTANGLE;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);

            }
        });

        mCircleButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_circle_btn);
        mCircleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.OVAL;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);
            }
        });

        mHeartButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_heart_btn);
        mHeartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.HEART;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);
            }
        });

        mStarButton = (ImageButton) shapeToolOptionsView.findViewById(R.id.shapes_star_btn);
        mStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShape = GeometricFillTool.BaseShape.STAR;
                mOnShapeToolOptionsChangedListener.setToolType(mShape);
                setShapeActivated(shapeToolOptionsView, mShape);
            }
        });

    }

    private void resetShapeActivated(View shapeToolOptionsView) {
        LinearLayout shapesContainer = (LinearLayout)shapeToolOptionsView.findViewById(R.id.shapes_container);
        for (int i = 0; i < shapesContainer.getChildCount(); i++) {
            shapesContainer.getChildAt(i).setBackgroundResource(R.color.transparent);
        }
    }

    private void setShapeActivated(View shapeToolOptionsView, GeometricFillTool.BaseShape shape) {
        resetShapeActivated(shapeToolOptionsView);
        TextView shapeToolDialogTitle = (TextView)shapeToolOptionsView.findViewById(R.id.shape_tool_dialog_title);
        switch (shape) {
            case RECTANGLE:
                shapeToolOptionsView.findViewById(R.id.shapes_square_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_rect_title);
                break;
            case OVAL:
                shapeToolOptionsView.findViewById(R.id.shapes_circle_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_ellipse_title);
                break;
            case STAR:
                shapeToolOptionsView.findViewById(R.id.shapes_star_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_star_title);
                break;
            case HEART:
                shapeToolOptionsView.findViewById(R.id.shapes_heart_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                shapeToolDialogTitle.setText(R.string.shape_tool_dialog_heart_title);
                break;
            default:
                shapeToolOptionsView.findViewById(R.id.shapes_square_btn).setBackgroundResource(R.color.bottom_bar_button_activated);
                break;
        }
    }

    public void setOnShapeToolOptionsChangedListener(OnShapeToolOptionsChangedListener listener) {
        mOnShapeToolOptionsChangedListener = listener;
    }

}
