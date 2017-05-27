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

package org.catrobat.paintroid.test.junit.intro;

import android.view.View;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.intro.TapTargetBase;
import org.catrobat.paintroid.intro.TapTargetBottomBar;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.intro.helper.WelcomeActivityHelper;
import org.catrobat.paintroid.test.junit.ui.IntroTestBase;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.catrobat.paintroid.intro.TapTargetBase.getToolTypeFromView;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class TapTargetBaseTest extends IntroTestBase{

    LinearLayout targetItemView;
    protected static int admirationDelay = 400;

    protected TapTargetBottomBar getTapTargetBottomBar() {
        targetItemView = getBottomBarToolsView();

        final View fadeView = activity.findViewById(R.id.intro_tools_textview);

        TapTargetBottomBar tapTargetBottomBar =
                new TapTargetBottomBar(targetItemView, fadeView, activity, R.id.intro_tools_bottom_bar);

        return tapTargetBottomBar;
    }

    protected LinearLayout getBottomBarToolsView() {
        return (LinearLayout) getDescendantView(R.id.intro_tools_bottom_bar, R.id.tools_layout);
    }

    protected TapTargetTopBar getTapTargetTopBar() throws NoSuchFieldException, IllegalAccessException {
        targetItemView = getTopBarToolsView();

        final View fadeView = activity.findViewById(R.id.intro_possibilities_textview);

        TapTargetTopBar tapTargetTopBar =
                new TapTargetTopBar(targetItemView, fadeView, activity, R.id.intro_possibilities_bottom_bar);

        PrivateAccess.setMemberValue(TapTargetTopBar.class, tapTargetTopBar, "firsTimeSequence", false);

        return tapTargetTopBar;
    }

    protected LinearLayout getTopBarToolsView() {
        return (LinearLayout) getDescendantView(R.id.intro_possibilites_topbar, R.id.layout_top_bar);
    }

    protected HashMap<ToolType, TapTarget> getMapFromTapTarget(TapTargetBase tapTarget) throws NoSuchFieldException, IllegalAccessException {
        Object o = PrivateAccess.getMemberValue(TapTargetBase.class, tapTarget, "tapTargetMap");
        assertThat("tapTarget member is not a HashMap", o, instanceOf(HashMap.class));

        return (HashMap<ToolType, TapTarget>) o;
    }

    protected int getExpectedRadius(TapTargetBase tapTargetTopBar) throws NoSuchFieldException, IllegalAccessException {
        int radiusOffset = (int) PrivateAccess.getMemberValue(TapTargetBase.class, tapTargetTopBar, "RADIUS_OFFSET");
        float dimension = activity.getResources().getDimension(R.dimen.top_bar_height);
        return WelcomeActivityHelper.calculateTapTargetRadius(dimension, context, radiusOffset);
    }

    protected int numberOfVisibleChildern(LinearLayout layout) {
        int count = 0;
        for(int i = 0; i < layout.getChildCount(); i++) {
            if(layout.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }

        return count;
    }

    protected List<ToolType> getToolTypesFromView(LinearLayout layout) {
        List<ToolType> toolTypeList = new ArrayList<>();

        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            ToolType toolType = getToolTypeFromView(view);
            if (toolType == null) {
                continue;
            }
            toolTypeList.add(toolType);
        }

        return toolTypeList;
    }
}
