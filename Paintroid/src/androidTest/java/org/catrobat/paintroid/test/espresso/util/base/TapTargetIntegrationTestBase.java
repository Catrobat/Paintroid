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

package org.catrobat.paintroid.test.espresso.util.base;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.view.View;
import android.widget.LinearLayout;

import com.getkeepsafe.taptargetview.TapTarget;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.intro.TapTargetBase;
import org.catrobat.paintroid.intro.TapTargetBottomBar;
import org.catrobat.paintroid.intro.TapTargetTopBar;
import org.catrobat.paintroid.intro.helper.WelcomeActivityHelper;
import org.catrobat.paintroid.test.espresso.util.IntroUtils;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.paintroid.intro.TapTargetBase.getToolTypeFromView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getDescendantView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class TapTargetIntegrationTestBase {

}
