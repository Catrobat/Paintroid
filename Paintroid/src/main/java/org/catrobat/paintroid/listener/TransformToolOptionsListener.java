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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import org.catrobat.paintroid.R;


public final class TransformToolOptionsListener {

    private static final String NOT_INITIALIZED_ERROR_MESSAGE = "TransformToolDialog has not been initialized. Call init() first!";
    private OnTransformToolOptionsChangedListener mOnTranformToolOptionsChangedListener;
    private Context mContext;
    private static TransformToolOptionsListener instance;
    private ImageButton mFlipVerticalButton;
    private ImageButton mFlipHorizontallButton;
    private ImageButton mRotateLeftButton;
    private ImageButton mRotateRightButton;
    private SeekBar mSizeSeekBar;
    private EditText mSizeText;

    public interface OnTransformToolOptionsChangedListener {
    }

    public TransformToolOptionsListener(Context context, View shapeToolOptionsView) {
        mContext = context;
        initializeListeners(shapeToolOptionsView);
    }

    public static TransformToolOptionsListener getInstance() {
        if (instance == null) {
            throw new IllegalStateException(NOT_INITIALIZED_ERROR_MESSAGE);
        }
        return instance;
    }

    public static void init(Context context, View transformToolOptionsView) {
        instance = new TransformToolOptionsListener(context, transformToolOptionsView);
    }

    private void initializeListeners(final View transformToolOptionsView) {


        mSizeText = (EditText) transformToolOptionsView.findViewById(R.id.transform_size_text);

        mFlipHorizontallButton = (ImageButton) transformToolOptionsView.findViewById(R.id.flip_horizontal_btn);
        mFlipVerticalButton = (ImageButton) transformToolOptionsView.findViewById(R.id.flip_vertical_btn);

        mRotateLeftButton = (ImageButton) transformToolOptionsView.findViewById(R.id.transform_rotate_left_btn);
        mRotateRightButton = (ImageButton) transformToolOptionsView.findViewById(R.id.transform_rotate_right_btn);

        mSizeSeekBar =(SeekBar) transformToolOptionsView.findViewById(R.id.transform_size_seek_bar);

        mSizeText.setText(Integer.toString(mSizeSeekBar.getProgress()) + "%");

    }

    public int getSeekBarSize() {return  mSizeSeekBar.getProgress(); }

    public ImageButton getFlipVerticalButton() {return  mFlipVerticalButton;}

    public ImageButton getRotateLeftButton() {return  mRotateLeftButton;}

    public ImageButton getRotateRightButton() {return  mRotateRightButton;}

    public ImageButton getFlipHorizontalButton() {return  mFlipHorizontallButton;}

    public SeekBar getSizeSeekBar() {return  mSizeSeekBar;}

    public void setSizeText() {
        mSizeText.setText(Integer.toString(mSizeSeekBar.getProgress()) + "%");
    }


}
